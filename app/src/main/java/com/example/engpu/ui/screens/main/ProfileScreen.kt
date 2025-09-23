package com.example.engpu.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.BottomNavigationBar
import com.example.engpu.ui.theme.*

@Composable
fun ProfileScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudyWithYellow)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color(0xFFD19700),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Profile Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "완료한 면접",
                    value = "12"
                )
                
                StatItem(
                    title = "저장된 질문",
                    value = "45"
                )
                
                StatItem(
                    title = "연습 시간",
                    value = "8시간"
                )
            }
            
            // Profile Options
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                HorizontalDivider(
                    color = StudyWithLightGray,
                    thickness = 1.dp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                ProfileOption(
                    icon = Icons.Default.Edit,
                    title = "프로필 편집",
                    subtitle = "개인정보 수정",
                    onClick = { /* Handle edit profile */ }
                )
                
                ProfileOption(
                    icon = Icons.Default.Settings,
                    title = "설정",
                    subtitle = "앱 설정 및 알림",
                    onClick = { /* Handle settings */ }
                )
                
                ProfileOption(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "로그아웃",
                    subtitle = "계정에서 로그아웃",
                    onClick = { showLogoutDialog = true },
                    isDestructive = true
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // App Info
                Text(
                    text = "StudyWith v1.0.0",
                    fontSize = 12.sp,
                    color = StudyWithGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        
        // Bottom Navigation
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "로그아웃",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("정말 로그아웃 하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        text = "로그아웃",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = StudyWithBlack
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = StudyWithGray
        )
    }
}

@Composable
private fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) Color.Red else StudyWithBlack,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) Color.Red else StudyWithBlack
                )
                
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithGray
                )
            }
        }
    }
}
