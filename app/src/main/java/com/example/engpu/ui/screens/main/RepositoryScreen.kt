package com.example.engpu.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.ui.components.BottomNavigationBar
import com.example.engpu.ui.components.StudyWithTextField
import com.example.engpu.ui.theme.*

data class InterviewQuestion(
    val id: Int,
    val title: String,
    val company: String,
    val level: String,
    val category: String,
    val description: String = ""
)

@Composable
fun RepositoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var searchTitle by remember { mutableStateOf("") }
    var searchCategory by remember { mutableStateOf("") }
    var selectedQuestion by remember { mutableStateOf<InterviewQuestion?>(null) }
    var isExpanded by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    
    // 샘플 데이터 - 피그마 디자인의 Economics, 마이다스, Level 2 등
    val questions = remember {
        listOf(
            InterviewQuestion(1, "Economics", "마이다스", "Level 2", "백엔드", "경제학 관련 기본 지식을 평가하는 질문입니다."),
            InterviewQuestion(2, "Economics", "Level 2", "Level 2", "프론트엔드", "프론트엔드 개발 역량을 확인하는 질문입니다."),
            InterviewQuestion(3, "Economics", "Level 2", "Level 2", "백엔드", "백엔드 아키텍처 설계에 관한 질문입니다."),
            InterviewQuestion(4, "Economics", "Level 2", "Level 2", "풀스택", "풀스택 개발 경험에 대한 질문입니다."),
            InterviewQuestion(5, "자기소개를 해주세요", "카카오", "Level 1", "인성", "본인에 대해 간단히 소개해주세요."),
            InterviewQuestion(6, "지원 동기는 무엇인가요?", "네이버", "Level 1", "인성", "우리 회사에 지원한 이유를 말해주세요."),
            InterviewQuestion(7, "팀워크 경험을 말해주세요", "삼성", "Level 2", "인성", "팀 프로젝트 경험과 역할을 설명해주세요."),
            InterviewQuestion(8, "기술적 도전 경험", "라인", "Level 3", "기술", "어려운 기술적 문제를 해결한 경험을 공유해주세요.")
        )
    }
    
    // 필터링된 질문 목록
    val filteredQuestions = questions.filter {
        (searchTitle.isEmpty() || it.title.contains(searchTitle, ignoreCase = true)) &&
        (searchCategory.isEmpty() || it.category.contains(searchCategory, ignoreCase = true))
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header - 피그마 디자인 매칭 (y: 0, height: 211)
            RepositoryHeader(
                searchTitle = searchTitle,
                onSearchTitleChange = { searchTitle = it },
                searchCategory = searchCategory,
                onSearchCategoryChange = { searchCategory = it },
                isExpanded = isExpanded,
                onExpandToggle = { isExpanded = !isExpanded },
                onUploadClick = { showUploadDialog = true }
            )
            
            // Questions List - 피그마 리스트 디자인 매칭
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp) // Bottom nav 공간
            ) {
                items(filteredQuestions) { question ->
                    QuestionItem(
                        question = question,
                        onClick = { selectedQuestion = question }
                    )
                    
                    // 구분선 - 피그마의 Vector 라인들
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 1.dp),
                        thickness = 2.dp,
                        color = Color(0xFFE5E5EA).copy(alpha = 0.27f)
                    )
                }
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
        
        // Question Detail Modal
        selectedQuestion?.let { question ->
            QuestionDetailModal(
                question = question,
                onDismiss = { selectedQuestion = null }
            )
        }

        // Excel Upload Dialog
        if (showUploadDialog) {
            ExcelUploadDialog(
                onDismiss = { showUploadDialog = false },
                onFileSelected = { uri ->
                    // TODO: Connect to ViewModel
                    println("File selected: $uri")
                }
            )
        }
    }
}

@Composable
private fun RepositoryHeader(
    searchTitle: String,
    onSearchTitleChange: (String) -> Unit,
    searchCategory: String,
    onSearchCategoryChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isExpanded) 250.dp else 210.dp), // 피그마 height: 211
        shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp) // 피그마 그림자
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 23.dp) // 피그마 x: 23
        ) {
            // Status Bar
            StatusBar()
            
            // Back Button and Upload Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* 뒤로가기 */ },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = StudyWithBlack
                    )
                }

                // Upload Button
                IconButton(
                    onClick = onUploadClick,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Excel 업로드",
                        tint = StudyWithYellow
                    )
                }
            }
            
            // Search Title Field - 피그마 위치 (y: 102)
            StudyWithTextField(
                value = searchTitle,
                onValueChange = onSearchTitleChange,
                placeholder = "제목",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 0.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp)) // y: 154 - 102 - 40 = 12
            
            // Search Category Field with Filter Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StudyWithTextField(
                    value = searchCategory,
                    onValueChange = onSearchCategoryChange,
                    placeholder = "종류",
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Filter Icon - 피그마 위치 (x: 316, y: 111)
                IconButton(
                    onClick = onExpandToggle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "필터",
                        tint = StudyWithBlack
                    )
                }
            }
            
            // Expanded Filter Options
            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "카테고리 필터",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = searchCategory == "백엔드",
                            onClick = { onSearchCategoryChange(if (searchCategory == "백엔드") "" else "백엔드") },
                            label = { Text("백엔드") }
                        )
                        FilterChip(
                            selected = searchCategory == "프론트엔드",
                            onClick = { onSearchCategoryChange(if (searchCategory == "프론트엔드") "" else "프론트엔드") },
                            label = { Text("프론트엔드") }
                        )
                        FilterChip(
                            selected = searchCategory == "인성",
                            onClick = { onSearchCategoryChange(if (searchCategory == "인성") "" else "인성") },
                            label = { Text("인성") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = StudyWithBlack
        )
        
        // 상태바 아이콘들 모두 제거
    }
}

@Composable
private fun QuestionItem(
    question: InterviewQuestion,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "itemScale"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                isPressed = true
                onClick()
            }
            .padding(horizontal = 23.dp, vertical = 16.dp) // 피그마 간격
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Education Icon - 피그마 Group 32 스타일
        Box(
            modifier = Modifier
                .size(30.dp) // 피그마 정확한 크기
                .background(
                    Color(0xFFC4C4C4).copy(alpha = 0.18f), // 피그마 fill_NUIZ0B
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // 아이콘 제거됨
        }
        
        Spacer(modifier = Modifier.width(10.dp))
        
        // Question Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = question.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, // 피그마 weight: 600
                color = StudyWithBlack
            )
            
            Spacer(modifier = Modifier.height(5.dp))
            
            Text(
                text = question.company,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal, // 피그마 weight: 400
                color = StudyWithBlack
            )
        }
        
        // Level Badge
        Card(
            modifier = Modifier.padding(start = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = StudyWithYellow.copy(alpha = 0.2f)
            )
        ) {
            Text(
                text = question.level,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = StudyWithBlack
            )
        }
    }
    
    // 클릭 상태 리셋
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun QuestionDetailModal(
    question: InterviewQuestion,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable { /* 클릭 전파 방지 */ },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "질문 상세정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyWithBlack
                    )
                    
                    TextButton(onClick = onDismiss) {
                        Text("닫기", color = StudyWithBlack)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Question Title
                Text(
                    text = "질문",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithGray
                )
                
                Text(
                    text = question.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Company and Category Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "회사명",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyWithGray
                        )
                        Text(
                            text = question.company,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "카테고리",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyWithGray
                        )
                        Text(
                            text = question.category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Level
                Text(
                    text = "난이도",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithGray
                )
                
                Card(
                    modifier = Modifier.padding(top = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StudyWithYellow.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = question.level,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithBlack
                    )
                }
                
                if (question.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "설명",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyWithGray
                    )
                    
                    Text(
                        text = question.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithBlack,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* 북마크 기능 */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StudyWithBlack
                        )
                    ) {
                        Text("북마크")
                    }
                    
                    Button(
                        onClick = { /* 면접 연습 시작 */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudyWithBlack,
                            contentColor = StudyWithYellow
                        )
                    ) {
                        Text("연습하기")
                    }
                }
            }
        }
    }
}
