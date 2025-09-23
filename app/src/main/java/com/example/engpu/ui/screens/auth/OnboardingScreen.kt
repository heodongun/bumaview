package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithOutlinedButton
import com.example.engpu.ui.theme.*
import kotlinx.coroutines.delay

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
            
            // Illustration Container with animation
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
                        .offset(x = 49.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    // Background blob shape with pulse animation
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
                    
                    Box(
                        modifier = Modifier
                            .size(432.dp, 452.dp)
                            .scale(scale)
                            .clip(RoundedCornerShape(226.dp))
                            .background(Color.White.copy(alpha = 0.48f))
                    )
                    
                    // Character illustration container with bounce animation
                    val bounceScale by infiniteTransition.animateFloat(
                        initialValue = 0.98f,
                        targetValue = 1.02f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = EaseInOut),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "bounce"
                    )
                    
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 66.dp)
                            .size(318.dp, 216.dp)
                            .scale(bounceScale)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎓👥",
                            fontSize = 80.sp,
                            textAlign = TextAlign.Center
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
