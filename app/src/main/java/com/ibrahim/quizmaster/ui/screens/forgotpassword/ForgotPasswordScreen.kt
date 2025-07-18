package com.ibrahim.quizmaster.ui.screens.forgotpassword

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.viewmodel.ForgotPasswordViewModelModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: ForgotPasswordViewModelModel= viewModel() ){

    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    val resetPass by viewModel.resetPasswordState.collectAsState()
    LaunchedEffect(resetPass) {
        if (resetPass.first) {
            Toast.makeText(context, "Check your email.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else if (resetPass.second != null) {
            Toast.makeText(context, "Error: ${resetPass.second}", Toast.LENGTH_LONG).show()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text("Reset Password", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.hint_green), // Change border color when focused
                unfocusedBorderColor = colorResource(id = R.color.Gray), // Default border color
                cursorColor = colorResource(id = R.color.hint_green), // Cursor color
                focusedLabelColor = colorResource(id = R.color.hint_green), // Label color when focused
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
               viewModel.sendResetPassword(email)
            }, colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.hint_green),
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray

            ),
            shape = RoundedCornerShape(16.dp), // Rounded corners for smooth UI
            border = BorderStroke(
                2.dp,
                colorResource(id = R.color.black)
            ), // Border color
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Set button height
                .shadow(8.dp, shape = RoundedCornerShape(16.dp)) // Adds a soft shadow
                .clip(RoundedCornerShape(16.dp)) // Ensures proper clipping
                .background(colorResource(id = R.color.hint_green))
        ) {
            Text(
                "Send Reset Link", style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
