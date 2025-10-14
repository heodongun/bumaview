package com.example.engpu.data.repository

import android.content.Context
import android.net.Uri
import com.example.engpu.data.supabase.Question
import com.example.engpu.data.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 전체 기능 검증을 위한 Repository
 *
 * 기능:
 * - Supabase 연결 검증
 * - Excel 업로드 기능 검증
 * - Gemini AI 연동 검증
 * - 통합 검증 리포트 생성
 */
class ValidationRepository(private val context: Context) {

    private val interviewRepository = InterviewRepository()
    private val excelRepository = ExcelRepository(context)
    private val geminiRepository = GeminiRepository()

    /**
     * 전체 시스템 검증 수행
     *
     * @return ValidationReport 검증 결과 리포트
     */
    suspend fun performFullValidation(
        testExcelUri: Uri? = null
    ): ValidationReport = withContext(Dispatchers.IO) {
        val report = ValidationReport()

        // 1. Supabase 연결 검증
        report.supabaseValidation = validateSupabase()

        // 2. Excel 업로드 기능 검증 (테스트 파일 제공 시)
        if (testExcelUri != null) {
            report.excelValidation = validateExcelUpload(testExcelUri)
        }

        // 3. Gemini AI 연동 검증
        report.geminiValidation = validateGeminiAPI()

        // 4. 통합 검증 (Question 조회 → Gemini 피드백)
        report.integrationValidation = validateIntegration()

        report
    }

    /**
     * Supabase 연결 및 CRUD 검증
     */
    private suspend fun validateSupabase(): ValidationResult {
        return try {
            // DB 연결 확인: 질문 개수 조회
            val questionsResult = interviewRepository.getAllQuestions()

            if (questionsResult.isSuccess) {
                val count = questionsResult.getOrNull()?.size ?: 0
                ValidationResult(
                    isSuccess = true,
                    message = "Supabase 연결 성공: $count 개의 질문 확인",
                    details = mapOf(
                        "connection" to "OK",
                        "question_count" to count.toString(),
                        "timestamp" to System.currentTimeMillis().toString()
                    )
                )
            } else {
                ValidationResult(
                    isSuccess = false,
                    message = "Supabase 질문 조회 실패: ${questionsResult.exceptionOrNull()?.message}",
                    error = questionsResult.exceptionOrNull()?.message
                )
            }
        } catch (e: Exception) {
            ValidationResult(
                isSuccess = false,
                message = "Supabase 연결 실패",
                error = e.message
            )
        }
    }

    /**
     * Excel 업로드 기능 검증
     */
    private suspend fun validateExcelUpload(uri: Uri): ValidationResult {
        return try {
            val uploadResult = excelRepository.uploadExcelToQuestions(uri)

            if (uploadResult.isSuccess) {
                val result = uploadResult.getOrNull()!!
                ValidationResult(
                    isSuccess = result.isSuccess,
                    message = "Excel 업로드 완료: ${result.successCount}/${result.totalRows} 성공",
                    details = mapOf(
                        "total_rows" to result.totalRows.toString(),
                        "success_count" to result.successCount.toString(),
                        "failure_count" to result.failureCount.toString(),
                        "success_rate" to "${(result.successRate * 100).toInt()}%"
                    ),
                    error = if (result.errors.isNotEmpty()) result.errors.joinToString("\n") else null
                )
            } else {
                ValidationResult(
                    isSuccess = false,
                    message = "Excel 업로드 실패",
                    error = uploadResult.exceptionOrNull()?.message
                )
            }
        } catch (e: Exception) {
            ValidationResult(
                isSuccess = false,
                message = "Excel 파일 처리 실패",
                error = e.message
            )
        }
    }

    /**
     * Gemini AI 연동 검증
     */
    private suspend fun validateGeminiAPI(): ValidationResult {
        return try {
            val testPrompt = "안녕하세요. 테스트 메시지입니다."
            val startTime = System.currentTimeMillis()
            val result = geminiRepository.sendMessage(testPrompt)
            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime

            if (result.isSuccess) {
                val response = result.getOrNull()!!
                ValidationResult(
                    isSuccess = true,
                    message = "Gemini AI 연동 성공",
                    details = mapOf(
                        "response_time" to "${responseTime}ms",
                        "response_length" to response.content.length.toString(),
                        "response_preview" to response.content.take(100)
                    )
                )
            } else {
                ValidationResult(
                    isSuccess = false,
                    message = "Gemini AI 호출 실패",
                    error = result.exceptionOrNull()?.message
                )
            }
        } catch (e: Exception) {
            ValidationResult(
                isSuccess = false,
                message = "Gemini AI 연동 오류",
                error = e.message
            )
        }
    }

    /**
     * 통합 검증: Question 조회 → Gemini 피드백
     */
    private suspend fun validateIntegration(): ValidationResult {
        return try {
            // 1. Question 조회
            val questionsResult = interviewRepository.getAllQuestions()
            if (questionsResult.isFailure) {
                return ValidationResult(
                    isSuccess = false,
                    message = "통합 검증 실패: Question 조회 불가",
                    error = questionsResult.exceptionOrNull()?.message
                )
            }

            val questions = questionsResult.getOrNull()!!
            if (questions.isEmpty()) {
                return ValidationResult(
                    isSuccess = true,
                    message = "통합 검증: Question 테이블이 비어있습니다",
                    details = mapOf("warning" to "테스트를 위해 질문을 추가하세요")
                )
            }

            // 2. 첫 번째 질문으로 Gemini 피드백 요청
            val testQuestion = questions.first()
            val testAnswer = "테스트 답변입니다. 이것은 자동 검증을 위한 샘플 답변입니다."

            val feedbackResult = geminiRepository.getFeedbackForInterview(
                question = testQuestion.question,
                answer = testAnswer
            )

            if (feedbackResult.isSuccess) {
                val feedback = feedbackResult.getOrNull()!!
                ValidationResult(
                    isSuccess = true,
                    message = "통합 검증 성공: Question → Gemini 피드백 정상 작동",
                    details = mapOf(
                        "test_question" to testQuestion.question,
                        "test_answer" to testAnswer,
                        "feedback_preview" to feedback.content.take(200)
                    )
                )
            } else {
                ValidationResult(
                    isSuccess = false,
                    message = "통합 검증 실패: Gemini 피드백 생성 오류",
                    error = feedbackResult.exceptionOrNull()?.message
                )
            }
        } catch (e: Exception) {
            ValidationResult(
                isSuccess = false,
                message = "통합 검증 오류",
                error = e.message
            )
        }
    }

    /**
     * 리소스 해제
     */
    fun cleanup() {
        geminiRepository.close()
    }
}

/**
 * 검증 결과 데이터 클래스
 */
data class ValidationResult(
    val isSuccess: Boolean,
    val message: String,
    val details: Map<String, String>? = null,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 전체 검증 리포트
 */
data class ValidationReport(
    var supabaseValidation: ValidationResult? = null,
    var excelValidation: ValidationResult? = null,
    var geminiValidation: ValidationResult? = null,
    var integrationValidation: ValidationResult? = null
) {
    val overallSuccess: Boolean
        get() = listOfNotNull(
            supabaseValidation,
            geminiValidation,
            integrationValidation
        ).all { it.isSuccess }

    val successCount: Int
        get() = listOfNotNull(
            supabaseValidation,
            excelValidation,
            geminiValidation,
            integrationValidation
        ).count { it.isSuccess }

    val totalTests: Int
        get() = listOfNotNull(
            supabaseValidation,
            excelValidation,
            geminiValidation,
            integrationValidation
        ).size

    fun toMarkdown(): String = buildString {
        appendLine("# 🔍 Supabase MCP 기반 Kotlin 앱 전체 검증 리포트")
        appendLine()
        appendLine("**생성 시간**: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())}")
        appendLine("**전체 결과**: ${if (overallSuccess) "✅ 성공" else "❌ 실패"} ($successCount/$totalTests 통과)")
        appendLine()
        appendLine("---")
        appendLine()

        supabaseValidation?.let { result ->
            appendLine("## 1️⃣ Supabase MCP 연결 검증")
            appendLine()
            appendLine("**상태**: ${if (result.isSuccess) "✅ 성공" else "❌ 실패"}")
            appendLine("**메시지**: ${result.message}")
            result.details?.let { details ->
                appendLine()
                appendLine("**세부 정보**:")
                details.forEach { (key, value) ->
                    appendLine("- $key: `$value`")
                }
            }
            result.error?.let {
                appendLine()
                appendLine("**오류**: `$it`")
            }
            appendLine()
        }

        excelValidation?.let { result ->
            appendLine("## 2️⃣ Excel 업로드 → Question 테이블 자동 반영")
            appendLine()
            appendLine("**상태**: ${if (result.isSuccess) "✅ 성공" else "❌ 실패"}")
            appendLine("**메시지**: ${result.message}")
            result.details?.let { details ->
                appendLine()
                appendLine("**세부 정보**:")
                details.forEach { (key, value) ->
                    appendLine("- $key: `$value`")
                }
            }
            result.error?.let {
                appendLine()
                appendLine("**오류**:")
                appendLine("```")
                appendLine(it)
                appendLine("```")
            }
            appendLine()
        }

        geminiValidation?.let { result ->
            appendLine("## 3️⃣ Gemini AI API 연동")
            appendLine()
            appendLine("**상태**: ${if (result.isSuccess) "✅ 성공" else "❌ 실패"}")
            appendLine("**메시지**: ${result.message}")
            result.details?.let { details ->
                appendLine()
                appendLine("**세부 정보**:")
                details.forEach { (key, value) ->
                    appendLine("- $key: `$value`")
                }
            }
            result.error?.let {
                appendLine()
                appendLine("**오류**: `$it`")
            }
            appendLine()
        }

        integrationValidation?.let { result ->
            appendLine("## 4️⃣ 통합 검증 (Question → Gemini 피드백)")
            appendLine()
            appendLine("**상태**: ${if (result.isSuccess) "✅ 성공" else "❌ 실패"}")
            appendLine("**메시지**: ${result.message}")
            result.details?.let { details ->
                appendLine()
                appendLine("**세부 정보**:")
                details.forEach { (key, value) ->
                    appendLine("- $key:")
                    appendLine("  ```")
                    appendLine("  $value")
                    appendLine("  ```")
                }
            }
            result.error?.let {
                appendLine()
                appendLine("**오류**: `$it`")
            }
            appendLine()
        }

        appendLine("---")
        appendLine()
        appendLine("## 📊 종합 평가")
        appendLine()
        appendLine("| 항목 | 상태 |")
        appendLine("|------|------|")
        supabaseValidation?.let {
            appendLine("| Supabase 연결 | ${if (it.isSuccess) "✅" else "❌"} |")
        }
        excelValidation?.let {
            appendLine("| Excel 업로드 | ${if (it.isSuccess) "✅" else "❌"} |")
        }
        geminiValidation?.let {
            appendLine("| Gemini AI 연동 | ${if (it.isSuccess) "✅" else "❌"} |")
        }
        integrationValidation?.let {
            appendLine("| 통합 검증 | ${if (it.isSuccess) "✅" else "❌"} |")
        }
        appendLine()
        appendLine("**최종 판정**: ${if (overallSuccess) "🎉 모든 기능 정상 작동" else "⚠️ 일부 기능 오류 발생"}")
    }
}
