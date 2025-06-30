package com.ibrahim.quizmaster.ui.screens


import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.data.model.UserScore
import com.ibrahim.quizmaster.viewmodel.QuizViewModel

@Composable
fun ResultScreen(
    score: Int,
    navController: NavController,
    viewModel: QuizViewModel
){
    // Save the score to Firestore when the screen is first displayed
    LaunchedEffect(true) {
        // Create a UserScore object
        val userScore = UserScore(

            score = score
        )
        // Save the user's score to Firestore
//        scoreRepository.saveUserScore(userScore)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the score
        Text(
            text = "Your score: $score",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Provide a button to go to the leaderboard
        OutlinedButton(
            onClick = {
                navController.navigate("leaderboard")  // Navigate to leaderboard screen
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
                text = stringResource(R.string.view_leaderboard),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
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
                .background(colorResource(id = R.color.hint_green))
        ) {
            Text(
                text = stringResource(R.string.main_screen),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Provide a button to retry the quiz
        OutlinedButton(
            onClick = {
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
                text = stringResource(R.string.retry_quiz),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
            )
        }



    }

}

