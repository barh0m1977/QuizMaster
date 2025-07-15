package com.ibrahim.quizmaster.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ibrahim.quizmaster.data.model.QuizQuestion
import kotlinx.coroutines.tasks.await

class QuizRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAllQuestions(): List<QuizQuestion> {
        val snapshot = db.collection("questions").orderBy("numberOfQuestion").get().await()
        return snapshot.documents.mapNotNull { doc ->
            try {
                QuizQuestion(
                    id = doc.id,
                    numberOfQuestion = doc.getLong("numberOfQuestion")?.toInt() ?: 0,
                    question = doc.getString("question") ?: "",
                    options = (doc["options"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    correctAnswer = doc.getString("correctAnswer") ?: "")
                } catch (e: Exception) {
                null
            }
        }
    }
    fun getQuestions(callback: (List<QuizQuestion>) -> Unit, onError: (Exception) -> Unit) {
        db.collection("questions") .orderBy("numberOfQuestion") // Sort by the field
             .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError(Exception("No questions found in Firestore"))
                    return@addOnSuccessListener
                }

                val questions = result.documents.mapNotNull { doc ->
                    try {
                        QuizQuestion(
                            id = doc.id,
                            numberOfQuestion = doc.getLong("numberOfQuestion")?.toInt() ?: 0,
                            question = doc.getString("question") ?: "",
                            options = (doc["options"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                            correctAnswer = doc.getString("correctAnswer") ?: ""

                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                callback(questions)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
    fun getQuestions(question: String,callback: (List<QuizQuestion>) -> Unit ,onError: (Exception) -> Unit){
        db.collection("questions").whereEqualTo("question",question).get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError(Exception("No questions found in Firestore"))
                    return@addOnSuccessListener
                }

                val questions = result.documents.mapNotNull { doc ->
                    try {
                        QuizQuestion(
                            id = doc.id,
                            numberOfQuestion = doc.getLong("numberOfQuestion")?.toInt() ?: 0,
                            question = doc.getString("question") ?: "",
                            options = (doc["options"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                            correctAnswer = doc.getString("correctAnswer") ?: ""

                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                callback(questions)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

}
