package com.example.engpu.data.repository

import android.content.Context
import android.net.Uri
import com.example.engpu.data.supabase.Question
import com.example.engpu.data.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

/**
 * Excel 파일 업로드 및 Question 테이블 자동 반영을 위한 Repository
 *
 * 기능:
 * - Excel 파일(.xlsx, .xls) 파싱
 * - question, category, company, question_at 컬럼 추출
 * - Supabase Question 테이블에 자동 Insert
 * - 중복 데이터 처리 (UPSERT)
 */
class ExcelRepository(private val context: Context) {

    private val interviewRepository = InterviewRepository()

    /**
     * Excel 파일을 읽어서 Question 테이블에 업로드
     *
     * @param uri Excel 파일 Uri
     * @return Result<UploadResult> 업로드 결과 (성공/실패 개수)
     */
    suspend fun uploadExcelToQuestions(uri: Uri): Result<UploadResult> = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                return@withContext Result.failure(Exception("파일을 열 수 없습니다"))
            }

            val questions = parseExcelFile(inputStream)
            inputStream.close()

            if (questions.isEmpty()) {
                return@withContext Result.failure(Exception("엑셀 파일에 유효한 데이터가 없습니다"))
            }

            // Batch insert with error tracking
            var successCount = 0
            var failureCount = 0
            val errors = mutableListOf<String>()

            questions.forEachIndexed { index, question ->
                try {
                    // UPSERT 로직: 동일한 question이 있으면 업데이트, 없으면 추가
                    val result = interviewRepository.addQuestion(
                        question = question.question,
                        category = question.category,
                        company = question.company,
                        questionAt = question.question_at
                    )

                    if (result.isSuccess) {
                        successCount++
                    } else {
                        failureCount++
                        errors.add("Row ${index + 2}: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    failureCount++
                    errors.add("Row ${index + 2}: ${e.message}")
                }
            }

            Result.success(
                UploadResult(
                    totalRows = questions.size,
                    successCount = successCount,
                    failureCount = failureCount,
                    errors = errors
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception("Excel 파일 처리 중 오류: ${e.message}"))
        }
    }

    /**
     * Excel 파일 파싱
     *
     * 예상 컬럼 구조:
     * | question | category | company | question_at |
     *
     * @param inputStream Excel 파일 InputStream
     * @return List<Question> 파싱된 질문 리스트
     */
    private fun parseExcelFile(inputStream: InputStream): List<Question> {
        val questions = mutableListOf<Question>()

        try {
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // 첫 번째 시트만 읽기

            // 헤더 행 검증 (첫 번째 행)
            val headerRow = sheet.getRow(0) ?: return emptyList()
            val columnIndices = mapHeaderColumns(headerRow)

            if (columnIndices.isEmpty()) {
                throw Exception("필수 컬럼(question)을 찾을 수 없습니다")
            }

            // 데이터 행 읽기 (2번째 행부터)
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                // question 컬럼은 필수
                val questionText = row.getCell(columnIndices["question"] ?: 0)
                    ?.stringCellValue
                    ?.trim()

                if (questionText.isNullOrBlank()) continue

                // 선택적 컬럼들
                val category = columnIndices["category"]?.let {
                    row.getCell(it)?.stringCellValue?.trim()
                }

                val company = columnIndices["company"]?.let {
                    row.getCell(it)?.stringCellValue?.trim()
                }

                val questionAt = columnIndices["question_at"]?.let { colIndex ->
                    try {
                        row.getCell(colIndex)?.numericCellValue?.toInt()
                    } catch (e: Exception) {
                        null
                    }
                }

                questions.add(
                    Question(
                        question = questionText,
                        category = category,
                        company = company,
                        question_at = questionAt
                    )
                )
            }

            workbook.close()
        } catch (e: Exception) {
            throw Exception("Excel 파싱 오류: ${e.message}")
        }

        return questions
    }

    /**
     * 헤더 행에서 컬럼 인덱스 매핑
     *
     * @param headerRow 헤더 행
     * @return Map<String, Int> 컬럼명 -> 인덱스 매핑
     */
    private fun mapHeaderColumns(headerRow: org.apache.poi.ss.usermodel.Row): Map<String, Int> {
        val columnMap = mutableMapOf<String, Int>()

        for (cellIndex in 0 until headerRow.lastCellNum) {
            val cell = headerRow.getCell(cellIndex) ?: continue
            val headerName = cell.stringCellValue.trim().lowercase()

            when {
                headerName.contains("question") || headerName.contains("질문") ->
                    columnMap["question"] = cellIndex
                headerName.contains("category") || headerName.contains("카테고리") || headerName.contains("분류") ->
                    columnMap["category"] = cellIndex
                headerName.contains("company") || headerName.contains("회사") || headerName.contains("기업") ->
                    columnMap["company"] = cellIndex
                headerName.contains("question_at") || headerName.contains("출제") || headerName.contains("연도") ->
                    columnMap["question_at"] = cellIndex
            }
        }

        return columnMap
    }
}

/**
 * Excel 업로드 결과 데이터 클래스
 */
data class UploadResult(
    val totalRows: Int,
    val successCount: Int,
    val failureCount: Int,
    val errors: List<String> = emptyList()
) {
    val isSuccess: Boolean get() = failureCount == 0
    val successRate: Float get() = if (totalRows > 0) successCount.toFloat() / totalRows else 0f
}
