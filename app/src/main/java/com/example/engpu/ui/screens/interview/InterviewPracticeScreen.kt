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

// Data class to hold interview answers
data class InterviewAnswer(
    val questionId: String,
    val question: String,
    val answer: String
)

@Composable
fun InterviewPracticeScreen(
    questions: List<com.example.engpu.data.supabase.Question>,
    onBackClick: () -> Unit,
    onCompleteInterview: (List<InterviewAnswer>) -> Unit,
    onSaveAnswer: (questionId: String, answer: String) -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }
    var showQuestion by remember { mutableStateOf(false) }
    var isInterviewStarted by remember { mutableStateOf(false) }
    var shouldAnimateQuestion by remember { mutableStateOf(false) }
    var currentAnswer by remember { mutableStateOf("") }
    var answers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("면접 질문이 없습니다", color = StudyWithBlack)
        }
        return
    }
    
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
                    text = "모의면접 ${currentQuestionIndex + 1}/${questions.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.width(42.dp))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = (currentQuestionIndex + 1).toFloat() / questions.size,
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
                            text = questions[currentQuestionIndex].question,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            // Answer input field
            OutlinedTextField(
                value = currentAnswer,
                onValueChange = { currentAnswer = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("답변을 입력하세요") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StudyWithYellow,
                    unfocusedBorderColor = StudyWithGray
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    text = if (currentQuestionIndex == questions.size - 1) "면접 완료" else "다음 질문",
                    onClick = {
                        // Save current answer
                        val currentQ = questions[currentQuestionIndex]
                        if (currentAnswer.isNotBlank()) {
                            answers = answers + (currentQ.id to currentAnswer)
                            onSaveAnswer(currentQ.id, currentAnswer)
                        }

                        if (currentQuestionIndex == questions.size - 1) {
                            // Complete interview and return all answers
                            val allAnswers = answers.map { (qId, ans) ->
                                val q = questions.find { it.id == qId }
                                InterviewAnswer(qId, q?.question ?: "", ans)
                            }
                            onCompleteInterview(allAnswers)
                        } else {
                            currentQuestionIndex += 1
                            currentAnswer = answers[questions[currentQuestionIndex].id] ?: ""
                            shouldAnimateQuestion = true
                            isRecording = false
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(55.dp),
                    backgroundColor = StudyWithBlack,
                    textColor = StudyWithYellow,
                    enabled = currentAnswer.isNotBlank() || answers.isNotEmpty()
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
