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
                    println("📝 [Excel] Processing row ${index + 2}: question='${question.question}', category='${question.category}', company='${question.company}', year=${question.question_at}")

                    // UPSERT 로직: 동일한 question이 있으면 업데이트, 없으면 추가
                    val result = interviewRepository.addQuestion(
                        question = question.question,
                        category = question.category,
                        company = question.company,
                        questionAt = question.question_at
                    )

                    if (result.isSuccess) {
                        successCount++
                        println("✅ [Excel] Row ${index + 2} inserted successfully")
                    } else {
                        failureCount++
                        val error = result.exceptionOrNull()?.message ?: "Unknown error"
                        errors.add("Row ${index + 2}: $error")
                        println("❌ [Excel] Row ${index + 2} failed: $error")
                    }
                } catch (e: Exception) {
                    failureCount++
                    errors.add("Row ${index + 2}: ${e.message}")
                    println("❌ [Excel] Row ${index + 2} exception: ${e.message}")
                    e.printStackTrace()
                }
            }

            println("📊 [Excel] Upload complete: $successCount success, $failureCount failed out of ${questions.size} total")
            if (errors.isNotEmpty()) {
                println("🔍 [Excel] First 5 errors:")
                errors.take(5).forEach { println("  - $it") }
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

            println("📋 [Excel] Header mapping: $columnIndices")

            if (columnIndices["question"] == null) {
                throw Exception("필수 컬럼(question)을 찾을 수 없습니다. 발견된 컬럼: ${columnIndices.keys}")
            }

            // 데이터 행 읽기 (2번째 행부터)
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                // question 컬럼은 필수
                val questionColIndex = columnIndices["question"] ?: 0
                var questionText = getCellValueAsString(row.getCell(questionColIndex))?.trim()

                // question이 비어있거나 숫자만 있는 경우 스킵
                if (questionText.isNullOrBlank()) {
                    println("⏭️ [Excel] Row ${rowIndex + 1} skipped: empty question")
                    continue
                }

                // question이 4자리 숫자만 있는 경우 (연도일 가능성) 스킵
                if (questionText.matches(Regex("^\\d{4}$"))) {
                    println("⏭️ [Excel] Row ${rowIndex + 1} skipped: question is year-like '$questionText'")
                    continue
                }

                // question이 너무 짧으면 (3자 이하) 스킵
                if (questionText.length <= 3) {
                    println("⏭️ [Excel] Row ${rowIndex + 1} skipped: question too short '$questionText'")
                    continue
                }

                // question 텍스트에서 연도 제거 (끝에 2000-2099 범위의 4자리 숫자)
                questionText = questionText.replace(Regex("\\s*20\\d{2}\\s*$"), "").trim()

                // 선택적 컬럼들
                val category = columnIndices["category"]?.let {
                    getCellValueAsString(row.getCell(it))?.trim()
                }?.takeIf { it.isNotBlank() && it.length > 1 }

                val company = columnIndices["company"]?.let {
                    getCellValueAsString(row.getCell(it))?.trim()
                }?.takeIf { it.isNotBlank() && it.length > 1 }

                val questionAt = columnIndices["question_at"]?.let { colIndex ->
                    val cell = row.getCell(colIndex)
                    try {
                        when (cell?.cellType) {
                            org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toInt()
                            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue.trim().toIntOrNull()
                            else -> null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                // 디버그: 각 행의 실제 셀 값 출력 (처음 5개만)
                if (rowIndex <= 5) {
                    println("🔍 [Excel] Row ${rowIndex + 1} raw data:")
                    for (cellIndex in 0 until row.lastCellNum.coerceAtMost(4)) {
                        val cellValue = getCellValueAsString(row.getCell(cellIndex))
                        println("   Cell[$cellIndex] = '$cellValue'")
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
     * Cell 값을 안전하게 String으로 변환
     * NUMERIC, STRING, BOOLEAN, FORMULA 타입 모두 처리
     */
    private fun getCellValueAsString(cell: org.apache.poi.ss.usermodel.Cell?): String? {
        if (cell == null) return null

        return try {
            when (cell.cellType) {
                org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                    // 날짜인지 확인
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        cell.dateCellValue.toString()
                    } else {
                        // 정수면 정수로, 소수면 소수로
                        val numValue = cell.numericCellValue
                        if (numValue % 1.0 == 0.0) {
                            numValue.toInt().toString()
                        } else {
                            numValue.toString()
                        }
                    }
                }
                org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
                org.apache.poi.ss.usermodel.CellType.FORMULA -> {
                    try {
                        cell.stringCellValue
                    } catch (e: Exception) {
                        try {
                            cell.numericCellValue.toString()
                        } catch (e2: Exception) {
                            null
                        }
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
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
            val headerName = getCellValueAsString(cell)?.trim()?.lowercase() ?: continue

            println("📝 [Excel] Column $cellIndex: '$headerName'")

            when {
                // question 컬럼 - 정확한 매칭 우선
                headerName == "question" || headerName == "질문" -> {
                    columnMap["question"] = cellIndex
                    println("✅ [Excel] Mapped 'question' to column $cellIndex")
                }
                // category 컬럼
                headerName == "category" || headerName == "카테고리" || headerName == "분류" -> {
                    columnMap["category"] = cellIndex
                    println("✅ [Excel] Mapped 'category' to column $cellIndex")
                }
                // company 컬럼
                headerName == "company" || headerName == "회사" || headerName == "기업" -> {
                    columnMap["company"] = cellIndex
                    println("✅ [Excel] Mapped 'company' to column $cellIndex")
                }
                // question_at 컬럼
                headerName == "question_at" || headerName == "출제" || headerName == "연도" || headerName == "year" -> {
                    columnMap["question_at"] = cellIndex
                    println("✅ [Excel] Mapped 'question_at' to column $cellIndex")
                }
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
