package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.theme.*

@Composable
fun SignUpScreen5(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedTime by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    
    val timeOptions = listOf(
        "30초" to "30초",
        "1분" to "1분",
        "1분 30초" to "1분 30초",
        "2분" to "2분",
        "3분" to "3분"
    )
    
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
            
            // Progress Indicator - 5/6 단계
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                StudyWithProgressIndicator(
                    progress = 5f / 6f,
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
                    text = "면접 질문 시간을 설정하세요",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    lineHeight = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
            ) {
                Text(
                    text = "각 질문마다 답변할 시간을 선택해주세요.\n나중에 변경할 수 있어요!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Time Options
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    timeOptions.forEachIndexed { index, (display, value) ->
                        TimeOptionCard(
                            text = display,
                            isSelected = selectedTime == value,
                            onClick = { selectedTime = value },
                            delay = index * 100
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next Button
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 600)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 600))
            ) {
                if (selectedTime.isNotBlank()) {
                    StudyWithButton(
                        text = "다음",
                        onClick = { onNextClick(selectedTime) },
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

@Composable
private fun TimeOptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    delay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(500)
        ) + fadeIn(animationSpec = tween(500))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) StudyWithBlack else Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 4.dp else 2.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) StudyWithYellow else StudyWithBlack
                )
                
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 18.sp,
                            color = StudyWithYellow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
