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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*

@Composable
fun SignUpScreen3(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
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
            
            // Progress Indicator - 3/6 단계 with enhanced progress bar
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Box(modifier = Modifier.offset(x = 6.dp)) { // x: 30 from figma
                    StudyWithProgressIndicator(
                        progress = 3f / 6f,
                        modifier = Modifier
                            .width(190.dp) // matching figma width
                            .height(4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title - matching figma "메일주소를 입력하세요"
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
            ) {
                Text(
                    text = "메일주소를 입력하세요",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    lineHeight = 28.sp // matching figma height: 56dp for 2 lines
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle - matching figma "거의 다 왔어요! 조금만 힘내세요"
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
            ) {
                Text(
                    text = "거의 다 왔어요! 조금만 힘내세요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    modifier = Modifier.offset(x = 3.dp) // x: 27 from figma
                )
            }
            
            Spacer(modifier = Modifier.height(21.dp)) // y: 184 - 140 - 20 = 24dp, adjusted
            
            // Email Input Field - matching figma position
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                StudyWithTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "메일 주소를 입력해주세요 (예: qwe@qwe.q)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next Button - matching figma position
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 500)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
            ) {
                if (email.isNotBlank() && email.contains("@")) {
                    StudyWithButton(
                        text = "다음",
                        onClick = { onNextClick(email) },
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
