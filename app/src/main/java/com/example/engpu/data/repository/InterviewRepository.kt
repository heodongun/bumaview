package com.example.engpu.data.repository

import com.example.engpu.data.supabase.*
import com.example.engpu.ui.screens.interview.InterviewHistoryItem
import com.example.engpu.ui.screens.interview.InterviewDetailItem
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InterviewRepository {
    
    // 모든 질문 가져오기
    suspend fun getAllQuestions(): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            val questions = supabase.from("Question")
                .select()
                .decodeList<Question>()
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 카테고리별 질문 가져오기
    suspend fun getQuestionsByCategory(category: String): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            val questions = supabase.from("Question")
                .select {
                    filter {
                        eq("category", category)
                    }
                }
                .decodeList<Question>()
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 회사별 질문 가져오기
    suspend fun getQuestionsByCompany(company: String): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            val questions = supabase.from("Question")
                .select {
                    filter {
                        eq("company", company)
                    }
                }
                .decodeList<Question>()
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 모든 카테고리 목록 가져오기
    suspend fun getAllCategories(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val questions = supabase.from("Question")
                .select()
                .decodeList<Question>()
            
            val categories = questions
                .mapNotNull { it.category }
                .distinct()
                .sorted()
            
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 질문 추가
    suspend fun addQuestion(
        question: String,
        category: String? = null,
        company: String? = null,
        questionAt: Int? = null
    ): Result<Question> = withContext(Dispatchers.IO) {
        try {
            val newQuestion = Question(
                question = question,
                category = category,
                company = company,
                question_at = questionAt
            )

            // Insert with select() to get the inserted data back
            val result = supabase.from("Question")
                .insert(newQuestion) {
                    select()
                }
                .decodeSingle<Question>()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 인터뷰 기록 저장
    suspend fun saveInterview(
        userId: String,
        questionId: String,
        answer: String,
        score: Int? = null,
        feedback: String? = null,
        groupId: Int? = null
    ): Result<Interview> = withContext(Dispatchers.IO) {
        try {
            println("💾 [InterviewRepository] Saving interview:")
            println("   - userId: $userId")
            println("   - questionId: $questionId")
            println("   - answer length: ${answer.length}")
            println("   - score: $score")
            println("   - groupId: $groupId")

            val interview = Interview(
                created_at = null,  // Let database auto-generate timestamp
                user_id = userId,
                question_id = questionId,
                answer = answer,
                score = score,
                feedback = feedback,
                group_id = groupId
            )

            val result = supabase.from("Interview")
                .insert(interview) {
                    select()
                }
                .decodeSingle<Interview>()

            println("✅ [InterviewRepository] Interview saved successfully!")
            println("   - created_at: ${result.created_at}")

            Result.success(result)
        } catch (e: Exception) {
            println("❌ [InterviewRepository] Failed to save interview: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // 사용자의 모든 인터뷰 기록 조회 (그룹별로)
    suspend fun getUserInterviewHistory(userId: String): Result<List<InterviewHistoryItem>> = withContext(Dispatchers.IO) {
        try {
            // Get all interviews for the user
            val interviews = supabase.from("Interview")
                .select()
                .decodeList<Interview>()
                .filter { it.user_id == userId }
                .sortedByDescending { it.created_at }

            // Get all question details
            val questionIds = interviews.map { it.question_id }.distinct()
            val questions = questionIds.mapNotNull { questionId ->
                getQuestionById(questionId).getOrNull()
            }.associateBy { it.id }

            // Group by group_id and created_at (filter out null created_at)
            val validInterviews = interviews.filter { it.created_at != null }
            val grouped = validInterviews.groupBy { it.group_id ?: it.created_at!!.hashCode() }

            val historyItems = grouped.map { (groupId, groupInterviews) ->
                val detailItems = groupInterviews.mapNotNull { interview ->
                    val question = questions[interview.question_id]
                    question?.let {
                        InterviewDetailItem(
                            questionId = interview.question_id,
                            question = it.question,
                            answer = interview.answer ?: "",
                            score = interview.score,
                            feedback = interview.feedback
                        )
                    }
                }

                val averageScore = groupInterviews.mapNotNull { it.score }.average().toInt()
                val createdAt = groupInterviews.first().created_at!!  // Safe because filtered above

                InterviewHistoryItem(
                    groupId = groupId,
                    createdAt = createdAt,
                    questionCount = detailItems.size,
                    averageScore = averageScore,
                    interviews = detailItems
                )
            }.sortedByDescending { it.createdAt }

            Result.success(historyItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 특정 질문 조회
    private suspend fun getQuestionById(questionId: String): Result<Question> = withContext(Dispatchers.IO) {
        try {
            val question = supabase.from("Question")
                .select()
                .decodeList<Question>()
                .firstOrNull { it.id == questionId }
            
            if (question != null) {
                Result.success(question)
            } else {
                Result.failure(Exception("Question not found: $questionId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 사용자의 인터뷰 기록 가져오기
    suspend fun getUserInterviews(userId: String): Result<List<Interview>> = withContext(Dispatchers.IO) {
        try {
            val interviews = supabase.from("Interview")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Interview>()
            Result.success(interviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 특정 그룹의 인터뷰 기록 가져오기
    suspend fun getInterviewsByGroup(userId: String, groupId: Int): Result<List<Interview>> = withContext(Dispatchers.IO) {
        try {
            val interviews = supabase.from("Interview")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("group_id", groupId)
                    }
                }
                .decodeList<Interview>()
            Result.success(interviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 인터뷰 피드백 업데이트
    suspend fun updateInterviewFeedback(
        userId: String,
        questionId: String,
        createdAt: String,
        feedback: String,
        score: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.from("Interview")
                .update(
                    mapOf(
                        "feedback" to feedback,
                        "score" to score
                    )
                ) {
                    filter {
                        eq("user_id", userId)
                        eq("question_id", questionId)
                        eq("created_at", createdAt)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 질문 삭제
    suspend fun deleteQuestion(questionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.from("Question")
                .delete {
                    filter {
                        eq("id", questionId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 질문 수정
    suspend fun updateQuestion(question: Question): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.from("Question")
                .update(
                    mapOf(
                        "question" to question.question,
                        "category" to question.category,
                        "company" to question.company,
                        "question_at" to question.question_at
                    )
                ) {
                    filter {
                        eq("id", question.id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}