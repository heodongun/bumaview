package com.example.engpu.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header - 피그마 노란색 헤더 (height: 231)
            ProfileHeader(
                userName = userName,
                userEmail = userEmail,
                isVisible = isVisible
            )
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(55.dp)) // y: 286 - 231 = 55
                
                // Section: 면접, 자신 있게!
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(700, delayMillis = 600)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 600))
                ) {
                    ProfileSection(
                        title = "면접, 자신 있게!",
                        description = "지금까지 준비한 만큼 충분히 잘할 수 있어요.\n긴장하지 말고 자신감을 가지세요."
                    )
                }
                
                Spacer(modifier = Modifier.height(37.dp)) // y: 384 - 286 - 21 - 37 = 37
                
                // Section: 만든이
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(700, delayMillis = 700)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 700))
                ) {
                    Column {
                        Text(
                            text = "만든이",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack
                        )
                        
                        Spacer(modifier = Modifier.height(15.dp))
                        
                        Text(
                            text = "김정훈과 허동운\n커피 사주세요. 후원하기",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = StudyWithBlack,
                            lineHeight = 17.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(46.dp)) // y: 493 - 384 - 14 - 34 = 61, adjusted
                
                // Section: 가격제
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(700, delayMillis = 800)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 800))
                ) {
                    Column {
                        Text(
                            text = "가격제",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack
                        )
                        
                        Spacer(modifier = Modifier.height(27.dp)) // y: 534 - 493 - 14 = 27
                        
                        // Pricing Options
                        PricingOption(
                            label = "VIP",
                            price = "$5",
                            isSelected = false
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp)) // y: 574 - 534 - 16 = 24
                        
                        PricingOption(
                            label = "USER",
                            price = "$0",
                            isSelected = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Logout Button
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(700, delayMillis = 900)
                    ) + fadeIn(animationSpec = tween(700, delayMillis = 900))
                ) {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE57373),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "로그아웃",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp)) // Bottom nav 공간
            }
        }
        
        // Bottom Navigation
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
        
        // Home Indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .width(134.dp)
                .height(5.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.48f),
                    shape = RoundedCornerShape(100.dp)
                )
        )
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    isVisible: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp), // 피그마 height: 231, 조정
        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp), // 피그마 borderRadius: 36px
        colors = CardDefaults.cardColors(containerColor = StudyWithYellow), // 피그마 fill_DDUUSM
        elevation = CardDefaults.cardElevation(defaultElevation = 11.dp) // 피그마 그림자
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 21.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "9:41",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "📶", fontSize = 10.sp)
                    Text(text = "📶", fontSize = 10.sp)
                    Text(text = "🔋", fontSize = 10.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp)) // y: 64 - 44 = 20
            
            // Profile Avatar - 피그마 위치 (x: 160, y: 64) 크기 (56x56)
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    initialScale = 0.3f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp) // 피그마 정확한 크기
                        .clip(CircleShape)
                        .background(Color(0xFFD19700)), // 기본 프로필 색상
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp)) // y: 126 - 64 - 56 = 6
            
            // User Name - 피그마 위치 (x: 152, y: 126)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(700, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 400))
            ) {
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold, // 피그마 weight: 600
                    color = StudyWithBlack,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(17.dp)) // y: 157 - 126 - 14 = 17
            
            // Email - 피그마 위치 (x: 88, y: 157)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(700, delayMillis = 500)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 500))
            ) {
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, // 피그마 weight: 600
                    color = StudyWithBlack,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp)) // y: 173 - 157 - 14 = 2
            
            // VIP Badge - 피그마 위치 (x: 88, y: 173)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(700, delayMillis = 600)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 600))
            ) {
                Text(
                    text = "VIP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, // 피그마 weight: 600
                    color = StudyWithBlack,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    description: String
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold, // 피그마 weight: 600
            color = StudyWithBlack
        )
        
        Spacer(modifier = Modifier.height(14.dp)) // y: 315 - 286 - 21 = 8, adjusted
        
        Text(
            text = description,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold, // 피그마 weight: 600
            color = StudyWithBlack,
            lineHeight = 17.sp // 피그마 lineHeight: 1.244
        )
    }
}

@Composable
private fun PricingOption(
    label: String,
    price: String,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Indicator - 피그마 Ellipse 10/11 (15x15)
            Box(
                modifier = Modifier
                    .size(15.dp) // 피그마 정확한 크기
                    .background(
                        color = Color(0xFFC4C4C4), // 피그마 fill_V4S0FA
                        shape = CircleShape
                    )
                    .then(
                        if (isSelected) Modifier.background(
                            color = StudyWithBlack,
                            shape = CircleShape
                        ) else Modifier
                    )
            )
            
            Spacer(modifier = Modifier.width(15.dp)) // x: 54 - 24 - 15 = 15
            
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal, // 피그마 weight: 400
                color = StudyWithBlack
            )
        }
        
        // Price - 피그마 위치 및 스타일
        Text(
            text = price,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold, // 피그마 weight: 600
            color = StudyWithBlack,
            textAlign = TextAlign.End,
            modifier = Modifier.width(90.dp) // 피그마 width
        )
    }
}
