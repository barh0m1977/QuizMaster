package com.ibrahim.quizmaster.utils

import android.content.Context
import android.net.Uri
import android.util.Log

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import com.ibrahim.quizmaster.data.model.QuizQuestion
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object XlsxParser{
    fun parseQuestionFromExcel(context: Context, uri: Uri): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        val inputStream = context.contentResolver.openInputStream(uri)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        fun getCellValueAsString(cell: Cell?): String {
            if (cell == null) return ""
            return when (cell.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> {
                    val value = cell.numericCellValue
                    if (value == Math.floor(value)) value.toLong().toString() else value.toString()
                }
                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                CellType.FORMULA -> when (cell.cachedFormulaResultType) {
                    CellType.STRING -> cell.stringCellValue.trim()
                    CellType.NUMERIC -> cell.numericCellValue.toString()
                    else -> ""
                }
                else -> ""
            }
        }

        for (row in sheet.drop(1)) {
            try {
                val numberOfQuizQuestion = row.getCell(0)?.numericCellValue?.toInt() ?: continue
                val question = getCellValueAsString(row.getCell(1))
                val optionA = getCellValueAsString(row.getCell(2))
                val optionB = getCellValueAsString(row.getCell(3))
                val optionC = getCellValueAsString(row.getCell(4))
                val correctAnswer = getCellValueAsString(row.getCell(5))

                val options = arrayListOf(optionA, optionB, optionC)

                questions.add(
                    QuizQuestion(
                        id = IdUtils.generateRandomId(),
                        numberOfQuestion = numberOfQuizQuestion,
                        question = question,
                        options = options,
                        correctAnswer = correctAnswer
                    )
                )
            } catch (e: Exception) {
                Log.e("ExcelParser", "Error parsing row: ${e.message}", e)
            }
        }

        workbook.close()
        return questions
    }

}