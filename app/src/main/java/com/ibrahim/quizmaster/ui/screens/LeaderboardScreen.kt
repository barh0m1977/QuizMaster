package com.ibrahim.quizmaster.ui.screens


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ibrahim.quizmaster.data.repository.ScoreRepository

@Composable
fun LeaderboardScreen(scoreRepository: ScoreRepository, navController: NavController) {
    var leaderboard by remember { mutableStateOf<List<UserScore>>(emptyList()) }

    LaunchedEffect(true) {
        scoreRepository.getTopScores { scores ->
            leaderboard = scores
        }
    }
    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.kingOfQuiz),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (leaderboard.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(leaderboard.take(10).withIndex().toList()) { (index, userScore) ->
                        LeaderboardItem(index, userScore)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate("quiz") {
                        popUpTo("leaderboard") {
                            inclusive = true
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
                    text = stringResource(R.string.back_to_quiz),
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

}

@Composable
fun LeaderboardItem(rank: Int, userScore: UserScore) {
    val backgroundColor = when (rank) {
        0 -> Color(0xFFFFD700) // Gold
        1 -> Color(0xFFC0C0C0) // Silver
        2 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${rank + 1}. ${userScore.userName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (rank < 3) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "${userScore.score}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun LeaderboardItem(userScore: UserScore) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = userScore.userName, style = MaterialTheme.typography.bodyMedium)
        Text(text = userScore.score.toString(), style = MaterialTheme.typography.bodyMedium)
    }
}
