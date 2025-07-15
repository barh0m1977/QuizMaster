package com.ibrahim.quizmaster.domain.usecase

import com.ibrahim.quizmaster.data.model.QuizQuestion
import com.ibrahim.quizmaster.data.repository.QuizRepository

class GetAllQuestionsUseCase (private val quizRepository: QuizRepository) {
    suspend operator fun invoke(): List<QuizQuestion> {
        return quizRepository.getAllQuestions()
    }
}