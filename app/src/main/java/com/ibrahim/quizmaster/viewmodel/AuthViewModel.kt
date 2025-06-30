package com.ibrahim.quizmaster.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ibrahim.quizmaster.data.UserPreferences

class AuthViewModel(application: Application) :AndroidViewModel(application) {
    private val userPreferences: UserPreferences = UserPreferences(application)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var loginSuccess: ((String) -> Unit)? = null
    var loginError: ((String) -> Unit)? = null

    fun login(email: String, password: String, onNavigate: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                userPreferences.saveUser(userId, email)  // Save user info
                loginSuccess?.invoke(userId)
                // Check if the logged-in email is "lubbadibrahim0@gmail.com"
                if (email == "lubbadibrahim0@gmail.com") {
                    onNavigate("dashBord")  // Navigate to SubmitQuestionScreen
                } else {
                    onNavigate("main")  // Navigate to the normal quiz flow
                }
            }
            .addOnFailureListener { e ->
                loginError?.invoke(e.message ?: "Login failed")
            }
    }

    fun signUp(email: String, password: String, onNavigate: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                saveUserToFirestore(userId, email,email,"",0)
                userPreferences.saveUser(userId, email)  // Save user info
                loginSuccess?.invoke(userId)
                onNavigate("main")
            }
            .addOnFailureListener { e ->
                loginError?.invoke(e.message ?: "Sign-up failed")
            }
    }
    private fun saveUserToFirestore(userId: String, email: String,userName:String,image:String,score:Int) {
        val user = hashMapOf(
            "userId" to userId,
            "email" to email,
            "userName" to  userName,
            "image" to image,
            "score" to score
        )
        db.collection("users").document(userId).set(user)
    }
}
