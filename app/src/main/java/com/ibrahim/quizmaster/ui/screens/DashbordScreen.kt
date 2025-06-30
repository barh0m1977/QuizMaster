package com.ibrahim.quizmaster.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.data.UserPreferences
import com.ibrahim.quizmaster.data.model.UserScore
import com.ibrahim.quizmaster.data.repository.ScoreRepository
import com.ibrahim.quizmaster.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBord(navController: NavController, scoreRepository: ScoreRepository,viewModel: QuizViewModel) {

    var leaderboard by remember { mutableStateOf<List<UserScore>>(emptyList()) }
    val lastQuestion by viewModel.lastQuestion.collectAsState()
    val isLoading = viewModel.isLoading



    // Fetch top scores when screen is first launched
    LaunchedEffect(true) {
        viewModel.getLastQuestionFromFirestore()
        scoreRepository.getKingQuiz { scores ->
            leaderboard = scores
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
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
                },

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
            GlowingKingCard(navController = navController, leaderboard = leaderboard)
            GlowingLastQuestionCard(navController = navController, question = lastQuestion, isLoading = isLoading)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MainButton(navController = navController, modifier = Modifier.weight(1f))
                SubmitQuizButton(navController = navController, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LogOutButton(navController = navController, modifier = Modifier.weight(1f))
                UploadFileButton(navController = navController, modifier = Modifier.weight(1f),viewModel)
            }
            SearchButton(navController = navController)


        }
    }
}

@Composable
fun GlowingKingCard(
    navController: NavController,
    leaderboard: List<UserScore>
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the border and glow color
    val animatedColor by infiniteTransition.animateColor(
        initialValue = colorResource(id = R.color.hint_green), // Lighter green
        targetValue = colorResource(id = R.color.Question_green), // Deeper green
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animate glowing shadow radius
    val animatedGlow: Dp by infiniteTransition.animateValue(
        initialValue = 8.dp,
        targetValue = 24.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = animatedGlow,
                shape = RoundedCornerShape(20.dp),
                ambientColor = animatedColor,
                spotColor = animatedColor
            )
            .border(
                width = 3.dp,
                color = animatedColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { navController.navigate("leaderboard") }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👑 " + stringResource(R.string.kingOfQuiz),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = animatedColor,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (leaderboard.isEmpty()) {
                CircularProgressIndicator(color = animatedColor)
            } else {
                LazyColumn {
                    items(leaderboard) { userScore ->
                        LeaderboardItem(userScore)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GlowingLastQuestionCard(
    navController: NavController,
    question: String?,
    isLoading: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the border and glow color
    val animatedColor by infiniteTransition.animateColor(
        initialValue = colorResource(id = R.color.hint_green), // Lighter green
        targetValue = colorResource(id = R.color.Question_green), // Deeper green
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animate glowing shadow radius
    val animatedGlow: Dp by infiniteTransition.animateValue(
        initialValue = 8.dp,
        targetValue = 24.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = animatedGlow,
                shape = RoundedCornerShape(20.dp),
                ambientColor = animatedColor,
                spotColor = animatedColor
            )
            .border(
                width = 3.dp,
                color = animatedColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\uD83E\uDDFE " + stringResource(R.string.Last_Question),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = animatedColor,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(color = animatedColor)
            }else{
                Text(
                    text = question ?: "No recent question found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@Composable
fun MainButton(navController: NavController, modifier: Modifier = Modifier) {
    Card(
        onClick = { navController.navigate("main") },
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.hint_green))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\uD83E\uDDFE ${stringResource(R.string.quiz_time)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SubmitQuizButton(navController: NavController, modifier: Modifier = Modifier) {
    Card(
        onClick = { navController.navigate("submit_question") },
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.hint_green))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "➕ ${stringResource(R.string.addQ)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LogOutButton(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    Card(
        onClick = {
            userPreferences.clearUser()
            navController.navigate("login") { popUpTo("dashBord") { inclusive = true } }
        },
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.hint_green))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\uD83D\uDD10 ${stringResource(R.string.logout)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UploadFileButton(navController: NavController, modifier: Modifier = Modifier,viewModel: QuizViewModel) {
    val context = LocalContext.current

    // Launcher to open the document picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                Toast.makeText(context, "Selected: ${uri.path}", Toast.LENGTH_SHORT).show()
                viewModel.uploadFile(context,uri)
            }
        }
    )

    Card(
        onClick = {
            // Launch the document picker to select an .xlsx file
            launcher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

        },
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.hint_green))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\uD83D\uDCE4 ${stringResource(R.string.Upload)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun SearchButton(navController: NavController, modifier: Modifier = Modifier) {

    Card(
        onClick = {
            navController.navigate("search")
        },
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.hint_green))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = " ${stringResource(R.string.search)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


