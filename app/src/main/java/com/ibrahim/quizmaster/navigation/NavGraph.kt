package com.ibrahim.quizmaster.navigation

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ibrahim.quizmaster.MainActivity
import com.ibrahim.quizmaster.data.UserPreferences
import com.ibrahim.quizmaster.ui.screens.*
import com.ibrahim.quizmaster.viewmodel.QuizViewModel
import com.ibrahim.quizmaster.viewmodel.AuthViewModel
import com.ibrahim.quizmaster.data.repository.ScoreRepository
import com.ibrahim.quizmaster.viewmodel.UserViewModel

@Composable
fun AppNavGraph(viewModel: QuizViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()  // Initialize the NavController
    val scoreRepository = ScoreRepository()
    val userViewModel = UserViewModel()
    val context = LocalContext.current
    val sharedPreferences = UserPreferences(context = context)


    // Define the NavHost and pass the navController to each screen
    NavHost(navController = navController, startDestination = "splash") {

        composable("quiz") {
            QuizScreen(viewModel = viewModel, navController = navController)
        }
        composable("result/{score}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
            ResultScreen(score = score, navController, viewModel)
        }
        composable("leaderboard") {
            LeaderboardScreen(scoreRepository = scoreRepository, navController = navController)
        }
        composable("dashBord") {
            DashBord(navController = navController, scoreRepository = scoreRepository,viewModel=viewModel)
        }
        composable("search") { SearchScreen(viewModel, navController) }
        composable("login") {

            var userEmail = sharedPreferences.getUserEmail()
            if (userEmail != null) {

                navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            } else {

                LoginScreen(navController, authViewModel)
            }


        }
        composable("splash") {
            SplashScreen(navController = navController, sharedPreferences = sharedPreferences)
        }

        composable("signup") {
            SignUpScreen(navController, authViewModel)
        }
        composable("main") {
            MainScreen(navController)
        }
        composable("submit_question") { SubmitQuestionScreen(viewModel, navController) }
        composable("profile") {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }

    }
}
