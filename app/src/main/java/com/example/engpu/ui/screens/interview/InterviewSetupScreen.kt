package com.example.engpu.ui.screens.interview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.theme.*

@Composable
fun InterviewSetupScreen(
    categories: List<String>,
    onStartInterview: (questionCount: Int, category: String?) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedQuestionCount by remember { mutableStateOf(5) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
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
                    text = "모의면접 설정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyWithBlack
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                // Question Count Selection
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                ) {
                    Column {
                        Text(
                            text = "질문 개수",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf(3, 5, 10).forEach { count ->
                                FilterChip(
                                    selected = selectedQuestionCount == count,
                                    onClick = { selectedQuestionCount = count },
                                    label = {
                                        Text(
                                            text = "${count}문제",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StudyWithYellow,
                                        selectedLabelColor = StudyWithBlack,
                                        containerColor = Color.White,
                                        labelColor = StudyWithGray
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedQuestionCount == count,
                                        borderColor = if (selectedQuestionCount == count) StudyWithYellow else StudyWithGray.copy(alpha = 0.3f),
                                        selectedBorderColor = StudyWithYellow
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Category Selection
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(700, delayMillis = 200)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
                ) {
                    Column {
                        Text(
                            text = "카테고리 선택",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "특정 카테고리만 선택하거나, 전체 질문에서 랜덤으로 선택할 수 있습니다",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = StudyWithGray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // All categories option
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = {
                                Text(
                                    text = "전체",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudyWithYellow,
                                selectedLabelColor = StudyWithBlack,
                                containerColor = Color.White,
                                labelColor = StudyWithGray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == null,
                                borderColor = if (selectedCategory == null) StudyWithYellow else StudyWithGray.copy(alpha = 0.3f),
                                selectedBorderColor = StudyWithYellow
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category chips
                        if (categories.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                categories.chunked(2).forEach { rowCategories ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowCategories.forEach { category ->
                                            FilterChip(
                                                selected = selectedCategory == category,
                                                onClick = { selectedCategory = category },
                                                label = {
                                                    Text(
                                                        text = category,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = StudyWithYellow,
                                                    selectedLabelColor = StudyWithBlack,
                                                    containerColor = Color.White,
                                                    labelColor = StudyWithGray
                                                ),
                                                border = FilterChipDefaults.filterChipBorder(
                                                    enabled = true,
                                                    selected = selectedCategory == category,
                                                    borderColor = if (selectedCategory == category) StudyWithYellow else StudyWithGray.copy(alpha = 0.3f),
                                                    selectedBorderColor = StudyWithYellow
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        // Fill remaining space if odd number of items
                                        if (rowCategories.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Summary Card
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(700, delayMillis = 400)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 400))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StudyWithYellow.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "📝 면접 설정 요약",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudyWithBlack
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "질문 개수:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = StudyWithGray
                                )
                                Text(
                                    text = "${selectedQuestionCount}문제",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StudyWithBlack
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "카테고리:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = StudyWithGray
                                )
                                Text(
                                    text = selectedCategory ?: "전체",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StudyWithBlack
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Start Button
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 600)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 600))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    StudyWithButton(
                        text = "모의면접 시작하기",
                        onClick = { onStartInterview(selectedQuestionCount, selectedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        backgroundColor = StudyWithBlack,
                        textColor = StudyWithYellow
                    )
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
    }
}
