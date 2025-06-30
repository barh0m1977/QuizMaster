package com.ibrahim.quizmaster.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(userId: String, email: String) {
        sharedPreferences.edit()
            .putString("user_id", userId)
            .putString("email", email)
            .apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun clearUser() {
        sharedPreferences.edit().clear().apply()
    }
}
