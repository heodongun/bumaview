package com.example.engpu.ui.screens.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.data.supabase.Question
import com.example.engpu.ui.theme.*

@Composable
fun QuestionItemDB(
    question: Question,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "itemScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(horizontal = 23.dp, vertical = 16.dp)
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    Color(0xFFC4C4C4).copy(alpha = 0.18f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📝",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Question Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = question.question,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                question.company?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithGray
                    )
                }

                question.category?.let {
                    Text(
                        text = "• $it",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithGray
                    )
                }
            }
        }

        // Year Badge (if available)
        question.question_at?.let { year ->
            Card(
                modifier = Modifier.padding(start = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StudyWithYellow.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = "$year",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithBlack
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
fun QuestionDetailModalDB(
    question: Question,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable { /* Prevent click propagation */ },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "질문 상세정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack
                    )

                    TextButton(onClick = onDismiss) {
                        Text("닫기", color = StudyWithBlack)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question Text
                Text(
                    text = "질문",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithGray
                )

                Text(
                    text = question.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Company and Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    question.company?.let { company ->
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "회사",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = StudyWithGray
                            )
                            Text(
                                text = company,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudyWithBlack,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    question.category?.let { category ->
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "카테고리",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = StudyWithGray
                            )
                            Text(
                                text = category,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudyWithBlack,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Question Year
                question.question_at?.let { year ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "출제 연도",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithGray
                    )

                    Card(
                        modifier = Modifier.padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = StudyWithYellow.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "${year}년",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyWithBlack
                        )
                    }
                }
            }
        }
    }
}
