package com.ibrahim.quizmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.viewmodel.AuthViewModel

@Composable
fun LoginOutlineButton(text:String, authViewModel: AuthViewModel, email:String, password:String, navController:NavController){
    OutlinedButton(
        onClick = {
            authViewModel.login(email, password) { destination ->
                navController.navigate(destination) { popUpTo("login") { inclusive = true } }
            }
        },
        colors = ButtonDefaults.buttonColors(
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
            .background(colorResource(id = R.color.hint_green)),
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
        )
    }
}

@Composable
fun SignUpOutlineButton(text:String, authViewModel: AuthViewModel, email:String, password:String, navController:NavController){
    OutlinedButton(
        onClick = {
            authViewModel.signUp(email, password) { destination ->
                navController.navigate(destination) { popUpTo("signUp") { inclusive = true } }
            }
        },
        colors = ButtonDefaults.buttonColors(
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
            .background(colorResource(id = R.color.hint_green)),
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
        )
    }



}

@Composable
fun BasicButton(text: String,){
    OutlinedButton(
        onClick = {

        },
        colors = ButtonDefaults.buttonColors(
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
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
        )
    }
}


