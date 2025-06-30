package com.ibrahim.quizmaster.data.model

data class QuizQuestion(
    val id: String = "",
    val numberOfQuestion:Int=0,
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)
