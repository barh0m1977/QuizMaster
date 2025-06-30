package com.ibrahim.quizmaster.data.repository

import android.util.Log
import com.ibrahim.quizmaster.data.model.UserScore
import com.google.firebase.firestore.FirebaseFirestore

class ScoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // Function to save a user's score
    fun saveUserScore(userScore: UserScore) {
        db.collection("scores").add(userScore)
            .addOnSuccessListener {
                Log.d("Firestore", "Score saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving score", e)
            }
    }

    // Function to retrieve the top scores
    fun getTopScores(callback: (List<UserScore>) -> Unit) {
        db.collection("users")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val scores = result.documents.mapNotNull { it.toObject(UserScore::class.java) }
                callback(scores)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving top scores", e)
            }
    }

    fun getKingQuiz(callback: (List<UserScore>)->Unit ){
        db.collection("users")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val scores = result.documents.mapNotNull { it.toObject(UserScore::class.java) }
                callback(scores)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving top scores", e)
            }
    }
}