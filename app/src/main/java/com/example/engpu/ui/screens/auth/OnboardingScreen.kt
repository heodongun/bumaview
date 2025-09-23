package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.R
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithOutlinedButton
import com.example.engpu.ui.theme.*


@Composable
fun OnboardingScreen(
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyWithYellow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(0.dp))
            
            // 피그마 Group 17 일러스트레이션 - 실제 이미지 사용
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(1000, easing = EaseOutBounce)
                ) + fadeIn(animationSpec = tween(1000))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(452.dp)
                        .offset(x = 73.dp), // 피그마 정확한 위치 x: 73
                    contentAlignment = Alignment.TopStart
                ) {
                    // Background pulse animation
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.02f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = EaseInOut),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )
                    
                    // 실제 Group17.png 이미지 사용 + Group28 보조 요소 추가
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.group17),
                            contentDescription = "StudyWith 일러스트레이션",
                            modifier = Modifier
                                .size(432.63.dp, 452.dp) // 피그마 정확한 크기
                                .scale(scale),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Group28 보조 그래픽 요소 추가
                        Image(
                            painter = painterResource(id = R.drawable.group28),
                            contentDescription = "보조 그래픽",
                            modifier = Modifier
                                .size(48.dp) // Group28 적절한 크기
                                .align(Alignment.BottomEnd)
                                .offset(x = (-20).dp, y = (-20).dp)
                                .scale(scale * 0.8f), // 약간 다른 애니메이션으로 층감 연출
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(81.dp))
            
            // Welcome Text with slide animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(800, delayMillis = 500)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 500))
            ) {
                Text(
                    text = "Welcome to StudyWith",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithBlack,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(58.dp))
            
            // Buttons with staggered animation
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, delayMillis = 800)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 800))
            ) {
                Column {
                    StudyWithButton(
                        text = "회원가입",
                        onClick = onSignUpClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        backgroundColor = StudyWithBlack,
                        textColor = StudyWithYellow
                    )
                    
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    StudyWithOutlinedButton(
                        text = "로그인",
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        borderColor = StudyWithBlack,
                        textColor = StudyWithBlack,
                        backgroundColor = Color.White.copy(alpha = 0.21f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
