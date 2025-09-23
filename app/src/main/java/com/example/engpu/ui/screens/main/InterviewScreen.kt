package com.example.engpu.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.BottomNavigationBar
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun InterviewScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var currentQuestion by remember { mutableStateOf("자기소개를 해주세요") }
    var recordingTime by remember { mutableStateOf(0) }
    var hasRecording by remember { mutableStateOf(false) }
    var completedQuestions by remember { mutableStateOf(0) }
    
    // Recording timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingTime++
            }
        } else {
            if (recordingTime > 0) {
                hasRecording = true
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudyWithYellow)
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = "모의면접",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "실제 면접처럼 연습해보세요",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithBlack
                    )
                    
                    Text(
                        text = "완료: ${completedQuestions}개",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithBlack
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "면접 질문 ${completedQuestions + 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = currentQuestion,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(60.dp))
                
                // Recording Status
                if (isRecording) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🔴 녹음 중...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Red
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "답변을 말씀해주세요",
                            fontSize = 14.sp,
                            color = StudyWithGray
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${recordingTime}초",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyWithBlack
                        )
                    }
                } else {
                    Text(
                        text = if (hasRecording) "녹음이 완료되었습니다" else "아래 버튼을 눌러 답변을 녹음하세요",
                        fontSize = 14.sp,
                        color = StudyWithGray,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Recording Button
                FloatingActionButton(
                    onClick = { 
                        if (isRecording) {
                            isRecording = false
                        } else {
                            isRecording = true
                            recordingTime = 0
                        }
                    },
                    modifier = Modifier.size(80.dp),
                    containerColor = if (isRecording) Color.Red else StudyWithYellow,
                    contentColor = if (isRecording) Color.White else StudyWithBlack
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "녹음 중지" else "녹음 시작",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StudyWithButton(
                        text = "다음 질문",
                        onClick = { 
                            currentQuestion = getRandomQuestion()
                            isRecording = false
                            hasRecording = false
                            recordingTime = 0
                            completedQuestions++
                        },
                        modifier = Modifier.weight(1f),
                        backgroundColor = StudyWithBlack,
                        textColor = Color.White
                    )
                    
                    StudyWithButton(
                        text = if (hasRecording) "답변 재생" else "답변 없음",
                        onClick = { 
                            if (hasRecording) {
                                // Handle playback
                            }
                        },
                        modifier = Modifier.weight(1f),
                        backgroundColor = if (hasRecording) Color.White else Color.Gray,
                        textColor = if (hasRecording) StudyWithBlack else Color.White,
                        enabled = hasRecording
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StudyWithButton(
                    text = "면접 완료",
                    onClick = { 
                        // Show completion dialog or navigate to results
                    },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = StudyWithOrange,
                    textColor = Color.White
                )
            }
        }
        
        // Bottom Navigation
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    }
}

private fun getRandomQuestion(): String {
    val questions = listOf(
        "자기소개를 해주세요",
        "지원 동기는 무엇인가요?",
        "본인의 장점과 단점을 말해주세요",
        "5년 후 자신의 모습은?",
        "왜 이 회사를 선택했나요?",
        "팀워크 경험에 대해 말해주세요",
        "스트레스 관리 방법은?",
        "실패 경험과 극복 과정은?",
        "리더십 경험이 있나요?",
        "회사에 기여할 수 있는 부분은?",
        "가장 성취감을 느꼈던 경험은?",
        "갈등 상황을 해결한 경험이 있나요?",
        "새로운 기술을 배우는 방법은?",
        "시간 관리는 어떻게 하시나요?",
        "도전적인 프로젝트 경험은?"
    )
    return questions.random()
}
