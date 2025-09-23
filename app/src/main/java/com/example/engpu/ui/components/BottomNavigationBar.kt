package com.example.engpu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            // Home Tab - matching figma position (x: 38)
            BottomNavItem(
                icon = "🏠",
                label = "홈",
                isSelected = currentRoute == Screen.Home.route,
                onClick = { onNavigate(Screen.Home.route) },
                modifier = Modifier.width(24.dp)
            )
            
            // Repository Tab - matching figma position (x: 128.5)
            BottomNavItem(
                icon = "🔍",
                label = "저장소",
                isSelected = currentRoute == Screen.Repository.route,
                onClick = { onNavigate(Screen.Repository.route) },
                modifier = Modifier.width(31.dp)
            )
            
            // Interview Tab - matching figma position (x: 226)
            BottomNavItem(
                icon = "💬",
                label = "면접",
                isSelected = currentRoute == Screen.Interview.route,
                onClick = { onNavigate(Screen.Interview.route) },
                modifier = Modifier.width(24.dp)
            )
            
            // Profile Tab - matching figma position (x: 314.5)
            BottomNavItem(
                icon = "👤",
                label = "내 정보",
                isSelected = currentRoute == Screen.Profile.route,
                onClick = { onNavigate(Screen.Profile.route) },
                modifier = Modifier.width(34.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
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
        // Icon - using emoji for simplicity, matching figma design
        Text(
            text = icon,
            fontSize = 20.sp,
            color = if (isSelected) StudyWithYellow else StudyWithBlack
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Label - matching figma font styling
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) StudyWithYellow else StudyWithBlack
        )
    }
}

@Composable
private fun BottomNavItemWithIcon(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) StudyWithYellow else StudyWithBlack,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) StudyWithYellow else StudyWithBlack
        )
    }
}
