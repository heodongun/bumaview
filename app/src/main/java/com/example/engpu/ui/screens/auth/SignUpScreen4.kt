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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun SignUpScreen4(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var verificationCode by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    var isCodeSent by remember { mutableStateOf(true) } // 이미 코드가 발송되었다고 가정
    var remainingTime by remember { mutableStateOf(180) } // 3분 = 180초
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // 타이머 효과
    LaunchedEffect(isCodeSent) {
        if (isCodeSent && remainingTime > 0) {
            kotlinx.coroutines.delay(1000L)
            remainingTime -= 1
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
            Spacer(modifier = Modifier.height(50.dp))
            
            // Progress Indicator - 4/6 단계
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                StudyWithProgressIndicator(
                    progress = 4f / 6f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
            ) {
                Text(
                    text = "확인코드를 입력하세요",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle with timer
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
            ) {
                Column {
                    Text(
                        text = "이메일로 발송된 6자리 확인코드를 입력해주세요.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (remainingTime > 0) {
                        Text(
                            text = "남은 시간: ${remainingTime / 60}:${String.format("%02d", remainingTime % 60)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = StudyWithOrange
                        )
                    } else {
                        Text(
                            text = "시간이 만료되었습니다. 코드를 다시 발송해주세요.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = androidx.compose.ui.graphics.Color.Red
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(35.dp))
            
            // Verification Code Input Field
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                StudyWithTextField(
                    value = verificationCode,
                    onValueChange = { 
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            verificationCode = it
                        }
                    },
                    placeholder = "6자리 확인코드를 입력해주세요",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Resend Code Link
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 600))
            ) {
                Text(
                    text = "코드를 받지 못하셨나요? 다시 보내기",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { 
                        // 코드 재발송 로직
                        remainingTime = 180
                        isCodeSent = true
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next Button
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 500)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
            ) {
                if (verificationCode.length == 6) {
                    StudyWithButton(
                        text = "다음",
                        onClick = { onNextClick(verificationCode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        backgroundColor = StudyWithBlack,
                        textColor = StudyWithYellow
                    )
                } else {
                    StudyWithInactiveButton(
                        text = "다음",
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        enabled = false
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
        }
        
        // Back Button
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 100))
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
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
