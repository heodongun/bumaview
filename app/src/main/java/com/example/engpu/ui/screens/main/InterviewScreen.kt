package com.example.engpu.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun InterviewScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onStartInterviewSetup: () -> Unit,
    isLoading: Boolean = false
) {
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 21.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "9:41",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "📶", fontSize = 10.sp)
                    Text(text = "📶", fontSize = 10.sp)
                    Text(text = "🔋", fontSize = 10.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interview Icon with animation
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(
                        initialScale = 0.3f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(800))
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                StudyWithYellow,
                                RoundedCornerShape(60.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎤",
                            fontSize = 60.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Title
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(700, delayMillis = 400)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 400))
                ) {
                    Text(
                        text = "모의면접",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyWithBlack,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(700, delayMillis = 500)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
                ) {
                    Text(
                        text = "실제 면접과 같은 환경에서\n연습해보세요!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(60.dp))
                
                // Feature Cards
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(700, delayMillis = 600)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 600))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FeatureCard(
                            icon = "⏰",
                            title = "시간 제한",
                            description = "실제 면접처럼 시간 제한을 두고 연습"
                        )
                        
                        FeatureCard(
                            icon = "🎯",
                            title = "맞춤형 질문",
                            description = "직무와 수준에 맞는 질문 제공"
                        )
                        
                        FeatureCard(
                            icon = "📊",
                            title = "피드백",
                            description = "답변에 대한 상세한 분석과 개선점"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Start Button
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(700, delayMillis = 800)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 800))
                ) {
                    StudyWithButton(
                        text = if (isLoading) "질문 준비 중..." else "모의면접 시작하기",
                        onClick = onStartInterviewSetup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        backgroundColor = StudyWithBlack,
                        textColor = StudyWithYellow,
                        enabled = !isLoading
                    )
                }
                
                Spacer(modifier = Modifier.height(100.dp)) // Bottom nav 공간
            }
        }
        
        // Bottom Navigation
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
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

@Composable
private fun FeatureCard(
    icon: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
