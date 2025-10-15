package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun SignUpScreen6(
    onCompleteClick: (password: String, confirmPassword: String) -> Unit,
    onBackClick: () -> Unit,
    userName: String,
    initialError: String? = null
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    var showCompletionAnimation by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Update error message when initialError changes
    LaunchedEffect(initialError) {
        errorMessage = initialError
    }

    val isPasswordValid = password.length >= 8
    val isPasswordMatching = password == confirmPassword && confirmPassword.isNotEmpty()
    val canProceed = isPasswordValid && isPasswordMatching

    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyWithYellow)
    ) {
        if (!showCompletionAnimation) {
            // 비밀번호 입력 화면
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                
                // Progress Indicator - 6/6 단계 (완료)
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(600)
                    ) + fadeIn(animationSpec = tween(600))
                ) {
                    StudyWithProgressIndicator(
                        progress = 1f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Title
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { -it / 2 },
                        animationSpec = tween(700, delayMillis = 200)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
                ) {
                    Text(
                        text = "비밀번호를 설정하세요",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack
                    )
                }
                
                Spacer(modifier = Modifier.height(15.dp))
                
                // Subtitle
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { -it / 2 },
                        animationSpec = tween(700, delayMillis = 300)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
                ) {
                    Text(
                        text = "8자 이상의 안전한 비밀번호를 입력해주세요.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack
                    )
                }
                
                Spacer(modifier = Modifier.height(35.dp))
                
                // Password Input Field
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(800, delayMillis = 400)
                    ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
                ) {
                    StudyWithTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "비밀번호를 입력해주세요 (8자 이상)",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
                
                Spacer(modifier = Modifier.height(15.dp))
                
                // Password validation indicator
                AnimatedVisibility(
                    visible = password.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Text(
                        text = if (isPasswordValid) "✓ 사용 가능한 비밀번호입니다" else "✗ 8자 이상 입력해주세요",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isPasswordValid) Color(0xFF4CAF50) else Color(0xFFE57373)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Confirm Password Input Field
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(800, delayMillis = 500)
                    ) + fadeIn(animationSpec = tween(800, delayMillis = 500))
                ) {
                    StudyWithTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "비밀번호를 다시 입력해주세요",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
                
                Spacer(modifier = Modifier.height(15.dp))

                // Password match indicator
                AnimatedVisibility(
                    visible = confirmPassword.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Text(
                        text = if (isPasswordMatching) "✓ 비밀번호가 일치합니다" else "✗ 비밀번호가 일치하지 않습니다",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isPasswordMatching) Color(0xFF4CAF50) else Color(0xFFE57373)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Error message display
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                
                // Complete Button
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(700, delayMillis = 600)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 600))
                ) {
                    if (canProceed) {
                        StudyWithButton(
                            text = "회원가입 완료",
                            onClick = {
                                println("🔘 [SignUpScreen6] Complete button clicked")
                                errorMessage = null
                                onCompleteClick(password, confirmPassword)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            backgroundColor = StudyWithBlack,
                            textColor = StudyWithYellow
                        )
                    } else {
                        StudyWithInactiveButton(
                            text = "회원가입 완료",
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = false
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }
        } else {
            // 완료 애니메이션 화면 - 실제로는 바로 로그인 화면으로 이동
            CompletionScreen(
                userName = userName,
                onLoginClick = { /* Already called onCompleteClick in button */ }
            )
        }
        
        // Back Button (비밀번호 입력 화면에서만 표시)
        if (!showCompletionAnimation) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 100))
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = StudyWithBlack
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletionScreen(
    userName: String,
    onLoginClick: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        isVisible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        
        // Progress Indicator (완료)
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(600)
            ) + fadeIn(animationSpec = tween(600))
        ) {
            StudyWithProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp))
        
        // Success Icon with bounce animation
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                initialScale = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(600))
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = StudyWithBlack,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 60.sp,
                    color = StudyWithYellow,
                    fontWeight = FontWeight.Bold
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
                text = "회원가입 완료!",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(15.dp))
        
        // Subtitle
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(700, delayMillis = 500)
            ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
        ) {
            Text(
                text = "${userName}님, StudyWith에 오신 것을 환영합니다!\n로그인하시면 모의면접을 시작할 수 있어요!",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Login Button
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(700, delayMillis = 800)
            ) + fadeIn(animationSpec = tween(700, delayMillis = 800))
        ) {
            StudyWithButton(
                text = "로그인하기",
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                backgroundColor = StudyWithBlack,
                textColor = StudyWithYellow
            )
        }
        
        Spacer(modifier = Modifier.height(60.dp))
    }
}
