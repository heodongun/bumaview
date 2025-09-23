package com.example.engpu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.engpu.data.SignUpData
import com.example.engpu.navigation.Screen
import com.example.engpu.ui.screens.auth.*
import com.example.engpu.ui.screens.main.*
import com.example.engpu.ui.theme.StudyWithTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyWithTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyWithApp()
                }
            }
        }
    }
}

@Composable
fun StudyWithApp() {
    var currentScreen by remember { mutableStateOf(Screen.Onboarding.route) }
    var signUpData by remember { mutableStateOf(SignUpData()) }
    
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
                    signUpData = signUpData.copy(name = name)
                    currentScreen = Screen.SignUp2.route
                },
                onBackClick = { currentScreen = Screen.Onboarding.route },
                onLoginClick = { currentScreen = Screen.Login.route }
            )
        }
        
        Screen.SignUp2.route -> {
            SignUpScreen2(
                onNextClick = { jobPosition ->
                    signUpData = signUpData.copy(jobPosition = jobPosition)
                    currentScreen = Screen.SignUp3.route
                },
                onBackClick = { currentScreen = Screen.SignUp1.route }
            )
        }
        
        Screen.SignUp3.route -> {
            SignUpScreen3(
                onNextClick = { email ->
                    signUpData = signUpData.copy(email = email)
                    currentScreen = Screen.SignUp4.route
                },
                onBackClick = { currentScreen = Screen.SignUp2.route }
            )
        }
        
        Screen.SignUp4.route -> {
            SignUpScreen4(
                onNextClick = { verificationCode ->
                    signUpData = signUpData.copy(verificationCode = verificationCode)
                    currentScreen = Screen.SignUp5.route
                },
                onBackClick = { currentScreen = Screen.SignUp3.route }
            )
        }
        
        Screen.SignUp5.route -> {
            SignUpScreen5(
                onNextClick = { interviewTime ->
                    signUpData = signUpData.copy(interviewTime = interviewTime)
                    currentScreen = Screen.SignUp6.route
                },
                onBackClick = { currentScreen = Screen.SignUp4.route }
            )
        }
        
        Screen.SignUp6.route -> {
            SignUpScreen6(
                onCompleteClick = { 
                    // 회원가입 완료 후 로그인 화면으로 이동
                    currentScreen = Screen.Login.route 
                },
                onBackClick = { currentScreen = Screen.SignUp5.route },
                userName = signUpData.name
            )
        }
        
        Screen.Login.route -> {
            LoginScreen(
                onLoginClick = { currentScreen = Screen.Home.route },
                onSignUpClick = { currentScreen = Screen.SignUp1.route },
                onBackClick = { currentScreen = Screen.Onboarding.route }
            )
        }
        
        Screen.Home.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = signUpData.name.ifEmpty { "사용자" },
                userEmail = signUpData.email.ifEmpty { "user@example.com" }
            )
        }
        
        Screen.Repository.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = signUpData.name.ifEmpty { "사용자" },
                userEmail = signUpData.email.ifEmpty { "user@example.com" }
            )
        }
        
        Screen.Interview.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = signUpData.name.ifEmpty { "사용자" },
                userEmail = signUpData.email.ifEmpty { "user@example.com" }
            )
        }
        
        Screen.Profile.route -> {
            MainAppContent(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                userName = signUpData.name.ifEmpty { "사용자" },
                userEmail = signUpData.email.ifEmpty { "user@example.com" }
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
