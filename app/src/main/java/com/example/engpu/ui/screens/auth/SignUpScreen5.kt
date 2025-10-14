package com.example.engpu.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.components.StudyWithInactiveButton
import com.example.engpu.ui.components.StudyWithProgressIndicator
import com.example.engpu.ui.theme.*

@Composable
fun SignUpScreen5(
    onNextClick: (String) -> Unit, // "14:30" 형식으로 시간 전달
    onBackClick: () -> Unit
) {
    var selectedHour: Int by remember { mutableStateOf(9) } // 기본값 오전 9시
    var selectedMinute: Int by remember { mutableStateOf(0) } // 기본값 00분
    var isVisible: Boolean by remember { mutableStateOf(false) }
    var showTimePicker: Boolean by remember { mutableStateOf(false) }
    
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            // Progress Indicator - 5/6 단계
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                StudyWithProgressIndicator(
                    progress = 5f / 6f,
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
                    text = "면접 알림 시간 설정",
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
                    text = "매일 면접 질문을 받고 싶은 시간을 선택해주세요.\n이메일로 오늘의 면접 질문을 보내드립니다.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    lineHeight = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(50.dp))
            
            // Time Display
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "선택한 시간",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithBlack.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable { showTimePicker = true },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = String.format("%02d", selectedHour),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudyWithBlack
                                )
                                Text(
                                    text = ":",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudyWithBlack,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                Text(
                                    text = String.format("%02d", selectedMinute),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudyWithBlack
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "탭하여 시간 변경",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithBlack.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Information
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 500))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = StudyWithLightBlue.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "💡 알림 정보",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 매일 설정한 시간에 면접 질문을 받습니다\n• 언제든지 프로필에서 시간을 변경할 수 있습니다\n• 알림을 끄고 싶으면 프로필에서 설정 가능합니다",
                            fontSize = 12.sp,
                            color = StudyWithBlack.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next Button
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 600)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 600))
            ) {
                StudyWithButton(
                    text = "다음",
                    onClick = { 
                        val timeString: String = String.format("%02d:%02d", selectedHour, selectedMinute)
                        onNextClick(timeString)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    backgroundColor = StudyWithBlack,
                    textColor = StudyWithYellow
                )
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
        
        // Time Picker Dialog
        if (showTimePicker) {
            TimePickerDialog(
                currentHour = selectedHour,
                currentMinute = selectedMinute,
                onTimeSelected = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    currentHour: Int,
    currentMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var tempHour: Int by remember { mutableStateOf(currentHour) }
    var tempMinute: Int by remember { mutableStateOf(currentMinute) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "시간 선택",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Hour Picker
                    NumberPicker(
                        value = tempHour,
                        onValueChange = { tempHour = it },
                        range = 0..23,
                        label = "시"
                    )
                    
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    // Minute Picker
                    NumberPicker(
                        value = tempMinute,
                        onValueChange = { tempMinute = it },
                        range = listOf(0, 30), // 0분 또는 30분만 선택 가능
                        label = "분"
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소", color = StudyWithBlack)
                    }
                    
                    Button(
                        onClick = { onTimeSelected(tempHour, tempMinute) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudyWithOrange
                        )
                    ) {
                        Text("확인", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = StudyWithBlack.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 150.dp)
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items(range.toList()) { number ->
                    Text(
                        text = String.format("%02d", number),
                        fontSize = if (number == value) 24.sp else 18.sp,
                        fontWeight = if (number == value) FontWeight.Bold else FontWeight.Normal,
                        color = if (number == value) StudyWithOrange else StudyWithBlack.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clickable { onValueChange(number) }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: List<Int>,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = StudyWithBlack.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 150.dp)
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items(range) { number ->
                    Text(
                        text = String.format("%02d", number),
                        fontSize = if (number == value) 24.sp else 18.sp,
                        fontWeight = if (number == value) FontWeight.Bold else FontWeight.Normal,
                        color = if (number == value) StudyWithOrange else StudyWithBlack.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clickable { onValueChange(number) }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}