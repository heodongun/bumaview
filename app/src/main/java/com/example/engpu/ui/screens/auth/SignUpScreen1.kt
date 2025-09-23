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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*

@Composable
fun SignUpScreen1(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    
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
            Spacer(modifier = Modifier.height(50.dp))
            
            // Progress Indicator with animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                StudyWithProgressIndicator(
                    progress = 1f / 6f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title with slide animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
            ) {
                Text(
                    text = "이름을 입력하세요",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    modifier = Modifier.offset(x = 0.dp, y = 0.dp)
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
                    text = "실제 이름이 아니여도 괜찮아요!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    modifier = Modifier.offset(x = 0.dp, y = 0.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(35.dp))
            
            // Input field with slide animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                StudyWithTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "이름을 입력해주세요 (예: 허동운)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .offset(x = 2.dp, y = 0.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Login link with fade animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 600))
            ) {
                Text(
                    text = "이미 계정이 있나요? 로그인",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack,
                    modifier = Modifier
                        .offset(x = 2.dp, y = 0.dp)
                        .clickable { onLoginClick() }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Button with slide up animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 500)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
            ) {
                if (name.isNotBlank()) {
                    StudyWithButton(
                        text = "다음",
                        onClick = { onNextClick(name) },
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
        
        // Back Button with fade animation
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
