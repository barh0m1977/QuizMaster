package com.ibrahim.quizmaster.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.ui.components.LoginOutlineButton
import com.ibrahim.quizmaster.ui.components.LocaleOutlinedTextField
import com.ibrahim.quizmaster.ui.components.SignUpOutlineButton
import com.ibrahim.quizmaster.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    authViewModel.loginSuccess = { userId ->
        navController.navigate("quiz")  // Navigate to main screen on success
    }

    authViewModel.loginError = { message ->
        errorMessage = message
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.sign_up_title), style = MaterialTheme.typography.headlineLarge)


        LocaleOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.email_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            visualTransformation = VisualTransformation.None,
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

        SignUpOutlineButton(
            text = stringResource(id = R.string.sign_up_title),
            authViewModel = authViewModel,
            email = email,
            password = password,
            navController = navController
        )

        TextButton(onClick = { navController.navigate("login") }) {
            Text(text = stringResource(id = R.string.login_btn), color = colorResource(id = R.color.hint_green))

        }
    }
}
