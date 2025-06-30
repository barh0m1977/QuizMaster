package com.ibrahim.quizmaster.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.data.UserPreferences
import com.ibrahim.quizmaster.ui.components.LoginOutlineButton
import com.ibrahim.quizmaster.ui.components.LocaleOutlinedTextField
import com.ibrahim.quizmaster.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userPreferences = remember { UserPreferences(context) }
    var userEmail by remember { mutableStateOf<String?>(null) }

    // ✅ Fetch saved email in a coroutine to avoid UI blocking
//    LaunchedEffect(Unit) {
//        userEmail = userPreferences.getUserEmail()
//    }
//
//    // ✅ Navigate if already logged in
//    userEmail?.let {
//        LaunchedEffect(Unit) {
//            navController.navigate("main") {
//                popUpTo("login") { inclusive = true }
//            }
//        }
//    }

    // ✅ Success Callback
    authViewModel.loginSuccess = { userId ->
        coroutineScope.launch {
            userPreferences.saveUser(userId, email)
        }
        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
        navController.navigate("main") { popUpTo("login") { inclusive = true } }
    }

    // ✅ Error Callback
    authViewModel.loginError = { message ->
        errorMessage = message
        Toast.makeText(context, "Login Failed: $message", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.login_title), style = MaterialTheme.typography.headlineLarge)


        LocaleOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.email_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            visualTransformation = VisualTransformation.None
        )
        LocaleOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(id = R.string.pass_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginOutlineButton(
            text = stringResource(id = R.string.login_title),
            authViewModel = authViewModel,
            email = email,
            password = password,
            navController = navController
        )

        TextButton(onClick = { navController.navigate("signup") }) {
            Text(
                stringResource(id = R.string.sign_up_btn),
                color = colorResource(id = R.color.hint_green)
            )

        }
    }
}
