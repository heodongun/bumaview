package com.example.engpu.ui.screens.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
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
import com.example.engpu.data.repository.UploadResult
import com.example.engpu.ui.components.StudyWithButton
import com.example.engpu.ui.theme.*

@Composable
fun ExcelUploadDialog(
    onDismiss: () -> Unit,
    onFileSelected: (Uri) -> Unit,
    uploadResult: UploadResult? = null,
    isLoading: Boolean = false
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onFileSelected(it) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "질문 업로드",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyWithBlack
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = StudyWithGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Excel 파일(.xlsx, .xls)을 업로드하여 면접 질문을 추가할 수 있습니다.",
                    fontSize = 14.sp,
                    color = StudyWithGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Format info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StudyWithYellow.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "필수 형식:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyWithBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• question (필수): 질문 내용\n• category (선택): 카테고리\n• company (선택): 회사명\n• question_at (선택): 출제년도",
                            fontSize = 12.sp,
                            color = StudyWithGray,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Loading state
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = StudyWithYellow,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "업로드 중...",
                                fontSize = 14.sp,
                                color = StudyWithGray
                            )
                        }
                    }
                } else if (uploadResult != null) {
                    // Result display
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (uploadResult.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (uploadResult.isSuccess) Color(0xFF4CAF50) else Color(0xFFFF5722),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (uploadResult.isSuccess) "업로드 성공!" else "업로드 일부 실패",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uploadResult.isSuccess) Color(0xFF4CAF50) else Color(0xFFFF5722)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Statistics
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = StudyWithGray.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                ResultRow("총 행 수", uploadResult.totalRows.toString())
                                Spacer(modifier = Modifier.height(8.dp))
                                ResultRow("성공", uploadResult.successCount.toString(), Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.height(8.dp))
                                ResultRow("실패", uploadResult.failureCount.toString(), if (uploadResult.failureCount > 0) Color(0xFFFF5722) else StudyWithGray)

                                if (uploadResult.errors.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "오류 목록:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StudyWithBlack
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    uploadResult.errors.take(3).forEach { error ->
                                        Text(
                                            text = "• $error",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFF5722),
                                            lineHeight = 16.sp
                                        )
                                    }
                                    if (uploadResult.errors.size > 3) {
                                        Text(
                                            text = "... 외 ${uploadResult.errors.size - 3}개",
                                            fontSize = 11.sp,
                                            color = StudyWithGray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    StudyWithButton(
                        text = "확인",
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        backgroundColor = StudyWithBlack,
                        textColor = StudyWithYellow
                    )
                } else {
                    // Upload button
                    StudyWithButton(
                        text = "Excel 파일 선택",
                        onClick = {
                            filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        backgroundColor = StudyWithYellow,
                        textColor = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StudyWithGray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("취소")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = StudyWithBlack
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = StudyWithGray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}
