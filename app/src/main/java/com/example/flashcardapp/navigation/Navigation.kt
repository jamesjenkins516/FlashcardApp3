package com.example.flashcardapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.*

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val dao = FlashcardDatabaseInstance.flashcardDao

    NavHost(navController = navController, startDestination = "login") {
        composable("login")  { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home")   { HomeScreen(navController, dao) }

        // New detail route – shows all cards in a single set
        composable(
            "setDetail/{setName}",
            arguments = listOf(
                navArgument("setName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val setName = backStackEntry.arguments!!.getString("setName")!!
            SetDetailScreen(navController, dao, setName)
        }

        // (Optional) remove your standalone create-route if you never navigate here directly
        // composable("create") { CreateSetScreen(navController, dao) }
    }
}
