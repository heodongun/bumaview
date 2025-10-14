package com.example.engpu.data.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Gemini AI API 연동을 위한 Repository
 *
 * 기능:
 * - Gemini AI API 호출
 * - 자동 Retry 및 Timeout 처리
 * - JSON 응답 파싱
 */
class GeminiRepository {

    private val apiUrl = "https://genai-app-koreanchatconversation-1-1757577861794-612486206975.us-central1.run.app/chat"
    private val apiKey = "dd28t8g7kefh6qo3"

    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000 // 30초
            connectTimeoutMillis = 15000 // 15초
            socketTimeoutMillis = 30000  // 30초
        }

        // Retry 설정
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
    }

    /**
     * Gemini AI에게 메시지 전송하고 응답 받기
     *
     * @param prompt 전송할 메시지
     * @return Result<GeminiResponse> AI 응답
     */
    suspend fun sendMessage(prompt: String): Result<GeminiResponse> = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = httpClient.post(apiUrl) {
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(prompt = prompt))
            }

            if (response.status.isSuccess()) {
                val geminiResponse = response.body<GeminiResponse>()
                Result.success(geminiResponse)
            } else {
                Result.failure(Exception("API 호출 실패: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gemini AI 연동 오류: ${e.message}"))
        }
    }

    /**
     * 인터뷰 답변에 대한 피드백 요청
     *
     * @param question 면접 질문
     * @param answer 사용자 답변
     * @return Result<GeminiResponse> AI 피드백
     */
    suspend fun getFeedbackForInterview(
        question: String,
        answer: String
    ): Result<GeminiResponse> {
        val prompt = """
            다음 면접 질문에 대한 답변을 평가하고 피드백을 제공해주세요.

            질문: $question
            답변: $answer

            다음 형식으로 피드백을 작성해주세요:
            1. 답변의 강점
            2. 개선이 필요한 부분
            3. 추천 점수 (1-10)
            4. 개선 제안
        """.trimIndent()

        return sendMessage(prompt)
    }

    /**
     * 질문 카테고리에 맞는 모범 답안 생성
     *
     * @param question 면접 질문
     * @param category 질문 카테고리
     * @return Result<GeminiResponse> AI 모범 답안
     */
    suspend fun generateBestAnswer(
        question: String,
        category: String?
    ): Result<GeminiResponse> {
        val categoryContext = category?.let { "[$it] 분야의 " } ?: ""
        val prompt = """
            다음 ${categoryContext}면접 질문에 대한 모범 답안을 작성해주세요.

            질문: $question

            답변은 다음 요소를 포함해야 합니다:
            1. 명확한 구조 (도입, 본론, 결론)
            2. 구체적인 예시
            3. 핵심 역량 강조
        """.trimIndent()

        return sendMessage(prompt)
    }

    /**
     * HttpClient 리소스 해제
     */
    fun close() {
        httpClient.close()
    }
}

/**
 * Gemini API 요청 데이터 모델
 */
@Serializable
data class GeminiRequest(
    val prompt: String
)

/**
 * Gemini API 응답 데이터 모델
 */
@Serializable
data class GeminiResponse(
    val response: String? = null,
    val message: String? = null,
    val error: String? = null
) {
    val content: String
        get() = response ?: message ?: error ?: "응답을 받을 수 없습니다"
}
