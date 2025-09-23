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
fun SignUpScreen5(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var school by remember { mutableStateOf("") }
    
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
            StudyWithProgressIndicator(progress = 5f / 6f)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title
            Text(
                text = "학교를 입력하세요",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Subtitle
            Text(
                text = "현재 다니고 있는 학교나 최종 학교를 입력해주세요",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack
            )
            
            Spacer(modifier = Modifier.height(35.dp))
            
            // School Input Field
            StudyWithTextField(
                value = school,
                onValueChange = { school = it },
                placeholder = "학교명을 입력해주세요 (예: 서울대학교)",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next Button
            if (school.isNotBlank()) {
                StudyWithButton(
                    text = "다음",
                    onClick = { onNextClick(school) },
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
