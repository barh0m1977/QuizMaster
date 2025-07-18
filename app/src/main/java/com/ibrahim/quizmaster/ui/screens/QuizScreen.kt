package com.ibrahim.quizmaster.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.data.repository.UserRepository
import com.ibrahim.quizmaster.ui.components.GlowingQuestionCard
import com.ibrahim.quizmaster.viewmodel.QuizViewModel
import com.ibrahim.quizmaster.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: QuizViewModel, navController: NavController) {
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val errorMessage by remember { derivedStateOf { viewModel.errorMessage } }
    val currentIndex by remember { derivedStateOf { viewModel.currentIndex } }
    val questions by remember { derivedStateOf { viewModel.questions } }
    val currentQuestion = questions.getOrNull(currentIndex)
    val numberOfQuestion = currentQuestion?.numberOfQuestion ?: 0
    val userState = UserViewModel()
        userState.user.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.quiz_time)) },
                actions = {
                    IconButton(onClick = {
                        // navigate to profile screen
                        navController.navigate("profile")
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.profile), // Your icon here
                            contentDescription = "Profile",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                errorMessage != null -> {
                    Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                }

                currentQuestion != null -> {
//                    Text(
//                        text = "${stringResource(id = R.string.question_title)} #$numberOfQuestion",
//                        style = MaterialTheme.typography.headlineSmall.copy( // Title-style font
//                            fontWeight = FontWeight.Bold,
//                            color = colorResource(id = R.color.black)
//                        )
//                    )

//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 8.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        elevation = CardDefaults.cardElevation(6.dp),
//                        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white))
//                    ) {
//                        Text(
//                            text = currentQuestion.question,
//                            style = MaterialTheme.typography.headlineMedium.copy(
//                                color = colorResource(id = R.color.black),
//                                fontWeight = FontWeight.SemiBold
//                            ),
//                            modifier = Modifier
//                                .padding(16.dp)
//                        )
//                    }
                    GlowingQuestionCard(question = currentQuestion.question, numberQ =numberOfQuestion )

                    Spacer(modifier = Modifier.height(16.dp))

                    currentQuestion.options.forEach { option ->
                        OutlinedButton(
                            onClick = {
                                viewModel.checkAnswer(option) {
                                    navController.navigate("result/${viewModel.score}")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.white),
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Gray

                            ),
                            shape = RoundedCornerShape(16.dp), // Rounded corners for smooth UI
                            border = BorderStroke(
                                1.dp,
                                colorResource(id = R.color.black)
                            ), // Border color
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp) // Set button height
                                .shadow(
                                    8.dp,
                                    shape = RoundedCornerShape(16.dp)
                                ) // Adds a soft shadow
                                .clip(RoundedCornerShape(16.dp)) // Ensures proper clipping
                                .background(colorResource(id = R.color.white))

                        ) {
                            Text(
                                text = option,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.Question_green)
                                ),
                                modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                else -> {
                    Text(
                        text = "No questions available!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}




