package com.example.engpu.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*

@Composable
fun SignUpScreen2(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyWithYellow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            // Progress Indicator
            StudyWithProgressIndicator(progress = 2f / 6f)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title
            Text(
                text = "이메일을 입력하세요",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle
            Text(
                text = "로그인 시 사용할 이메일을 입력해주세요",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack
            )
            
            Spacer(modifier = Modifier.height(35.dp))
            
            // Email Input Field
            StudyWithTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "이메일을 입력해주세요 (예: hong@gmail.com)",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next Button
            if (email.isNotBlank() && email.contains("@")) {
                StudyWithButton(
                    text = "다음",
                    onClick = { onNextClick(email) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                StudyWithInactiveButton(
                    text = "다음",
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }
            
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
