package com.ibrahim.quizmaster.data.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val aut = FirebaseAuth.getInstance()

    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        aut.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful, task.exception?.message)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }

    }
}