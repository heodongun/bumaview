package com.example.engpu.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Intent 기반 이메일 발송 서비스
 *
 * SMTP가 실패할 경우 사용자의 기본 이메일 앱으로 이메일 전송
 * - Gmail, Outlook, Yahoo 등 사용자가 설치한 이메일 앱 사용
 * - 앱 비밀번호나 보안 설정 불필요
 * - 사용자가 직접 "전송" 버튼을 눌러야 함
 */
class IntentEmailSender(private val context: Context) {

    /**
     * Intent를 통한 이메일 발송
     *
     * @param toEmail 수신자 이메일
     * @param subject 이메일 제목
     * @param body 이메일 본문 (HTML 지원)
     * @return Result<Unit> 성공/실패
     */
    suspend fun sendEmailViaIntent(
        toEmail: String,
        subject: String,
        body: String
    ): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            println("📱 [IntentEmailSender] Attempting to send email via Intent")

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)

                // FLAG_ACTIVITY_NEW_TASK 추가 (서비스/백그라운드에서 실행 가능)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // 이메일 앱이 설치되어 있는지 확인
            if (intent.resolveActivity(context.packageManager) != null) {
                println("✅ [IntentEmailSender] Email app found, launching intent")
                context.startActivity(intent)
                println("✅ [IntentEmailSender] Email intent launched successfully")
                Result.success(Unit)
            } else {
                println("❌ [IntentEmailSender] No email app found on device")
                Result.failure(Exception("이메일 앱이 설치되어 있지 않습니다"))
            }

        } catch (e: Exception) {
            println("❌ [IntentEmailSender] Error launching email intent: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("이메일 앱 실행 실패: ${e.message}"))
        }
    }

    /**
     * 인증 코드 이메일 전송 (Intent 방식)
     *
     * @param toEmail 수신자 이메일
     * @param verificationCode 6자리 인증 코드
     */
    suspend fun sendVerificationEmail(
        toEmail: String,
        verificationCode: String
    ): Result<Unit> {
        val subject = "[EngPu Interview Practice] 이메일 인증 코드"
        val body = createEmailTextContent(verificationCode)

        return sendEmailViaIntent(toEmail, subject, body)
    }

    /**
     * 인증 이메일 텍스트 본문 생성
     */
    private fun createEmailTextContent(code: String): String {
        return """
            안녕하세요, EngPu Interview Practice입니다.

            이메일 인증을 완료하기 위해 아래 인증 코드를 입력해주세요.

            ━━━━━━━━━━━━━━━━━━━━
            인증 코드: $code
            ━━━━━━━━━━━━━━━━━━━━

            이 코드는 15분 동안 유효합니다.

            ⚠️ 본인이 요청하지 않았다면 이 이메일을 무시하세요.

            문의사항이 있으시면 답장 부탁드립니다.

            감사합니다.
            EngPu Interview Practice Team
        """.trimIndent()
    }

    /**
     * 비밀번호 재설정 이메일 전송 (Intent 방식)
     */
    suspend fun sendPasswordResetEmail(
        toEmail: String,
        resetCode: String
    ): Result<Unit> {
        val subject = "[EngPu Interview Practice] 비밀번호 재설정 코드"
        val body = """
            안녕하세요, EngPu Interview Practice입니다.

            비밀번호 재설정을 위해 아래 코드를 입력해주세요.

            ━━━━━━━━━━━━━━━━━━━━
            재설정 코드: $resetCode
            ━━━━━━━━━━━━━━━━━━━━

            이 코드는 15분 동안 유효합니다.

            ⚠️ 본인이 요청하지 않았다면 즉시 비밀번호를 변경하세요.

            문의사항이 있으시면 답장 부탁드립니다.

            감사합니다.
            EngPu Interview Practice Team
        """.trimIndent()

        return sendEmailViaIntent(toEmail, subject, body)
    }
}
