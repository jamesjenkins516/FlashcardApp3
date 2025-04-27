package com.example.flashcardapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.CreateSetScreen
import com.example.flashcardapp.screens.HomeScreen
import com.example.flashcardapp.screens.LoginScreen
import com.example.flashcardapp.screens.SignupScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val dao = FlashcardDatabaseInstance.flashcardDao

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController, dao) }
        composable("create") { CreateSetScreen(navController, dao) }
    }
}
