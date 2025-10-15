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
    val categories: List<String> = emptyList(),
    val selectedInterviewQuestions: List<Question> = emptyList(),
    val interviews: List<Interview> = emptyList(),
    val interviewHistory: List<com.example.engpu.ui.screens.interview.InterviewHistoryItem> = emptyList(),
    val interviewResults: List<InterviewResult> = emptyList(),
    val uploadResult: UploadResult? = null,
    val error: String? = null
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    // Enable dev mode for debug builds (skips email verification)
    private val isDev = true  // Set to false for production

    private val authRepository = AuthRepository(devMode = isDev).apply {
        setContext(application.applicationContext)
        println("✅ [AppViewModel] AuthRepository context configured (DEV MODE: $isDev)")
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

        println("🚀 [AppViewModel] Starting signup completion")
        println("   - Name: ${data.name}")
        println("   - Email: ${data.email}")
        println("   - Job: ${data.jobPosition}")
        println("   - Time: ${data.interviewTime}")
        println("   - Password length: ${data.password.length}")

        // Validation (skip email verification in dev mode)
        if (!isDev && !data.isEmailVerified) {
            println("❌ [AppViewModel] Email not verified")
            onError("이메일 인증을 먼저 완료해주세요")
            return
        }
        if (data.password.isEmpty() || data.password.length < 8) {
            println("❌ [AppViewModel] Password too short: ${data.password.length}")
            onError("비밀번호는 8자 이상이어야 합니다")
            return
        }
        if (data.password != data.confirmPassword) {
            println("❌ [AppViewModel] Passwords don't match")
            onError("비밀번호가 일치하지 않습니다")
            return
        }

        viewModelScope.launch {
            println("📝 [AppViewModel] Calling authRepository.signUp...")
            _uiState.value = _uiState.value.copy(isLoading = true)
            authRepository.signUp(
                email = data.email,
                password = data.password,
                name = data.name,
                category = data.jobPosition,
                alarmTime = data.interviewTime
            )
                .onSuccess { authUser ->
                    println("✅ [AppViewModel] Signup successful!")

                    // Dev mode: Set current user after signup
                    if (isDev) {
                        authRepository.setDevModeCurrentUser(data.email)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    resetSignUpData()
                    onSuccess()
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Signup failed: ${exception.message}")
                    exception.printStackTrace()
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
                    // Dev mode: Set current user after signin
                    if (isDev) {
                        authRepository.setDevModeCurrentUser(email)
                    }

                    val user = authRepository.getCurrentUser()
                    println("✅ [AppViewModel] User signed in: ${user?.name} (${user?.email})")
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        currentUser = user,
                        isLoading = false,
                        error = null
                    )
                    if (user != null) {
                        println("📥 [AppViewModel] Loading user data for: ${user.id}")
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
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load questions
            interviewRepository.getAllQuestions()
                .onSuccess { questions ->
                    println("✅ [AppViewModel] Loaded ${questions.size} questions from database")
                    _uiState.value = _uiState.value.copy(
                        questions = questions,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    println("❌ [AppViewModel] Failed to load questions: ${e.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }

            // Load user's interviews
            interviewRepository.getUserInterviews(userId)
                .onSuccess { interviews ->
                    println("✅ [AppViewModel] Loaded ${interviews.size} interviews")
                    _uiState.value = _uiState.value.copy(interviews = interviews)
                }
                .onFailure { e ->
                    println("❌ [AppViewModel] Failed to load interviews: ${e.message}")
                }
        }
    }

    /**
     * Select random questions for mock interview
     */
    fun startRandomInterview(
        questionCount: Int = 5,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val allQuestions = _uiState.value.questions

                if (allQuestions.isEmpty()) {
                    // Try loading questions first
                    val result = interviewRepository.getAllQuestions()
                    if (result.isFailure) {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onError("질문을 불러올 수 없습니다. 인터넷 연결을 확인해주세요.")
                        return@launch
                    }

                    val loadedQuestions = result.getOrNull() ?: emptyList()
                    if (loadedQuestions.isEmpty()) {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onError("저장된 질문이 없습니다. 질문을 먼저 추가해주세요.")
                        return@launch
                    }

                    // Select random questions
                    val selectedQuestions = loadedQuestions.shuffled().take(questionCount)
                    _uiState.value = _uiState.value.copy(
                        questions = selectedQuestions,
                        isLoading = false
                    )
                    onSuccess()
                } else {
                    // Select random questions from existing list
                    val selectedQuestions = allQuestions.shuffled().take(questionCount)
                    _uiState.value = _uiState.value.copy(
                        questions = selectedQuestions,
                        isLoading = false
                    )
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onError(e.message ?: "면접 시작 중 오류가 발생했습니다")
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

    /**
     * Load all questions from database
     * Can be called independently without user login
     */
    fun loadAllQuestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            interviewRepository.getAllQuestions()
                .onSuccess { questions ->
                    println("✅ [AppViewModel] Loaded ${questions.size} questions from database")
                    _uiState.value = _uiState.value.copy(
                        questions = questions,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Failed to load questions: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    /**
     * Load all available categories from database
     */
    fun loadAllCategories() {
        viewModelScope.launch {
            interviewRepository.getAllCategories()
                .onSuccess { categories ->
                    println("✅ [AppViewModel] Loaded ${categories.size} categories from database")
                    _uiState.value = _uiState.value.copy(categories = categories)
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Failed to load categories: ${exception.message}")
                }
        }
    }

    /**
     * Load user's interview history
     */
    fun loadInterviewHistory() {
        val userId = _uiState.value.currentUser?.id
        if (userId == null) {
            println("❌ [AppViewModel] Cannot load interview history: No user logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            interviewRepository.getUserInterviewHistory(userId)
                .onSuccess { history ->
                    println("✅ [AppViewModel] Loaded ${history.size} interview groups from database")
                    _uiState.value = _uiState.value.copy(interviewHistory = history, isLoading = false)
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Failed to load interview history: ${exception.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = exception.message)
                }
        }
    }

    /**
     * Update existing question
     */
    fun updateQuestion(
        question: Question,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            interviewRepository.updateQuestion(question)
                .onSuccess {
                    println("✅ [AppViewModel] Question updated successfully: ${question.id}")
                    // Reload questions to refresh UI
                    loadAllQuestions()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Failed to update question: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "질문 수정 실패")
                }
        }
    }

    /**
     * Delete question from database
     */
    fun deleteQuestion(
        questionId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            interviewRepository.deleteQuestion(questionId)
                .onSuccess {
                    println("✅ [AppViewModel] Question deleted successfully: $questionId")
                    // Reload questions to refresh UI
                    loadAllQuestions()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { exception ->
                    println("❌ [AppViewModel] Failed to delete question: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                    onError(exception.message ?: "질문 삭제 실패")
                }
        }
    }

    /**
     * Start interview with specific settings
     */
    fun startInterviewWithSettings(
        questionCount: Int,
        category: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val questionsResult = if (category != null) {
                    interviewRepository.getQuestionsByCategory(category)
                } else {
                    interviewRepository.getAllQuestions()
                }

                if (questionsResult.isFailure) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onError("질문을 불러올 수 없습니다. 인터넷 연결을 확인해주세요.")
                    return@launch
                }

                val allQuestions = questionsResult.getOrNull() ?: emptyList()
                if (allQuestions.isEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onError("${category ?: "전체"} 카테고리에 질문이 없습니다.")
                    return@launch
                }

                val selectedQuestions = allQuestions.shuffled().take(questionCount)
                _uiState.value = _uiState.value.copy(
                    selectedInterviewQuestions = selectedQuestions,
                    isLoading = false
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onError(e.message ?: "면접 시작 중 오류가 발생했습니다")
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
                // Generate a unique group ID for this interview session
                val sessionGroupId = groupId ?: System.currentTimeMillis().toInt()
                println("📋 [AppViewModel] Starting interview completion with groupId: $sessionGroupId")
                println("   - User ID: $userId")
                println("   - Total answers: ${answers.size}")

                // Process all answers with AI scoring in parallel
                val results = answers.map { answer ->
                    async {
                        processInterviewAnswer(userId, answer, sessionGroupId)
                    }
                }.map { it.await() }

                val successResults = results.filterNotNull()

                println("✅ [AppViewModel] Processing complete: ${successResults.size}/${answers.size} successful")

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
                println("❌ [AppViewModel] Interview completion failed: ${e.message}")
                e.printStackTrace()
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
            println("🔄 [AppViewModel] Processing answer for question: ${answer.questionId}")
            
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
            
            println("✅ [AppViewModel] AI feedback received (${feedback.length} chars)")

            // Extract score from feedback (simple parsing)
            val score = extractScoreFromFeedback(feedback)
            println("📊 [AppViewModel] Extracted score: $score")

            // Save to database
            println("💾 [AppViewModel] Attempting to save to database...")
            val saveResult = interviewRepository.saveInterview(
                userId = userId,
                questionId = answer.questionId,
                answer = answer.answer,
                score = score,
                feedback = feedback,
                groupId = groupId
            )

            if (saveResult.isSuccess) {
                println("✅ [AppViewModel] Successfully saved to database!")
            } else {
                println("❌ [AppViewModel] Failed to save to database: ${saveResult.exceptionOrNull()?.message}")
                saveResult.exceptionOrNull()?.printStackTrace()
            }

            InterviewResult(
                questionId = answer.questionId,
                question = answer.question,
                answer = answer.answer,
                score = score,
                feedback = feedback
            )
        } catch (e: Exception) {
            println("❌ Error processing answer for ${answer.questionId}: ${e.message}")
            e.printStackTrace()
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
                    println("✅ [AppViewModel] Excel upload successful: ${result.successCount} added, ${result.failureCount} failed")

                    // Reload ALL questions from database
                    interviewRepository.getAllQuestions()
                        .onSuccess { questions ->
                            println("✅ [AppViewModel] Reloaded ${questions.size} questions from database")
                            _uiState.value = _uiState.value.copy(
                                uploadResult = result,
                                questions = questions,
                                isLoading = false
                            )
                            onSuccess(result)
                        }
                        .onFailure { e ->
                            println("❌ [AppViewModel] Failed to reload questions: ${e.message}")
                            _uiState.value = _uiState.value.copy(
                                uploadResult = result,
                                isLoading = false
                            )
                            onSuccess(result)
                        }
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