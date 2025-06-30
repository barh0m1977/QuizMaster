package com.ibrahim.quizmaster.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.data.UserPreferences
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, sharedPreferences: UserPreferences) {
    LaunchedEffect(Unit) {
        val userEmail = sharedPreferences.getUserEmail()
        delay(2000) // Optional: simulate splash delay

        if (userEmail != null && userEmail != "lubbadibrahim0@gmail.com") {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        }
        if (userEmail =="lubbadibrahim0@gmail.com") {
            navController.navigate("dashBord") {
                popUpTo("splash") { inclusive = true }
            }
        }
        else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .height(500.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.rafik),
                contentDescription = "Welcome Image",
                modifier = Modifier
                    .size(500.dp)

            )

            Text(
                text ="${ stringResource(id = R.string.splash_title)} ${stringResource(id = R.string.app_name)}",
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 24.dp),
                style = TextStyle(
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

}