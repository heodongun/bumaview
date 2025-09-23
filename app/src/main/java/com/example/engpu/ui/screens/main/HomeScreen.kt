package com.example.engpu.ui.screens.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.navigation.Screen
import com.example.engpu.ui.components.BottomNavigationBar
import com.example.engpu.ui.theme.*

@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userName: String,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar - 피그마 디자인과 정확히 동일
            StatusBar()
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // User Greeting - 피그마 위치 (y: 74)
                UserGreeting(
                    userName = userName,
                    onProfileClick = onProfileClick
                )
                
                Spacer(modifier = Modifier.height(14.dp)) // y: 112 - 74 - 28 = 10dp
                
                // Welcome Message - 피그마 텍스트와 위치 정확히 매칭
                Text(
                    text = "이런 과정을 통해 면접에 대비 해보는 것은 어떨까요?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    lineHeight = 18.sp,
                    modifier = Modifier.width(307.dp) // 피그마 width
                )
                
                Spacer(modifier = Modifier.height(29.dp)) // y: 182 - 112 - 42 = 28dp
                
                // Action Cards - 피그마 순서와 색상에 맞춰 배치
                // 1. 면접질문 저장소 (주황색)
                ActionCard(
                    title = "면접질문 저장소",
                    description = "면접질문을 돌아보는것도\n괜찮은 선택이에요!",
                    backgroundColor = StudyWithLightOrange, // #FF9A62
                    onClick = { onNavigate(Screen.Repository.route) },
                    iconEmoji = "📁"
                )
                
                Spacer(modifier = Modifier.height(17.dp)) // 카드 간격
                
                // 2. 모의면접 (보라색) 
                ActionCard(
                    title = "모의면접",
                    description = "모의면접을 하며\n실력을 키워봐요!",
                    backgroundColor = StudyWithPurple, // #D9C7E7
                    onClick = { onNavigate(Screen.Interview.route) },
                    showMusicIcon = true
                )
                
                Spacer(modifier = Modifier.height(17.dp)) // 카드 간격
                
                // 3. 모의면접 저장소 (파란색)
                ActionCard(
                    title = "모의면접 저장소",
                    description = "면접에 대한 피드백을\n들으며 복기를 해보아요!",
                    backgroundColor = StudyWithLightBlue, // #85C7EE
                    onClick = { 
                        // 추후 구현할 저장소 화면
                    },
                    showClockIcon = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Bottom Navigation - 피그마 디자인 매칭
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
        
        // Home Indicator - 피그마 정확한 위치와 크기
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .width(134.dp)
                .height(5.dp)
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(100.dp)
                )
        )
    }
}

@Composable
private fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 21.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time - 피그마 "9:41"
        Text(
            text = "9:41",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = StudyWithBlack
        )
        
        // Status icons - 피그마 우측 상단 아이콘들
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 신호 강도 표시 (단순화)
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height((6 + index * 2).dp)
                            .background(StudyWithBlack, RoundedCornerShape(1.dp))
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(5.dp))
            
            // WiFi 아이콘 (단순화)
            Text(text = "📶", fontSize = 10.sp)
            
            Spacer(modifier = Modifier.width(5.dp))
            
            // 배터리 아이콘
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(12.dp)
                    .background(StudyWithBlack, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.8f)
                        .background(StudyWithBlack)
                )
            }
        }
    }
}

@Composable
private fun UserGreeting(
    userName: String,
    onProfileClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Avatar - 피그마 정확한 크기 (28x28)와 색상 (#D19700)
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = Color(0xFFD19700), // 피그마 정확한 색상
                    shape = CircleShape
                )
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(10.dp))
        
        // Greeting text - 피그마 정확한 텍스트와 스타일
        Text(
            text = "${userName}님!",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = StudyWithBlack
        )
    }
}

@Composable
private fun ActionCard(
    title: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    showMusicIcon: Boolean = false,
    showClockIcon: Boolean = false,
    iconEmoji: String = "📚"
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp) // 피그마 정확한 높이
            .scale(scale),
        shape = RoundedCornerShape(6.dp), // 피그마 정확한 모서리
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // 피그마 그림자
        onClick = {
            isPressed = true
            onClick()
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp) // 피그마 패딩
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Spacer(modifier = Modifier.height(19.dp)) // 피그마 y: 31
                
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(8.dp)) // 피그마 간격
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack,
                    lineHeight = 18.sp
                )
            }
            
            // 우측 상단 아이콘 - 피그마 정확한 위치 (186, 10) 크기 (122x122)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(122.dp)
                    .offset(x = 10.dp, y = (-2).dp)
            ) {
                when {
                    showMusicIcon -> {
                        // 피그마 music 컴포넌트 스타일
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        ) {
                            Text(
                                text = "🎵",
                                fontSize = 40.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    showClockIcon -> {
                        // 피그마 clock 컴포넌트 스타일
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        ) {
                            Text(
                                text = "🕐",
                                fontSize = 40.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    else -> {
                        // 기본 아이콘
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        ) {
                            Text(
                                text = iconEmoji,
                                fontSize = 40.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 클릭 상태 리셋
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}
