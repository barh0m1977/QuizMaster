package com.ibrahim.quizmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibrahim.quizmaster.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModelModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _resetPasswordState = MutableStateFlow<Pair<Boolean, String?>>(false to null)
    val resetPasswordState: StateFlow<Pair<Boolean, String?>> = _resetPasswordState

    fun sendResetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email) { success, message ->
                _resetPasswordState.value = success to message
            }
        }
    }
}


