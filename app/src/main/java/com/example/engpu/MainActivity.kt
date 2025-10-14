package com.example.engpu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engpu.data.SignUpData
import com.example.engpu.data.repository.AuthRepository
import com.example.engpu.data.supabase.User
import com.example.engpu.navigation.Screen
import com.example.engpu.ui.screens.auth.*
import com.example.engpu.ui.screens.main.*
import com.example.engpu.ui.screens.interview.*
import com.example.engpu.ui.theme.StudyWithTheme
import com.example.engpu.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyWithTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyWithApp(appViewModel)
                }
            }
        }
    }
}

@Composable
fun StudyWithApp(appViewModel: AppViewModel) {
    val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val signUpData by appViewModel.signUpData.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(Screen.Onboarding.route) }

    // 로그인 상태에 따라 초기 화면 결정
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && currentScreen == Screen.Onboarding.route) {
            currentScreen = Screen.Home.route
        }
    }
    
    when (currentScreen) {
        Screen.Onboarding.route -> {
            OnboardingScreen(
                onSignUpClick = { currentScreen = Screen.SignUp1.route },
                onLoginClick = { currentScreen = Screen.Login.route }
            )
        }
        
        // 회원가입 플로우: 이름 → 희망직무 → 이메일 → 확인코드 → 면접시간 → 비밀번호
        Screen.SignUp1.route -> {
            SignUpScreen1(
                onNextClick = { name ->
                    appViewModel.updateSignUpData { it.copy(name = name) }
                    currentScreen = Screen.SignUp2.route
                },
                onBackClick = { currentScreen = Screen.Onboarding.route },
                onLoginClick = { currentScreen = Screen.Login.route }
            )
        }
        
        Screen.SignUp2.route -> {
            SignUpScreen2(
                onNextClick = { jobPosition ->
                    appViewModel.updateSignUpData { it.copy(jobPosition = jobPosition) }
                    currentScreen = Screen.SignUp3.route
                },
                onBackClick = { currentScreen = Screen.SignUp1.route }
            )
        }
        
        Screen.SignUp3.route -> {
            SignUpScreen3(
                onNextClick = { email ->
                    appViewModel.updateSignUpData { it.copy(email = email) }
                    // Send verification code
                    appViewModel.sendVerificationCode(
                        email = email,
                        onSuccess = { currentScreen = Screen.SignUp4.route },
                        onError = { /* Error handled by ViewModel */ }
                    )
                },
                onBackClick = { currentScreen = Screen.SignUp2.route }
            )
        }
        
        Screen.SignUp4.route -> {
            SignUpScreen4(
                email = signUpData.email,
                onNextClick = { verificationCode ->
                    appViewModel.verifyEmailCode(
                        email = signUpData.email,
                        code = verificationCode,
                        onSuccess = { currentScreen = Screen.SignUp5.route },
                        onError = { /* Error handled by ViewModel */ }
                    )
                },
                onBackClick = { currentScreen = Screen.SignUp3.route }
            )
        }
        
        Screen.SignUp5.route -> {
            SignUpScreen5(
                onNextClick = { interviewTime ->
                    appViewModel.updateSignUpData { it.copy(interviewTime = interviewTime) }
                    currentScreen = Screen.SignUp6.route
                },
                onBackClick = { currentScreen = Screen.SignUp4.route }
            )
        }
        
        Screen.SignUp6.route -> {
            SignUpScreen6(
                onCompleteClick = { password, confirmPassword ->
                    appViewModel.updateSignUpData { 
                        it.copy(
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    }
                    appViewModel.completeSignUp(
                        onSuccess = { currentScreen = Screen.Login.route },
                        onError = { /* Error handled by ViewModel */ }
                    )
                },
                onBackClick = { currentScreen = Screen.SignUp5.route },
                userName = signUpData.name
            )
        }

        Screen.Login.route -> {
            LoginScreen(
                onLoginClick = { currentScreen = Screen.Home.route },
                onSignUpClick = { currentScreen = Screen.SignUp1.route },
                onForgotPasswordClick = { currentScreen = Screen.ForgotPassword.route },
                onBackClick = { currentScreen = Screen.Onboarding.route }
            )
        }
        
        Screen.ForgotPassword.route -> {
            ForgotPasswordScreen(
                onResetPasswordClick = { email ->
                    // 비밀번호 재설정 이메일 전송 로직
                    currentScreen = Screen.Login.route
                },
                onBackClick = { currentScreen = Screen.Login.route }
            )
        }

        Screen.Home.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = uiState.currentUser?.name ?: "사용자",
                userEmail = uiState.currentUser?.email ?: "user@example.com"
            )
        }
        
        Screen.Repository.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = uiState.currentUser?.name ?: "사용자",
                userEmail = uiState.currentUser?.email ?: "user@example.com"
            )
        }
        
        Screen.Interview.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = uiState.currentUser?.name ?: "사용자",
                userEmail = uiState.currentUser?.email ?: "user@example.com"
            )
        }
        
        Screen.Profile.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = uiState.currentUser?.name ?: "사용자",
                userEmail = uiState.currentUser?.email ?: "user@example.com"
            )
        }
        
        Screen.InterviewPractice.route -> {
            InterviewPracticeScreen(
                questions = uiState.questions,
                onBackClick = { currentScreen = Screen.Home.route },
                onCompleteInterview = { answers ->
                    appViewModel.completeInterview(
                        answers = answers,
                        onSuccess = { results ->
                            currentScreen = Screen.InterviewResult.route
                        },
                        onError = { error ->
                            println("❌ Interview completion error: $error")
                            currentScreen = Screen.Home.route
                        }
                    )
                },
                onSaveAnswer = { questionId, answer ->
                    // Individual answer saving (kept for backward compatibility)
                    // Main saving happens in completeInterview
                }
            )
        }

        Screen.InterviewResult.route -> {
            InterviewResultScreen(
                results = uiState.interviewResults,
                onGoHome = { currentScreen = Screen.Home.route }
            )
        }
    }
}

@Composable
fun MainAppContent(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    userName: String,
    userEmail: String
) {
    when (currentScreen) {
        Screen.Home.route -> {
            HomeScreen(
                currentRoute = currentScreen,
                onNavigate = onNavigate,
                userName = userName,
                onProfileClick = { onNavigate(Screen.Profile.route) }
            )
        }
        Screen.Repository.route -> {
            RepositoryScreen(
                currentRoute = currentScreen,
                onNavigate = onNavigate
            )
        }
        Screen.Interview.route -> {
            InterviewScreen(
                currentRoute = currentScreen,
                onNavigate = onNavigate
            )
        }
        Screen.Profile.route -> {
            ProfileScreen(
                currentRoute = currentScreen,
                onNavigate = onNavigate,
                userName = userName,
                userEmail = userEmail,
                onLogout = { onNavigate(Screen.Onboarding.route) }
            )
        }
    }
}
