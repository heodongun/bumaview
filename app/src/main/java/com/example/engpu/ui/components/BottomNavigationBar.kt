package com.example.engpu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.R
import com.example.engpu.navigation.Screen
import com.example.engpu.ui.theme.*

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab - 피그마 group1543.png 사용 (홈)
            BottomNavItemWithImage(
                imageRes = R.drawable.group1543,
                label ="",
                isSelected = currentRoute == Screen.Home.route,
                onClick = { onNavigate(Screen.Home.route) },
                modifier = Modifier.width(30.dp)
            )
            
            // Repository Tab - 피그마 group1540.png 사용 (저장소)
            BottomNavItemWithImage(
                imageRes = R.drawable.group1540,
                label = "",
                isSelected = currentRoute == Screen.Repository.route,
                onClick = { onNavigate(Screen.Repository.route) },
                modifier = Modifier.width(30.dp)
            )
            
            // Interview Tab - 피그마 group1541.png 사용 (면접)
            BottomNavItemWithImage(
                imageRes = R.drawable.group1541,
                label = "",
                isSelected = currentRoute == Screen.Interview.route,
                onClick = { onNavigate(Screen.Interview.route) },
                modifier = Modifier.width(30.dp)
            )
            
            // Profile Tab - 피그마 group1542.png 사용 (내 정보)
            BottomNavItemWithImage(
                imageRes = R.drawable.group1542,
                label = "",
                isSelected = currentRoute == Screen.Profile.route,
                onClick = { onNavigate(Screen.Profile.route) },
                modifier = Modifier.width(30.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItemWithImage(
    imageRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        // 실제 피그마 Group PNG 이미지 사용
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier.size(100.dp), // 피그마 정확한 아이콘 크기
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Label - 피그마 스타일 매칭
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) StudyWithYellow else StudyWithBlack
        )
    }
}
