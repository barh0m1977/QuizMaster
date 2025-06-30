package com.ibrahim.quizmaster.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ibrahim.quizmaster.data.model.QuizQuestion
import com.ibrahim.quizmaster.data.model.UserScore
import com.ibrahim.quizmaster.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID


/**
 * get data from repository
 * save status of variable
 * customize data to display it in screen
 */

@Suppress("UNCHECKED_CAST")
class UserViewModel : ViewModel() {

    private val _user = MutableStateFlow(UserScore())
    val user: StateFlow<UserScore> = _user
        .onStart {
            fetchUserData()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 5000,
            ),
            initialValue = UserScore()
        )

    private val userRepository = UserRepository()


    fun fetchUserData() {
        viewModelScope.launch {
            val userData = UserRepository.FirebaseRepository.getUserData()
            Log.d("UserViewModel", "Fetched result: $userData") // <- Add this
            userData?.let {
                _user.value = it
            }
        }
    }

    fun updateUserProfile(username: String, email: String) {
        UserRepository.UpdateData.updateUserProfile(username, email) { success ->
            if (success) {
                _user.value = _user.value.copy(userName = username, email = email)
            } else {
                // Handle error (Snackbar, Toast, log, etc.)
            }
        }
    }
    fun updateUserProfile(image: String) {
        UserRepository.UpdateData.updateUserProfile(image) { success ->
            if (success) {
                _user.value = _user.value.copy(image = image)
            } else {
                // Handle error (Snackbar, Toast, log, etc.)
            }
        }
    }


    fun uploadProfileImage(uri: Uri, context: Context) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateUserProfile(downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }








}