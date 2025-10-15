package com.example.engpu.navigation

sealed class Screen(val route: String) {
    // Authentication Screens
    object Onboarding : Screen("onboarding")
    object SignUp1 : Screen("signup1")
    object SignUp2 : Screen("signup2")
    object SignUp3 : Screen("signup3")
    object SignUp4 : Screen("signup4")
    object SignUp5 : Screen("signup5")
    object SignUp6 : Screen("signup6")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    
    // Main App Screens
    object Home : Screen("home")
    object Repository : Screen("repository")
    object Interview : Screen("interview")
    object Profile : Screen("profile")
    
    // Additional Screens
    object InterviewSetup : Screen("interview_setup")
    object InterviewPractice : Screen("interview_practice")
    object InterviewResult : Screen("interview_result")
    object InterviewHistory : Screen("interview_history")
    object QuestionRepository : Screen("question_repository")
}
