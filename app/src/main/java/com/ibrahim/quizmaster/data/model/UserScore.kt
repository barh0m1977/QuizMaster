package com.ibrahim.quizmaster.data.model


/*
*data variable we need it to display
* variable we need get it from Firebase
 */

data class UserScore(
    val userId: String = "",
    var userName: String = "",
    var email: String = "",
    val image: String = "",
    val score: Int = 0,
    val lastAnsQuestionId:String=""

)