package com.ibrahim.quizmaster.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.data.UserPreferences
import com.ibrahim.quizmaster.ui.components.rememberWifiInternetConnection
import com.ibrahim.quizmaster.viewmodel.QuizViewModel
import com.ibrahim.quizmaster.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val viewModel: QuizViewModel = viewModel()  // Get the ViewModel
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val userViewModel = UserViewModel().user.value

    // Get user email to check if logged in
    val userEmail = userPreferences.getUserEmail()
    // Track Wi-Fi status
    val isWifiConnected by rememberWifiInternetConnection(context)

    // Use selected image if available, otherwise load from Firebase, fallback to default
    val painter = rememberAsyncImagePainter(
        model = imageUri.value,
        placeholder = painterResource(id = R.drawable.profile),
        error = painterResource(id = R.drawable.profile)
    )
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.quiz_time)) },
            actions = {
                IconButton(onClick = {
                    // navigate to profile screen
                    navController.navigate("profile")
                }) {
                    Image(
                        painter = painter, // Your icon here
                        contentDescription = "Profile",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        )
    }
    ) { padding ->

//                    AppNavGraph(viewModel, authViewModel)  // Pass the ViewModel to the NavGraph
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display an image icon
            Image(
                painter = painterResource(id = R.drawable.rafik),  // Replace with your image resource
                contentDescription = "Quiz Icon",
                modifier = Modifier.size(350.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quiz Start Button
            OutlinedButton(

                onClick = {
                    if (isWifiConnected) {

                        if (viewModel.questions.isEmpty()) {
                            viewModel.fetchQuestions()
                            Handler(Looper.getMainLooper()).postDelayed({
                                viewModel.restartQuiz {
                                    navController.navigate("quiz") {
                                        popUpTo("quiz") { inclusive = true }
                                    }
                                }
                            }, 3000)
                        } else {
                            viewModel.restartQuiz {
                                navController.navigate("quiz") {
                                    popUpTo("quiz") { inclusive = true }
                                }
                            }
                        }
                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.hint_green),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp), // Rounded corners for smooth UI
                border = BorderStroke(
                    2.dp,
                    colorResource(id = R.color.hint_green)
                ), // Border color
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Set button height
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp)) // Adds a soft shadow
                    .clip(RoundedCornerShape(16.dp)) // Ensures proper clipping
                    .background(colorResource(id = R.color.hint_green)),
                enabled = userEmail != null && isWifiConnected
            ) {
                Text(
                    text = stringResource(R.string.start_quiz),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
                )
            }

            if (!isWifiConnected) {
                Text("⚠ Connect to Wi-Fi to start the quiz!", color = Color.Red)
            }


            Spacer(modifier = Modifier.height(16.dp))
            // Provide a button to go to the leaderboard
            OutlinedButton(
                onClick = {
                    if (isWifiConnected) {
                        navController.navigate("leaderboard")
                    }// Navigate to leaderboard screen
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.hint_green),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp), // Rounded corners for smooth UI
                border = BorderStroke(
                    2.dp,
                    colorResource(id = R.color.hint_green)
                ), // Border color
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Set button height
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp)) // Adds a soft shadow
                    .clip(RoundedCornerShape(16.dp)) // Ensures proper clipping
                    .background(colorResource(id = R.color.hint_green)),
                enabled = userEmail != null && isWifiConnected
            ) {
                Text(
                    text = stringResource(R.string.view_leaderboard),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            // Logout Button
            OutlinedButton(
                onClick = {
                    userPreferences.clearUser()
                    navController.navigate("login") { popUpTo("main") { inclusive = true } }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.hint_green),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp), // Rounded corners for smooth UI
                border = BorderStroke(
                    2.dp,
                    colorResource(id = R.color.hint_green)
                ), // Border color
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Set button height
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp)) // Adds a soft shadow
                    .clip(RoundedCornerShape(16.dp)) // Ensures proper clipping
                    .background(colorResource(id = R.color.hint_green)),
            ) {
                Text(
                    text = stringResource(R.string.logout),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
                )
            }
        }

    }


}

// Function to check Wi-Fi connection
private fun checkWifiAndInternetConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    val hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    // Return true only if connected to WiFi AND network has internet and is validated
    return hasWifi && hasInternet && isValidated
}

