package com.example.engpu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engpu.data.repository.AuthRepository
import com.example.engpu.data.repository.InterviewRepository
import com.example.engpu.data.repository.GeminiRepository
import com.example.engpu.data.repository.ExcelRepository
import com.example.engpu.data.repository.UploadResult
import com.example.engpu.data.SignUpData
import com.example.engpu.data.supabase.User
import com.example.engpu.data.supabase.Question
import com.example.engpu.data.supabase.Interview
import com.example.engpu.ui.screens.interview.InterviewAnswer
import com.example.engpu.ui.screens.interview.InterviewResult
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

data class AppUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val questions: List<Question> = emptyList(),
    val interviews: List<Interview> = emptyList(),
    val interviewResults: List<InterviewResult> = emptyList(),
    val uploadResult: UploadResult? = null,
    val error: String? = null
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository().apply {
        setContext(application.applicationContext)
        println("✅ [AppViewModel] AuthRepository context configured")
    }
    private val interviewRepository = InterviewRepository()
    private val geminiRepository = GeminiRepository()
    private val excelRepository = ExcelRepository(application.applicationContext)
    
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    
    // SignUp flow state management
    private val _signUpData = MutableStateFlow(SignUpData())
    val signUpData: StateFlow<SignUpData> = _signUpData.asStateFlow()
    
    fun updateSignUpData(update: (SignUpData) -> SignUpData) {
        _signUpData.value = update(_signUpData.value)
    }
    
    fun resetSignUpData() {
        _signUpData.value = SignUpData()
    }
    
    // Email verification with enhanced logging
    fun sendVerificationCode(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            println("🚀 [AppViewModel] Starting email verification code send for: $email")
            _uiState.value = _uiState.value.copy(isLoading = true)

            authRepository.sendVerificationCode(email)
                .onSuccess {
                    println("✅ [AppViewModel] Email verification code sent successfully to: $email")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    onSuccess()
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Failed to send verification code: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "인증 코드 전송 실패")
                }
        }
    }
    
    fun verifyEmailCode(email: String, code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            println("🔍 [AppViewModel] Verifying email code for: $email with code: $code")
            _uiState.value = _uiState.value.copy(isLoading = true)

            authRepository.verifyEmail(email, code)
                .onSuccess {
                    println("✅ [AppViewModel] Email verification successful for: $email")
                    _signUpData.value = _signUpData.value.copy(isEmailVerified = true)
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    onSuccess()
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Email verification failed: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "인증 코드 확인 실패")
                }
        }
    }
    
    // Enhanced signUp with alarm time
    fun completeSignUp(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val data = _signUpData.value
        
        // Validation
        if (!data.isEmailVerified) {
            onError("이메일 인증을 먼저 완료해주세요")
            return
        }
        if (data.password.isEmpty() || data.password.length < 6) {
            onError("비밀번호는 6자 이상이어야 합니다")
            return
        }
        if (data.password != data.confirmPassword) {
            onError("비밀번호가 일치하지 않습니다")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            authRepository.signUp(
                email = data.email,
                password = data.password,
                name = data.name,
                category = data.jobPosition,
                alarmTime = data.interviewTime
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    resetSignUpData()
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "회원가입 실패")
                }
        }
    }
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val isLoggedIn = authRepository.isLoggedIn()
            if (isLoggedIn) {
                val user = authRepository.getCurrentUser()
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    currentUser = user,
                    isLoading = false
                )
                if (user != null) {
                    loadUserData(user.id)
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false,
                    isLoading = false
                )
            }
        }
    }
    
    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            authRepository.signIn(email, password)
                .onSuccess { authUser ->
                    val user = authRepository.getCurrentUser()
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        currentUser = user,
                        isLoading = false,
                        error = null
                    )
                    if (user != null) {
                        loadUserData(user.id)
                    }
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "로그인 실패")
                }
        }
    }
    
    fun signUp(
        email: String, 
        password: String, 
        name: String, 
        category: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            authRepository.signUp(
                email, password, name, category,
                alarmTime = ""
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "회원가입 실패")
                }
        }
    }
    
    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
                .onSuccess {
                    _uiState.value = AppUiState()
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message
                    )
                }
        }
    }
    
    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            // Load questions
            interviewRepository.getAllQuestions()
                .onSuccess { questions ->
                    _uiState.value = _uiState.value.copy(questions = questions)
                }
            
            // Load user's interviews
            interviewRepository.getUserInterviews(userId)
                .onSuccess { interviews ->
                    _uiState.value = _uiState.value.copy(interviews = interviews)
                }
        }
    }
    
    fun loadQuestionsByCategory(category: String) {
        viewModelScope.launch {
            interviewRepository.getQuestionsByCategory(category)
                .onSuccess { questions ->
                    _uiState.value = _uiState.value.copy(questions = questions)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message
                    )
                }
        }
    }
    
    fun saveInterview(
        questionId: String,
        answer: String,
        score: Int? = null,
        feedback: String? = null,
        groupId: Int? = null,
        onSuccess: () -> Unit
    ) {
        val userId = _uiState.value.currentUser?.id ?: return
        
        viewModelScope.launch {
            interviewRepository.saveInterview(
                userId = userId,
                questionId = questionId,
                answer = answer,
                score = score,
                feedback = feedback,
                groupId = groupId
            ).onSuccess {
                loadUserData(userId)
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = exception.message
                )
            }
        }
    }
    
    fun addQuestion(
        question: String,
        category: String? = null,
        company: String? = null,
        questionAt: Int? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            interviewRepository.addQuestion(
                question = question,
                category = category,
                company = company,
                questionAt = questionAt
            ).onSuccess {
                _uiState.value.currentUser?.let { user ->
                    loadUserData(user.id)
                }
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = exception.message
                )
            }
        }
    }
    
    fun updateUserProfile(
        name: String? = null,
        category: String? = null,
        alarmAt: String? = null,
        onSuccess: () -> Unit
    ) {
        val userId = _uiState.value.currentUser?.id ?: return
        
        viewModelScope.launch {
            authRepository.updateUser(
                userId = userId,
                name = name,
                category = category
            ).onSuccess { updatedUser ->
                _uiState.value = _uiState.value.copy(
                    currentUser = updatedUser
                )
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = exception.message
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Complete interview with AI scoring and feedback
     */
    fun completeInterview(
        answers: List<InterviewAnswer>,
        groupId: Int? = null,
        onSuccess: (List<InterviewResult>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = _uiState.value.currentUser?.id
        if (userId == null) {
            onError("사용자 정보를 찾을 수 없습니다")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Process all answers with AI scoring in parallel
                val results = answers.map { answer ->
                    async {
                        processInterviewAnswer(userId, answer, groupId)
                    }
                }.map { it.await() }

                val successResults = results.filterNotNull()

                if (successResults.size == answers.size) {
                    _uiState.value = _uiState.value.copy(
                        interviewResults = successResults,
                        isLoading = false
                    )
                    onSuccess(successResults)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onError("일부 답변 처리에 실패했습니다 (${successResults.size}/${answers.size})")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
                onError(e.message ?: "면접 완료 처리 실패")
            }
        }
    }

    /**
     * Process single interview answer with AI scoring
     */
    private suspend fun processInterviewAnswer(
        userId: String,
        answer: InterviewAnswer,
        groupId: Int?
    ): InterviewResult? {
        return try {
            // Get AI feedback
            val feedbackResult = geminiRepository.getFeedbackForInterview(
                question = answer.question,
                answer = answer.answer
            )

            if (feedbackResult.isFailure) {
                println("❌ AI feedback failed for question ${answer.questionId}: ${feedbackResult.exceptionOrNull()?.message}")
                // Use fallback scoring
                return createFallbackResult(userId, answer, groupId)
            }

            val geminiResponse = feedbackResult.getOrNull()
            val feedback = geminiResponse?.content ?: "피드백을 생성할 수 없습니다"

            // Extract score from feedback (simple parsing)
            val score = extractScoreFromFeedback(feedback)

            // Save to database
            interviewRepository.saveInterview(
                userId = userId,
                questionId = answer.questionId,
                answer = answer.answer,
                score = score,
                feedback = feedback,
                groupId = groupId
            )

            InterviewResult(
                questionId = answer.questionId,
                question = answer.question,
                answer = answer.answer,
                score = score,
                feedback = feedback
            )
        } catch (e: Exception) {
            println("❌ Error processing answer for ${answer.questionId}: ${e.message}")
            createFallbackResult(userId, answer, groupId)
        }
    }

    /**
     * Create fallback result when AI fails
     */
    private suspend fun createFallbackResult(
        userId: String,
        answer: InterviewAnswer,
        groupId: Int?
    ): InterviewResult {
        val score = calculateBasicScore(answer.answer)
        val feedback = "답변이 저장되었습니다. 상세한 피드백은 추후 제공됩니다."

        interviewRepository.saveInterview(
            userId = userId,
            questionId = answer.questionId,
            answer = answer.answer,
            score = score,
            feedback = feedback,
            groupId = groupId
        )

        return InterviewResult(
            questionId = answer.questionId,
            question = answer.question,
            answer = answer.answer,
            score = score,
            feedback = feedback
        )
    }

    /**
     * Extract score from AI feedback
     */
    private fun extractScoreFromFeedback(feedback: String): Int {
        // Look for patterns like "점수: 8" or "8/10" or "추천 점수: 7"
        val scorePatterns = listOf(
            Regex("""추천 점수[:\s]*(\d+)"""),
            Regex("""점수[:\s]*(\d+)"""),
            Regex("""(\d+)\s*/\s*10"""),
            Regex("""score[:\s]*(\d+)""", RegexOption.IGNORE_CASE)
        )

        for (pattern in scorePatterns) {
            val match = pattern.find(feedback)
            if (match != null) {
                val score = match.groupValues[1].toIntOrNull()
                if (score != null && score in 1..10) {
                    return score
                }
            }
        }

        // Fallback: calculate based on answer length
        return calculateBasicScore(feedback)
    }

    /**
     * Basic score calculation based on answer characteristics
     */
    private fun calculateBasicScore(answer: String): Int {
        val words = answer.trim().split(Regex("\\s+"))
        return when {
            words.size < 10 -> 4
            words.size < 30 -> 6
            words.size < 50 -> 7
            words.size < 100 -> 8
            else -> 9
        }.coerceIn(1, 10)
    }

    /**
     * Upload Excel file with interview questions
     */
    fun uploadExcelQuestions(
        uri: Uri,
        onSuccess: (UploadResult) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            excelRepository.uploadExcelToQuestions(uri)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        uploadResult = result,
                        isLoading = false
                    )
                    // Reload questions
                    _uiState.value.currentUser?.let { user ->
                        loadUserData(user.id)
                    }
                    onSuccess(result)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "Excel 업로드 실패")
                }
        }
    }

    /**
     * Get interview history for current user
     */
    fun loadInterviewHistory(onComplete: () -> Unit = {}) {
        val userId = _uiState.value.currentUser?.id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            interviewRepository.getUserInterviews(userId)
                .onSuccess { interviews ->
                    _uiState.value = _uiState.value.copy(
                        interviews = interviews,
                        isLoading = false
                    )
                    onComplete()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        geminiRepository.close()
    }
}