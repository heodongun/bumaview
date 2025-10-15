package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engpu.viewmodel.AppViewModel
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*
import android.widget.Toast

@Composable
fun LoginScreen(
    appViewModel: AppViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyWithYellow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(98.dp))
            
            // Title with slide animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
            ) {
                Text(
                    text = "로그인",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle with slide animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
            ) {
                Text(
                    text = "이메일과 비밀번호를 입력해주세요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
            }
            
            Spacer(modifier = Modifier.height(50.dp))
            
            // Email Input Field with slide animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                StudyWithTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        showErrorMessage = false
                    },
                    placeholder = "이메일을 입력해주세요.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(11.dp))
            
            // Sign Up Link with fade animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 500))
            ) {
                Text(
                    text = "계정이 없나요? 회원가입",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Password Input Field with slide animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(800, delayMillis = 600)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 600))
            ) {
                StudyWithTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        showErrorMessage = false
                    },
                    placeholder = "비밀번호를 입력해주세요.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    isPassword = true
                )
            }
            
            Spacer(modifier = Modifier.height(11.dp))
            
            // Forgot Password Link with fade animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 700))
            ) {
                Text(
                    text = "비밀번호를 잃어버리셨나요? 비밀번호 찾기",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onForgotPasswordClick() }
                )
            }
            
            // Error Message with slide animation
            AnimatedVisibility(
                visible = showErrorMessage,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400)),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.Red
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Login Button with slide up animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 800)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 800))
            ) {
                when {
                    email.isNotBlank() && password.isNotBlank() && !uiState.isLoading -> {
                        StudyWithButton(
                            text = "로그인",
                            onClick = {
                                showErrorMessage = false
                                appViewModel.signIn(
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess()
                                    },
                                    onError = { error ->
                                        errorMessage = when {
                                            error.contains("Invalid") -> "이메일 또는 비밀번호가 올바르지 않습니다."
                                            error.contains("Network") -> "네트워크 연결을 확인해주세요."
                                            else -> "로그인 중 오류가 발생했습니다."
                                        }
                                        showErrorMessage = true
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            backgroundColor = Color.White.copy(alpha = 0.21f),
                            textColor = StudyWithOrange
                        )
                    }
                    uiState.isLoading -> {
                        StudyWithButton(
                            text = "로그인 중...",
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = false
                        )
                    }
                    else -> {
                        StudyWithInactiveButton(
                            text = "로그인",
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = false
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
        }
        
        // Back Button with fade animation
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 100))
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(start = 13.dp, top = 46.dp)
                    .size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = StudyWithBlack
                )
            }
        }
    }
}
