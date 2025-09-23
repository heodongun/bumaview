package com.example.engpu.ui.screens.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
    userName: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar - matching figma design
            StatusBar()
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // User Greeting - matching figma position (y: 74)
                UserGreeting(userName = userName)
                
                Spacer(modifier = Modifier.height(14.dp)) // to match y: 112
                
                // Welcome Message - matching figma text and position
                Text(
                    text = "이런 과정을 통해 면접에 대비 해보는 것은 어떨까요?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    lineHeight = 18.sp,
                    modifier = Modifier.width(307.dp) // matching figma width
                )
                
                Spacer(modifier = Modifier.height(29.dp)) // to match y: 182
                
                // Action Cards - matching figma positions and colors
                ActionCard(
                    title = "면접질문 저장소",
                    description = "면접질문을 돌아보는것도\n괜찮은 선택이에요!",
                    backgroundColor = StudyWithLightOrange,
                    onClick = { onNavigate(Screen.Repository.route) }
                )
                
                Spacer(modifier = Modifier.height(17.dp)) // spacing between cards
                
                ActionCard(
                    title = "모의면접",
                    description = "모의면접을 하며\n실력을 키워봐요!",
                    backgroundColor = StudyWithPurple,
                    onClick = { onNavigate(Screen.Interview.route) },
                    showMusicIcon = true
                )
                
                Spacer(modifier = Modifier.height(17.dp)) // spacing between cards
                
                ActionCard(
                    title = "모의면접 저장소",
                    description = "면접에 대한 피드백을\n들으며 복기를 해보아요!",
                    backgroundColor = StudyWithLightBlue,
                    onClick = { 
                        // Show toast for now
                        // Can add specific screen later
                    },
                    showClockIcon = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Bottom Navigation - matching figma design
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
        
        // Home Indicator - matching figma design
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
        // Time - matching figma "9:41"
        Text(
            text = "9:41",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = StudyWithBlack
        )
        
        // Status icons - battery, wifi, signal
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simplified status icons
            Text(text = "📶", fontSize = 12.sp)
            Text(text = "📶", fontSize = 12.sp)
            Text(text = "🔋", fontSize = 12.sp)
        }
    }
}

@Composable
private fun UserGreeting(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Avatar - matching figma design (28x28 with D19700 color)
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = Color(0xFFD19700),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.toString()?.uppercase() ?: "T",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(10.dp))
        
        // Greeting text - matching figma "{userName}님!"
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
    showClockIcon: Boolean = false
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
            .height(144.dp)
            .scale(scale),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            isPressed = true
            onClick()
            // Reset after a short delay
            // Note: In a real app, you might want to handle this differently
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Spacer(modifier = Modifier.height(19.dp))
                
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack,
                    lineHeight = 18.sp
                )
            }
            
            // Decorative icon in top right - matching figma illustrations
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(122.dp)
                    .offset(x = 10.dp, y = (-2).dp)
            ) {
                when {
                    showMusicIcon -> {
                        // Music/study illustration
                        Text(
                            text = "🎵📚",
                            fontSize = 40.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    showClockIcon -> {
                        // Clock illustration
                        Text(
                            text = "🕐",
                            fontSize = 50.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        // Default illustration
                        Text(
                            text = "📁",
                            fontSize = 50.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
