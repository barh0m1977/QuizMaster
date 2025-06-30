package com.ibrahim.quizmaster.ui.screens


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ibrahim.quizmaster.viewmodel.QuizViewModel
import androidx.navigation.NavController
import com.ibrahim.quizmaster.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun SubmitQuestionScreen(viewModel: QuizViewModel, navController: NavController) {
    val context = LocalContext.current // get context
    // State to hold question input fields
    var question by remember { mutableStateOf(TextFieldValue("")) }
    var optionA by remember { mutableStateOf(TextFieldValue("")) }
    var optionB by remember { mutableStateOf(TextFieldValue("")) }
    var optionC by remember { mutableStateOf(TextFieldValue("")) }
    var correctAnswer by remember { mutableStateOf(TextFieldValue("")) }
    var numberOfQuestion by remember { mutableStateOf(TextFieldValue("")) }

    // State for loading indicator or any messages
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Submit a Question", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Question TextField
        TextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Enter Question") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Option A TextField
        TextField(
            value = optionA,
            onValueChange = { optionA = it },
            label = { Text("Option A") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Option B TextField
        TextField(
            value = optionB,
            onValueChange = { optionB = it },
            label = { Text("Option B") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Option C TextField
        TextField(
            value = optionC,
            onValueChange = { optionC = it },
            label = { Text("Option C") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))


        // Correct Answer TextField
        TextField(
            value = correctAnswer,
            onValueChange = { correctAnswer = it },
            label = { Text("Correct Answer") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = numberOfQuestion,
            onValueChange = { numberOfQuestion = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Number Of Question") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        OutlinedButton(
            onClick = {
                if (question.text.isNotEmpty() && optionA.text.isNotEmpty() && optionB.text.isNotEmpty() && optionC.text.isNotEmpty() && correctAnswer.text.isNotEmpty() && numberOfQuestion.text.isNotEmpty()) {
                    // Submit the question to Firestore
                    val optionsList = listOf(optionA.text, optionB.text, optionC.text)
                    viewModel.submitQuestion(
                        context,
                        question.text,
                        optionsList,
                        correctAnswer.text,
                        numberOfQuestion.text.toInt()
                    )
                    successMessage = "Question Submitted Successfully!"
                    question = TextFieldValue("")
                    optionA = TextFieldValue("")
                    optionB = TextFieldValue("")
                    optionC = TextFieldValue("")
                    correctAnswer = TextFieldValue("")
                    numberOfQuestion = TextFieldValue("")
                } else {
                    successMessage = "Please fill all fields"
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
        ) {
            Text(
                text = "Submit Question",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
            )
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Display success or error message
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = if (successMessage == "Question Submitted Successfully!") androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}
