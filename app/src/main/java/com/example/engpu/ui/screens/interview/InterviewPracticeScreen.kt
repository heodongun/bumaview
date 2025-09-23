package com.example.engpu.ui.screens.interview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun InterviewPracticeScreen(
    onBackClick: () -> Unit,
    onCompleteInterview: () -> Unit
) {
    var currentQuestion by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }
    var showQuestion by remember { mutableStateOf(false) }
    var isInterviewStarted by remember { mutableStateOf(false) }
    var shouldAnimateQuestion by remember { mutableStateOf(false) }
    
    val questions = listOf(
        "자기소개를 간단히 해주세요.",
        "이 회사에 지원한 이유는 무엇인가요?",
        "본인의 장점과 단점은 무엇인가요?",
        "5년 후의 목표는 무엇인가요?",
        "마지막으로 하고 싶은 말이 있나요?"
    )
    
    // Recording timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingTime += 1
            }
        } else {
            recordingTime = 0
        }
    }
    
    // 질문 애니메이션 처리
    LaunchedEffect(shouldAnimateQuestion) {
        if (shouldAnimateQuestion) {
            showQuestion = false
            delay(200)
            showQuestion = true
            shouldAnimateQuestion = false
        }
    }
    
    LaunchedEffect(Unit) {
        delay(500)
        showQuestion = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = StudyWithBlack
                    )
                }
                
                Text(
                    text = "모의면접 ${currentQuestion + 1}/${questions.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.width(42.dp))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = (currentQuestion + 1).toFloat() / questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = StudyWithYellow,
                trackColor = StudyWithGray.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Question Card
            AnimatedVisibility(
                visible = showQuestion,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(800)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StudyWithYellow.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = questions[currentQuestion],
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Recording Status
            if (isRecording) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pulseAnimation by rememberInfiniteTransition(label = "pulse").animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                Color.Red,
                                RoundedCornerShape(50)
                            )
                            .graphicsLayer {
                                scaleX = pulseAnimation
                                scaleY = pulseAnimation
                            }
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "녹음 중 ${recordingTime / 60}:${String.format("%02d", recordingTime % 60)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Record/Stop Button
                FloatingActionButton(
                    onClick = {
                        isRecording = !isRecording
                        if (!isInterviewStarted) {
                            isInterviewStarted = true
                        }
                    },
                    modifier = Modifier.size(64.dp),
                    containerColor = if (isRecording) Color.Red else StudyWithYellow,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "녹음 중지" else "녹음 시작",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Next/Complete Button
                StudyWithButton(
                    text = if (currentQuestion == questions.size - 1) "면접 완료" else "다음 질문",
                    onClick = {
                        if (currentQuestion == questions.size - 1) {
                            onCompleteInterview()
                        } else {
                            currentQuestion += 1
                            shouldAnimateQuestion = true
                            isRecording = false
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(55.dp),
                    backgroundColor = StudyWithBlack,
                    textColor = StudyWithYellow,
                    enabled = isInterviewStarted
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
