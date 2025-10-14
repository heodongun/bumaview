package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.data.repository.AuthRepository
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.widget.Toast

@Composable
fun SignUpScreen4(
    email: String, // 이전 화면에서 전달받은 이메일
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var verificationCode: String by remember { mutableStateOf("") }
    var isVisible: Boolean by remember { mutableStateOf(false) }
    var isCodeSent: Boolean by remember { mutableStateOf(false) }
    var remainingTime: Int by remember { mutableStateOf(0) }
    var isLoading: Boolean by remember { mutableStateOf(false) }
    var errorMessage: String? by remember { mutableStateOf(null) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository: AuthRepository = remember {
        AuthRepository().apply {
            setContext(context)
            println("✅ [SignUpScreen4] AuthRepository context configured")
        }
    }
    
    // 화면 진입시 애니메이션 시작 및 인증 코드 발송 상태 초기화
    LaunchedEffect(Unit) {
        isVisible = true
        isCodeSent = true // 이미 SignUpScreen3에서 코드를 발송했으므로 true로 설정
        remainingTime = 180 // 3분 타이머 시작
    }
    
    // 타이머 효과
    LaunchedEffect(isCodeSent, remainingTime) {
        if (isCodeSent && remainingTime > 0) {
            delay(1000L)
            remainingTime -= 1
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyWithYellow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            // Progress Indicator - 4/6 단계
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                StudyWithProgressIndicator(
                    progress = 4f / 6f,
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
                    text = "확인코드를 입력하세요",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle with timer
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(700, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
            ) {
                Column {
                    Text(
                        text = "${email}로 발송된 6자리 확인코드를 입력해주세요.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    when {
                        !isCodeSent -> {
                            Text(
                                text = "인증 코드 발송 중...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = StudyWithBlack
                            )
                        }
                        remainingTime > 0 -> {
                            val minutes: Int = remainingTime / 60
                            val seconds: Int = remainingTime % 60
                            Text(
                                text = "남은 시간: $minutes:${String.format("%02d", seconds)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = StudyWithOrange
                            )
                        }
                        else -> {
                            Text(
                                text = "시간이 만료되었습니다. 코드를 다시 발송해주세요.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = androidx.compose.ui.graphics.Color.Red
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(35.dp))
            
            // Verification Code Input Field
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                StudyWithTextField(
                    value = verificationCode,
                    onValueChange = { newValue: String ->
                        if (newValue.length <= 6 && newValue.all { char -> char.isDigit() }) {
                            verificationCode = newValue
                            errorMessage = null
                        }
                    },
                    placeholder = "6자리 확인코드를 입력해주세요",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Error message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = androidx.compose.ui.graphics.Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Resend Code Link
            AnimatedVisibility(
                visible = isVisible && isCodeSent,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 600))
            ) {
                Text(
                    text = if (remainingTime > 0) 
                        "코드를 받지 못하셨나요?" 
                    else 
                        "코드 다시 보내기",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { 
                        if (remainingTime <= 0 || remainingTime < 120) { // 1분 이하일 때 재발송 가능
                            coroutineScope.launch {
                                sendVerificationCode(
                                    email = email,
                                    authRepository = authRepository,
                                    onSuccess = {
                                        remainingTime = 180
                                        isCodeSent = true
                                        verificationCode = ""
                                        errorMessage = null
                                        Toast.makeText(context, "인증 코드가 재발송되었습니다.", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        } else {
                            Toast.makeText(
                                context, 
                                "${(remainingTime - 120) / 60}분 ${(remainingTime - 120) % 60}초 후에 재발송 가능합니다.", 
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next Button
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 500)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
            ) {
                when {
                    isLoading -> {
                        StudyWithButton(
                            text = "확인 중...",
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = false
                        )
                    }
                    verificationCode.length == 6 && !isLoading -> {
                        StudyWithButton(
                            text = "다음",
                            onClick = {
                                println("🔘 [SignUpScreen4] Verification button clicked - Email: $email, Code: $verificationCode")
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = null

                                    println("📤 [SignUpScreen4] Calling authRepository.verifyEmail...")
                                    authRepository.verifyEmail(email, verificationCode)
                                        .onSuccess {
                                            println("✅ [SignUpScreen4] Verification SUCCESS! Navigating to next screen...")
                                            isLoading = false
                                            Toast.makeText(context, "이메일 인증 완료!", Toast.LENGTH_SHORT).show()
                                            onNextClick(verificationCode)
                                        }
                                        .onFailure { exception ->
                                            println("❌ [SignUpScreen4] Verification FAILED: ${exception.message}")
                                            errorMessage = exception.message ?: "잘못된 인증 코드입니다. 다시 확인해주세요."
                                            isLoading = false
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                        }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            backgroundColor = StudyWithBlack,
                            textColor = StudyWithYellow
                        )
                    }
                    else -> {
                        StudyWithInactiveButton(
                            text = "다음",
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = false
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
        }
        
        // Back Button
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

// 이메일 인증 코드 발송 함수
private suspend fun sendVerificationCode(
    email: String,
    authRepository: AuthRepository,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    authRepository.sendVerificationCode(email)
        .onSuccess { onSuccess() }
        .onFailure { exception ->
            onError(exception.message ?: "인증 코드 발송에 실패했습니다.")
        }
}