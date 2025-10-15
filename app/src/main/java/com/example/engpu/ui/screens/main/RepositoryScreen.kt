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

@Composable
fun RepositoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    questions: List<com.example.engpu.data.supabase.Question> = emptyList(),
    categories: List<String> = emptyList(),
    isLoading: Boolean = false,
    onUploadExcel: (android.net.Uri) -> Unit = {},
    onEditQuestion: ((com.example.engpu.data.supabase.Question) -> Unit)? = null,
    onDeleteQuestion: ((String) -> Unit)? = null
) {
    var searchTitle by remember { mutableStateOf("") }
    var searchCategory by remember { mutableStateOf("") }
    var selectedQuestion by remember { mutableStateOf<com.example.engpu.data.supabase.Question?>(null) }
    var isExpanded by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    
    // 필터링된 질문 목록
    val filteredQuestions = questions.filter {
        (searchTitle.isEmpty() || it.question.contains(searchTitle, ignoreCase = true)) &&
        (searchCategory.isEmpty() || (it.category?.contains(searchCategory, ignoreCase = true) ?: false))
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
                categories = categories,
                isExpanded = isExpanded,
                onExpandToggle = { isExpanded = !isExpanded },
                onUploadClick = { showUploadDialog = true }
            )
            
            // Questions List - 피그마 리스트 디자인 매칭
            if (isLoading) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StudyWithYellow)
                }
            } else if (filteredQuestions.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "질문이 없습니다\nExcel 파일을 업로드하세요",
                        fontSize = 16.sp,
                        color = StudyWithGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp) // Bottom nav 공간
                ) {
                    items(filteredQuestions) { question ->
                        QuestionItemDB(
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
        
        // Excel Upload Dialog
        if (showUploadDialog) {
            ExcelUploadDialog(
                onDismiss = { showUploadDialog = false },
                onFileSelected = { uri ->
                    onUploadExcel(uri)
                    showUploadDialog = false
                }
            )
        }

        // Question Detail Modal
        selectedQuestion?.let { question ->
            QuestionDetailModalDB(
                question = question,
                onDismiss = { selectedQuestion = null },
                onEdit = onEditQuestion,
                onDelete = onDeleteQuestion
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
    categories: List<String>,
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

                    if (categories.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.chunked(3).forEach { rowCategories ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowCategories.forEach { category ->
                                        FilterChip(
                                            selected = searchCategory == category,
                                            onClick = { onSearchCategoryChange(if (searchCategory == category) "" else category) },
                                            label = { Text(category) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    // Fill remaining space if not full row
                                    repeat(3 - rowCategories.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
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
