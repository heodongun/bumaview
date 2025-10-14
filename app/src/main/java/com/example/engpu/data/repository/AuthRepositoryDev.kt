package com.example.engpu.data.repository

import com.example.engpu.data.supabase.*
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 개발용 AuthRepository - 이메일 인증 없이 바로 회원가입
 */
class AuthRepositoryDev {
    
    // 회원가입 (이메일 인증 없이)
    suspend fun signUpDirect(
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
            val createdAt: String = System.currentTimeMillis().toString()
            
            // 3. User 테이블에 추가 정보 저장
            val user: User = User(
                id = userId,
                name = name,
                category = category,
                email = email,
                grade = "USER",
                password = "", // 비밀번호는 Auth에서 관리
                alarm_at = alarmTime
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
    
    // 로그인
    suspend fun signIn(
        email: String,
        password: String
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val signInResult = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            // 현재 세션에서 실제 유저 정보 가져오기
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
}