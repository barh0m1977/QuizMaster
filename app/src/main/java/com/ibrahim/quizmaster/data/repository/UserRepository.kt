package com.ibrahim.quizmaster.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ibrahim.quizmaster.data.model.UserScore
import kotlinx.coroutines.tasks.await

class UserRepository {
    object FirebaseRepository {
        suspend fun getUserData(): UserScore? {
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val userId = auth.currentUser?.uid

            if (userId != null) {
                return try {
                    val documentSnapshot = firestore.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    val userName = documentSnapshot.getString("userName") ?: ""
                    val email = documentSnapshot.getString("email") ?: ""
                    val image = documentSnapshot.getString("image") ?: ""
                    val score = documentSnapshot.getLong("score")?.toInt() ?: 0

                    Log.d("UserRepository", "User data fetched: $userName, $email, $score")

                    UserScore(
                        userId = userId,
                        userName = userName,
                        email = email,
                        image = image,
                        score = score
                    )

                } catch (e: Exception) {
                    Log.e("UserRepository", "Error fetching user: ${e.message}")
                    null
                }
            } else {
                Log.e("UserRepository", "User not logged in!")
                return null
            }
        }

    }

    fun saveScore(score: Int, numberOfQuestion: Int) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid
        val userUpdate = hashMapOf(
            "score" to score,
            "numberOfQuestion" to numberOfQuestion
        )
        if (userId != null) {
            return try {
                val documentSnapshot = firestore.collection("users")
                    .document(userId)
                    .update(userUpdate as Map<String, Int>)
                    .addOnSuccessListener {
                        Log.d("scoreUpdate", "done")
                    }
            } catch (e: Exception) {
            }

        }
    }

    object UpdateData{
        @SuppressLint("StaticFieldLeak")
        private val db = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()
        fun updateUserProfile(username: String, email: String, onResult: (Boolean) -> Unit) {

            val userId = auth.currentUser?.uid ?: return onResult(false)

            val updates = hashMapOf<String, Any>(
                "userName" to username,
                "email" to email
            )

            db.collection("users")
                .document(userId)
                .update(updates)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }

        fun updateUserProfile(image: String, onResult: (Boolean) -> Unit){
            val userId = auth.currentUser?.uid ?: return onResult(false)
            val updates = hashMapOf<String, Any>(
                "image" to image
            )
            db.collection("users")
                .document(userId)
                .update(updates)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
    }

    // Assuming the user progress (score and numberOfQuestion) is stored in Firestore
    fun getUserScoreAndLastQuestion(callback: (Int?, Int?) -> Unit) {
         val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val userScore = document.getLong("score")?.toInt()
                    val numberOfQuestion = document.getLong("numberOfQuestion")?.toInt()
                    callback(userScore, numberOfQuestion)
                }
                .addOnFailureListener {
                    callback(null, null)
                }
        } else {
            callback(null, null)
        }
    }


    fun checkUserScore(): Int {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid
        var score: Int = 0
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        score = document.getLong("score")?.toInt()!!
                    }
                }
        }
        return score
    }
}
