package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    onResetPasswordClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    var shouldSendEmail by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // 이메일 전송 처리
    LaunchedEffect(shouldSendEmail) {
        if (shouldSendEmail) {
            delay(2000) // 2초 지연으로 이메일 전송 시뮬레이션
            isLoading = false
            showSuccessMessage = true
            shouldSendEmail = false
            onResetPasswordClick(email) // 콜백 호출
        }
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
                    text = "비밀번호 찾기",
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
                    text = "가입하신 이메일 주소를 입력하시면\n비밀번호 재설정 링크를 보내드립니다.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    lineHeight = 18.sp
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
                        showSuccessMessage = false
                    },
                    placeholder = "이메일을 입력해주세요",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Success Message with slide animation
            AnimatedVisibility(
                visible = showSuccessMessage,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400)),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "비밀번호 재설정 링크가 이메일로 전송되었습니다.\n이메일을 확인해주세요.",
                        fontSize = 12.sp,
                        color = StudyWithBlack,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Reset Password Button with slide up animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 500)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
            ) {
                when {
                    email.isNotBlank() && email.contains("@") && !isLoading -> {
                        StudyWithButton(
                            text = "비밀번호 재설정 링크 보내기",
                            onClick = { 
                                isLoading = true
                                shouldSendEmail = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            backgroundColor = Color.White.copy(alpha = 0.21f),
                            textColor = StudyWithOrange
                        )
                    }
                    isLoading -> {
                        StudyWithButton(
                            text = "전송 중...",
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = false
                        )
                    }
                    else -> {
                        StudyWithInactiveButton(
                            text = "비밀번호 재설정 링크 보내기",
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
