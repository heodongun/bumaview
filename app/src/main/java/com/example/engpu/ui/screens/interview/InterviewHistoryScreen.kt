package com.example.engpu.ui.screens.interview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class InterviewHistoryItem(
    val groupId: Int,
    val createdAt: String,
    val questionCount: Int,
    val averageScore: Int,
    val interviews: List<InterviewDetailItem>
)

data class InterviewDetailItem(
    val questionId: String,
    val question: String,
    val answer: String,
    val score: Int?,
    val feedback: String?
)

@Composable
fun InterviewHistoryScreen(
    historyItems: List<InterviewHistoryItem>,
    isLoading: Boolean = false,
    onBackClick: () -> Unit
) {
    var selectedGroup by remember { mutableStateOf<InterviewHistoryItem?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 21.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = StudyWithBlack
                    )
                }

                Text(
                    text = "모의면접 저장소",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyWithBlack
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StudyWithYellow)
                }
            } else if (historyItems.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📝",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "아직 완료한 면접이 없습니다",
                            fontSize = 16.sp,
                            color = StudyWithGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(historyItems) { item ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(500)
                            ) + fadeIn(animationSpec = tween(500))
                        ) {
                            InterviewHistoryCard(
                                item = item,
                                onClick = { selectedGroup = item }
                            )
                        }
                    }
                }
            }
        }

        // Home Indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .width(134.dp)
                .height(5.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.48f),
                    shape = RoundedCornerShape(100.dp)
                )
        )

        // Detail Modal
        selectedGroup?.let { group ->
            InterviewDetailModal(
                group = group,
                onDismiss = { selectedGroup = null }
            )
        }
    }
}

@Composable
private fun InterviewHistoryCard(
    item: InterviewHistoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Text(
                    text = formatDate(item.createdAt),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithGray
                )

                // Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(getScoreColor(item.averageScore).copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${item.averageScore}점",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = getScoreColor(item.averageScore)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Count
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StudyWithYellow,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "총 ${item.questionCount}개 질문 완료",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preview of questions
            Text(
                text = item.interviews.take(2).joinToString(" • ") {
                    it.question.take(20) + if (it.question.length > 20) "..." else ""
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = StudyWithGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InterviewDetailModal(
    group: InterviewHistoryItem,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Modal Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StudyWithYellow)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formatDate(group.createdAt),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyWithBlack.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "면접 결과",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudyWithBlack
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${group.averageScore}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = getScoreColor(group.averageScore)
                        )
                    }
                }

                // Questions and Answers
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(group.interviews) { interview ->
                        InterviewDetailCard(interview = interview)
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudyWithBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "닫기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithYellow,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InterviewDetailCard(interview: InterviewDetailItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Question
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Q.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyWithYellow
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = interview.question,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack,
                        lineHeight = 20.sp
                    )
                }

                interview.score?.let { score ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(getScoreColor(score).copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${score}점",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = getScoreColor(score)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Answer
            Text(
                text = "A.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = interview.answer,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = StudyWithBlack,
                lineHeight = 19.sp
            )

            // Feedback
            interview.feedback?.let { feedback ->
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "💬 AI 피드백",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyWithGray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = feedback,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun formatDate(timestamp: String): String {
    return try {
        val date = Date(timestamp.toLong())
        val format = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREAN)
        format.format(date)
    } catch (e: Exception) {
        timestamp
    }
}

private fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50) // Green
        score >= 60 -> StudyWithYellow     // Yellow
        else -> Color(0xFFF44336)          // Red
    }
}
