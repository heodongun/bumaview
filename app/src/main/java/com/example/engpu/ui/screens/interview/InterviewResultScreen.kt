package com.example.engpu.ui.screens.interview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.theme.*

data class InterviewResult(
    val questionId: String,
    val question: String,
    val answer: String,
    val score: Int,
    val feedback: String
)

@Composable
fun InterviewResultScreen(
    results: List<InterviewResult>,
    onGoHome: () -> Unit
) {
    val averageScore = if (results.isNotEmpty()) {
        results.map { it.score }.average().toInt()
    } else {
        0
    }

    var showResults by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showResults = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(50.dp))

                // Completion Header
                AnimatedVisibility(
                    visible = showResults,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "완료",
                            tint = StudyWithYellow,
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "면접 완료!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudyWithBlack
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "총 ${results.size}개의 질문에 답변하셨습니다",
                            fontSize = 16.sp,
                            color = StudyWithGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Average Score Card
                AnimatedVisibility(
                    visible = showResults,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 200))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = StudyWithYellow.copy(alpha = 0.1f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "평균 점수",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = StudyWithGray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "$averageScore",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyWithYellow
                            )

                            Text(
                                text = "/ 10",
                                fontSize = 20.sp,
                                color = StudyWithGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "상세 피드백",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyWithBlack
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Results List
            items(results) { result ->
                AnimatedVisibility(
                    visible = showResults,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 400))
                ) {
                    ResultCard(result = result)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Home Button
                StudyWithButton(
                    text = "홈으로 돌아가기",
                    onClick = onGoHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    backgroundColor = StudyWithBlack,
                    textColor = StudyWithYellow
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ResultCard(result: InterviewResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Question
            Text(
                text = "Q. ${result.question}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Answer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StudyWithGray.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "답변",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.answer,
                        fontSize = 14.sp,
                        color = StudyWithBlack,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score and Feedback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "점수:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${result.score}/10",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyWithYellow
                    )
                }

                val scoreColor = when {
                    result.score >= 8 -> Color(0xFF4CAF50)
                    result.score >= 6 -> StudyWithYellow
                    else -> Color(0xFFFF5722)
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = scoreColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = when {
                            result.score >= 8 -> "우수"
                            result.score >= 6 -> "보통"
                            else -> "노력 필요"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = scoreColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Feedback
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StudyWithYellow.copy(alpha = 0.05f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "피드백",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.feedback,
                        fontSize = 13.sp,
                        color = StudyWithBlack,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
