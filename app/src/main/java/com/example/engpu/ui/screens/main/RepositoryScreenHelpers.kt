package com.example.engpu.ui.screens.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engpu.data.supabase.Question
import com.example.engpu.ui.theme.*

@Composable
fun QuestionItemDB(
    question: Question,
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
            .padding(horizontal = 23.dp, vertical = 16.dp)
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    Color(0xFFC4C4C4).copy(alpha = 0.18f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📝",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Question Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = question.question,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudyWithBlack,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                question.company?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithGray
                    )
                }

                question.category?.let {
                    Text(
                        text = "• $it",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = StudyWithGray
                    )
                }
            }
        }

        // Year Badge (if available)
        question.question_at?.let { year ->
            Card(
                modifier = Modifier.padding(start = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StudyWithYellow.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = "$year",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithBlack
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
fun QuestionDetailModalDB(
    question: Question,
    onDismiss: () -> Unit,
    onEdit: ((Question) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editedQuestion by remember { mutableStateOf(question.question) }
    var editedCategory by remember { mutableStateOf(question.category ?: "") }
    var editedCompany by remember { mutableStateOf(question.company ?: "") }
    var editedYear by remember { mutableStateOf(question.question_at?.toString() ?: "") }
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
                .clickable { /* Prevent click propagation */ },
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

                // Question Text
                Text(
                    text = "질문",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyWithGray
                )

                Text(
                    text = question.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyWithBlack,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Company and Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    question.company?.let { company ->
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "회사",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = StudyWithGray
                            )
                            Text(
                                text = company,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudyWithBlack,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    question.category?.let { category ->
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "카테고리",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = StudyWithGray
                            )
                            Text(
                                text = category,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudyWithBlack,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Question Year
                question.question_at?.let { year ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "출제 연도",
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
                            text = "${year}년",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyWithBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Delete Button
                    onDelete?.let {
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "삭제",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // Edit Button
                    onEdit?.let {
                        Button(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudyWithYellow
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "수정",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudyWithBlack
                            )
                        }
                    }
                }
            }
        }

        // Edit Dialog
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("질문 수정") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editedQuestion,
                            onValueChange = { editedQuestion = it },
                            label = { Text("질문") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedCategory,
                            onValueChange = { editedCategory = it },
                            label = { Text("카테고리") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedCompany,
                            onValueChange = { editedCompany = it },
                            label = { Text("회사") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedYear,
                            onValueChange = { editedYear = it },
                            label = { Text("출제연도") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEdit?.invoke(
                                question.copy(
                                    question = editedQuestion,
                                    category = editedCategory.ifBlank { null },
                                    company = editedCompany.ifBlank { null },
                                    question_at = editedYear.toIntOrNull()
                                )
                            )
                            showEditDialog = false
                            onDismiss()
                        }
                    ) {
                        Text("저장")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("질문 삭제") },
                text = { Text("이 질문을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDelete?.invoke(question.id)
                            showDeleteDialog = false
                            onDismiss()
                        }
                    ) {
                        Text("삭제", color = Color(0xFFF44336))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}

@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String?, String?, Int?) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("질문 추가") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("질문 *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("카테고리") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("예: 기술면접, 인성면접") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("회사") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("예: 네이버, 카카오") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("출제연도") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("예: 2024") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (questionText.isNotBlank()) {
                        onAdd(
                            questionText.trim(),
                            category.ifBlank { null },
                            company.ifBlank { null },
                            year.toIntOrNull()
                        )
                    }
                },
                enabled = questionText.isNotBlank()
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
