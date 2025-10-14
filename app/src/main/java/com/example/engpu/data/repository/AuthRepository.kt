package com.example.engpu.data.repository

import android.content.Context
import com.example.engpu.data.supabase.*
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AuthRepository(private val devMode: Boolean = false) {

    private val emailVerificationRepository = EmailVerificationRepository()

    /**
     * Context 설정 (Intent 기반 이메일 fallback용)
     */
    fun setContext(context: Context) {
        emailVerificationRepository.setContext(context)
        println("✅ [AuthRepository] Context configured (DEV MODE: $devMode)")
    }

    // 이메일 인증 코드 전송 (Gmail SMTP 사용)
    suspend fun sendVerificationCode(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (devMode) {
                println("🔧 [DEV MODE] Skipping email verification send")
                return@withContext Result.success(true)
            }

            println("📧 [AuthRepository] Starting verification code send to: $email")

            // 기존 Supabase OTP 대신 커스텀 SMTP 사용
            val codeResult = emailVerificationRepository.sendVerificationCode(email)

            if (codeResult.isSuccess) {
                println("✅ [AuthRepository] Verification code sent successfully")
                Result.success(true)
            } else {
                val error = codeResult.exceptionOrNull() ?: Exception("인증 코드 전송 실패")
                println("❌ [AuthRepository] Failed to send code: ${error.message}")
                Result.failure(error)
            }
        } catch (e: Exception) {
            println("❌ [AuthRepository] Exception in sendVerificationCode: ${e.message}")
            Result.failure(e)
        }
    }

    // 이메일 인증 코드 확인 (커스텀 검증)
    suspend fun verifyEmail(
        email: String,
        code: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (devMode) {
                println("🔧 [DEV MODE] Auto-approving verification code")
                return@withContext Result.success(true)
            }

            println("🔐 [AuthRepository] Verifying code for email: $email")

            // 커스텀 인증 코드 검증
            val verifyResult = emailVerificationRepository.verifyCode(email, code)

            if (verifyResult.isSuccess) {
                println("✅ [AuthRepository] Code verification successful")
                Result.success(true)
            } else {
                val error = verifyResult.exceptionOrNull() ?: Exception("인증 코드 확인 실패")
                println("❌ [AuthRepository] Code verification failed: ${error.message}")
                Result.failure(error)
            }
        } catch (e: Exception) {
            println("❌ [AuthRepository] Exception in verifyEmail: ${e.message}")
            Result.failure(e)
        }
    }
    
    // 회원가입 (이메일 인증 후)
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        category: String,
        alarmTime: String // "14:30" 형식의 시간
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            // 1. Supabase Auth에 사용자 생성
            val authResult = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            
            // 2. 실제 유저 ID 가져오기
            val userId: String = authResult?.id ?: throw Exception("User ID not found")
            val createdAt: String = authResult?.createdAt?.toString() 
                ?: System.currentTimeMillis().toString()
            
            // 3. 알람 시간 포맷팅 (HH:mm -> timestamp)
            val alarmTimestamp: String = formatAlarmTime(alarmTime)
            
            // 4. User 테이블에 추가 정보 저장
            val user: User = User(
                id = userId,
                name = name,
                category = category,
                email = email,
                grade = "USER",
                alarm_at = alarmTimestamp
            )
            
            supabase.from("User").insert(user)
            
            Result.success(AuthUser(
                id = userId,
                email = email,
                created_at = createdAt
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 로그인 (이메일 인증 확인 포함)
    suspend fun signIn(
        email: String,
        password: String
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            // 1. 이메일 인증 여부 확인 (dev mode에서는 skip)
            if (!devMode) {
                val isVerified = emailVerificationRepository.isEmailVerified(email)
                if (!isVerified) {
                    return@withContext Result.failure(Exception("이메일 인증이 필요합니다"))
                }
            } else {
                println("🔧 [DEV MODE] Skipping email verification check")
            }

            // 2. Supabase Auth 로그인
            val signInResult = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // 3. 현재 세션에서 실제 유저 정보 가져오기
            val session = supabase.auth.currentSessionOrNull()
            val userId: String = session?.user?.id
                ?: throw Exception("User ID not found")
            val createdAt: String = session?.user?.createdAt?.toString()
                ?: System.currentTimeMillis().toString()

            Result.success(AuthUser(
                id = userId,
                email = email,
                created_at = createdAt
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 로그아웃
    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 현재 사용자 정보 가져오기
    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        try {
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                val userId: String = session.user?.id ?: return@withContext null
                
                val result: User? = supabase.from("User")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<User>()
                
                result
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // 세션 확인
    suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = supabase.auth.currentSessionOrNull()
            session != null
        } catch (e: Exception) {
            false
        }
    }
    
    // 사용자 정보 업데이트
    suspend fun updateUser(
        userId: String,
        name: String? = null,
        category: String? = null,
        alarmTime: String? = null // "14:30" 형식
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val updateMap: MutableMap<String, Any?> = mutableMapOf()
            name?.let { updateMap["name"] = it }
            category?.let { updateMap["category"] = it }
            alarmTime?.let { 
                val formattedTime: String = formatAlarmTime(it)
                updateMap["alarm_at"] = formattedTime 
            }
            
            supabase.from("User")
                .update(updateMap) {
                    filter {
                        eq("id", userId)
                    }
                }
            
            val updatedUser: User = supabase.from("User")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<User>()
            
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 비밀번호 재설정 이메일 전송
    suspend fun sendPasswordResetEmail(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabase.auth.resetPasswordForEmail(email)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 알람 시간 포맷팅 헬퍼 함수
    private fun formatAlarmTime(timeString: String): String {
        return try {
            // "14:30" -> timestamp 형식으로 변환
            val time: LocalTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            time.toString()
        } catch (e: Exception) {
            timeString // 변환 실패시 원본 반환
        }
    }
    
    // 이메일 중복 확인
    suspend fun checkEmailExists(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val users: List<User> = supabase.from("User")
                .select {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeList<User>()
            
            Result.success(users.isNotEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}