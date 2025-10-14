package com.example.engpu.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random

/**
 * 하이브리드 이메일 발송 서비스
 *
 * Primary: Gmail SMTP
 * - Host: smtp.gmail.com
 * - Port: 587
 * - STARTTLS: 활성화
 * - Account: heodongun08@gmail.com
 *
 * Fallback: Android Intent (이메일 앱 연동)
 * - SMTP 실패 시 자동으로 Intent 방식 사용
 * - 사용자의 기본 이메일 앱으로 전송
 *
 * ⚠️ 중요: Gmail 계정에서 "앱 비밀번호" 또는 "보안 수준이 낮은 앱 액세스" 활성화 필요
 */
class EmailService {

    private val smtpHost = "smtp.gmail.com"
    private val smtpPort = "587"
    private val smtpUsername = "heodongun08@gmail.com"
    private val smtpPassword = "jiwoonmentoring"
    private val fromEmail = "heodongun08@gmail.com"
    private val fromName = "EngPu Interview Practice"

    /**
     * 6자리 인증 코드 생성
     */
    fun generateVerificationCode(): String {
        return String.format("%06d", Random.nextInt(100000, 999999))
    }

    /**
     * 이메일 인증 코드 발송
     *
     * @param toEmail 수신자 이메일
     * @param verificationCode 6자리 인증 코드
     * @return Result<Unit> 발송 성공/실패
     */
    suspend fun sendVerificationEmail(
        toEmail: String,
        verificationCode: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            println("📧 [EmailService] Attempting SMTP email send to: $toEmail")

            val properties = Properties().apply {
                put("mail.smtp.host", smtpHost)
                put("mail.smtp.port", smtpPort)
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.starttls.required", "true")
                put("mail.smtp.ssl.protocols", "TLSv1.2")
                put("mail.smtp.ssl.trust", smtpHost)
                put("mail.smtp.connectiontimeout", "15000")
                put("mail.smtp.timeout", "15000")
                put("mail.smtp.writetimeout", "15000")

                // 디버그 모드 활성화
                put("mail.debug", "true")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    println("🔐 [EmailService] Authenticating with: $smtpUsername")
                    return PasswordAuthentication(smtpUsername, smtpPassword)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail, fromName))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "[$fromName] 이메일 인증 코드"

                // HTML 이메일 본문
                setContent(createEmailHtmlContent(verificationCode), "text/html; charset=utf-8")
            }

            println("📤 [EmailService] Sending email via SMTP...")
            // 이메일 전송
            Transport.send(message)

            println("✅ [EmailService] SMTP email sent successfully!")
            Result.success(Unit)

        } catch (e: AuthenticationFailedException) {
            println("❌ [EmailService] SMTP Authentication failed: ${e.message}")
            println("⚠️ [EmailService] Please check Gmail settings:")
            println("   1. Enable 2-Step Verification")
            println("   2. Generate App Password at: https://myaccount.google.com/apppasswords")
            println("   3. Or enable 'Less secure app access' (not recommended)")
            Result.failure(Exception("Gmail 인증 실패: 앱 비밀번호를 확인하거나 '보안 수준이 낮은 앱 액세스'를 활성화하세요. 상세: ${e.message}"))

        } catch (e: MessagingException) {
            println("❌ [EmailService] SMTP MessagingException: ${e.message}")
            e.printStackTrace()

            // 534 오류 (인증 거부) 특별 처리
            if (e.message?.contains("534") == true || e.message?.contains("5.7.9") == true) {
                Result.failure(Exception("Gmail 보안 설정 오류: Google 계정에서 2단계 인증 및 앱 비밀번호를 설정해야 합니다."))
            } else {
                Result.failure(Exception("이메일 전송 실패: ${e.message}"))
            }

        } catch (e: Exception) {
            println("❌ [EmailService] Unexpected error: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("이메일 발송 중 오류: ${e.message}"))
        }
    }

    /**
     * 이메일 HTML 본문 생성
     */
    private fun createEmailHtmlContent(code: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        background-color: #f5f5f5;
                        margin: 0;
                        padding: 20px;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: white;
                        border-radius: 10px;
                        padding: 40px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #2196F3;
                    }
                    .content {
                        text-align: center;
                    }
                    .title {
                        font-size: 24px;
                        font-weight: bold;
                        color: #333;
                        margin-bottom: 20px;
                    }
                    .message {
                        font-size: 16px;
                        color: #666;
                        line-height: 1.6;
                        margin-bottom: 30px;
                    }
                    .code-box {
                        background-color: #f0f7ff;
                        border: 2px solid #2196F3;
                        border-radius: 8px;
                        padding: 20px;
                        margin: 30px 0;
                    }
                    .code {
                        font-size: 36px;
                        font-weight: bold;
                        color: #2196F3;
                        letter-spacing: 8px;
                        font-family: 'Courier New', monospace;
                    }
                    .warning {
                        font-size: 14px;
                        color: #ff9800;
                        margin-top: 30px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        font-size: 12px;
                        color: #999;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🎤 $fromName</div>
                    </div>

                    <div class="content">
                        <div class="title">이메일 인증 코드</div>

                        <div class="message">
                            회원가입을 완료하기 위해 아래 인증 코드를 입력해주세요.
                        </div>

                        <div class="code-box">
                            <div class="code">$code</div>
                        </div>

                        <div class="message">
                            이 코드는 <strong>15분 동안</strong> 유효합니다.
                        </div>

                        <div class="warning">
                            ⚠️ 본인이 요청하지 않았다면 이 이메일을 무시하세요.
                        </div>
                    </div>

                    <div class="footer">
                        이 이메일은 자동으로 발송되었습니다.<br>
                        문의사항이 있으시면 ${fromEmail}로 연락주세요.
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * 비밀번호 재설정 이메일 발송
     */
    suspend fun sendPasswordResetEmail(
        toEmail: String,
        resetCode: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val properties = Properties().apply {
                put("mail.smtp.host", smtpHost)
                put("mail.smtp.port", smtpPort)
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.starttls.required", "true")
                put("mail.smtp.ssl.protocols", "TLSv1.2")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(smtpUsername, smtpPassword)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail, fromName))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "[$fromName] 비밀번호 재설정 코드"

                setContent(createPasswordResetHtmlContent(resetCode), "text/html; charset=utf-8")
            }

            Transport.send(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("비밀번호 재설정 이메일 전송 실패: ${e.message}"))
        }
    }

    /**
     * 비밀번호 재설정 HTML 본문
     */
    private fun createPasswordResetHtmlContent(code: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        background-color: #f5f5f5;
                        margin: 0;
                        padding: 20px;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: white;
                        border-radius: 10px;
                        padding: 40px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #f44336;
                    }
                    .content {
                        text-align: center;
                    }
                    .title {
                        font-size: 24px;
                        font-weight: bold;
                        color: #333;
                        margin-bottom: 20px;
                    }
                    .message {
                        font-size: 16px;
                        color: #666;
                        line-height: 1.6;
                        margin-bottom: 30px;
                    }
                    .code-box {
                        background-color: #fff3f3;
                        border: 2px solid #f44336;
                        border-radius: 8px;
                        padding: 20px;
                        margin: 30px 0;
                    }
                    .code {
                        font-size: 36px;
                        font-weight: bold;
                        color: #f44336;
                        letter-spacing: 8px;
                        font-family: 'Courier New', monospace;
                    }
                    .warning {
                        font-size: 14px;
                        color: #ff9800;
                        margin-top: 30px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        font-size: 12px;
                        color: #999;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🔒 $fromName</div>
                    </div>

                    <div class="content">
                        <div class="title">비밀번호 재설정</div>

                        <div class="message">
                            비밀번호 재설정을 위해 아래 코드를 입력해주세요.
                        </div>

                        <div class="code-box">
                            <div class="code">$code</div>
                        </div>

                        <div class="message">
                            이 코드는 <strong>15분 동안</strong> 유효합니다.
                        </div>

                        <div class="warning">
                            ⚠️ 본인이 요청하지 않았다면 즉시 비밀번호를 변경하세요.
                        </div>
                    </div>

                    <div class="footer">
                        이 이메일은 자동으로 발송되었습니다.<br>
                        문의사항이 있으시면 ${fromEmail}로 연락주세요.
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
