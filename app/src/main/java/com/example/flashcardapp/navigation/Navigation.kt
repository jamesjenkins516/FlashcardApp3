package com.example.flashcardapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.CreateSetScreen
import com.example.flashcardapp.screens.HomeScreen
import com.example.flashcardapp.screens.LoginScreen
import com.example.flashcardapp.screens.SetDetailScreen
import com.example.flashcardapp.screens.SignupScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    // obtain Android Context in Compose
    val context = LocalContext.current
    // now invoke with context
    val dao = FlashcardDatabaseInstance.flashcardDao(context)

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignupScreen(navController)
        }
        composable("home") {
            HomeScreen(navController = navController, flashcardDao = dao)
        }
        composable("create") {
            CreateSetScreen(navController = navController, flashcardDao = dao)
        }
        composable(
            route = "setDetail/{setName}",
            arguments = listOf(
                navArgument("setName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val setName = backStackEntry.arguments?.getString("setName")!!
            SetDetailScreen(
                navController = navController,
                flashcardDao = dao,
                setName = setName
            )
        }
    }
}
