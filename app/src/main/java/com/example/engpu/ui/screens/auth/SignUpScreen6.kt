package com.example.engpu.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.theme.*

@Composable
fun SignUpScreen6(
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit,
    userName: String
) {
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
            Spacer(modifier = Modifier.height(50.dp))
            
            // Progress Indicator
            StudyWithProgressIndicator(progress = 1f)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title
            Text(
                text = "회원가입 완료!",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle
            Text(
                text = "${userName}님, StudyWith에 오신 것을 환영합니다!\n로그인 하시면 모의면접을 시작할 수 있어요!",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Success Icon
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
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Complete Button - 로그인 화면으로 이동
            StudyWithButton(
                text = "로그인하기",
                onClick = onCompleteClick,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = StudyWithBlack,
                textColor = StudyWithYellow
            )
            
            Spacer(modifier = Modifier.height(60.dp))
        }
        
        // Back Button
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
