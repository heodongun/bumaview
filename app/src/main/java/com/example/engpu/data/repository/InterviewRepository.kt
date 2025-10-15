package com.example.engpu.data.repository

import com.example.engpu.data.supabase.*
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
            val interview = Interview(
                created_at = System.currentTimeMillis().toString(),
                user_id = userId,
                question_id = questionId,
                answer = answer,
                score = score,
                feedback = feedback,
                group_id = groupId
            )
            
            val result = supabase.from("Interview")
                .insert(interview)
                .decodeSingle<Interview>()
            
            Result.success(result)
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
}