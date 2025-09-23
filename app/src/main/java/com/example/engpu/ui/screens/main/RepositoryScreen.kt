package com.example.engpu.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.BottomNavigationBar
import com.example.engpu.ui.theme.*

@Composable
fun RepositoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    // Sample data for questions
    val questions = remember {
        listOf(
            "자기소개를 해주세요",
            "지원 동기는 무엇인가요?",
            "본인의 장점과 단점을 말해주세요",
            "5년 후 자신의 모습은?",
            "왜 이 회사를 선택했나요?",
            "팀워크 경험에 대해 말해주세요",
            "스트레스 관리 방법은?",
            "실패 경험과 극복 과정은?",
            "리더십 경험이 있나요?",
            "회사에 기여할 수 있는 부분은?",
            "가장 성취감을 느꼈던 경험은?",
            "갈등 상황을 해결한 경험이 있나요?",
            "새로운 기술을 배우는 방법은?",
            "시간 관리는 어떻게 하시나요?",
            "도전적인 프로젝트 경험은?"
        )
    }
    
    var searchQuery by remember { mutableStateOf("") }
    
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
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = "면접질문 저장소",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "저장된 면접 질문들을 확인해보세요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = StudyWithBlack
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Search Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = { /* Handle search click */ }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색",
                            tint = StudyWithGray,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "질문을 검색해보세요...",
                            fontSize = 14.sp,
                            color = StudyWithGray
                        )
                    }
                }
            }
            
            // Questions List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(questions) { question ->
                    QuestionCard(
                        question = question,
                        onBookmarkClick = { /* Handle bookmark */ },
                        onShareClick = { /* Handle share */ }
                    )
                }
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
}

@Composable
private fun QuestionCard(
    question: String,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit
) {
    var isBookmarked by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = StudyWithBlack,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { 
                        isBookmarked = !isBookmarked
                        onBookmarkClick()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "북마크",
                        tint = if (isBookmarked) StudyWithYellow else StudyWithGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "공유",
                        tint = StudyWithGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
