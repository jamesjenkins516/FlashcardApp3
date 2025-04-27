// app/src/main/java/com/example/flashcardapp/navigation/Navigation.kt
package com.example.flashcardapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
    // grab a Context and then your DAO
    val context: Context = LocalContext.current
    val dao = FlashcardDatabaseInstance.flashcardDao(context)

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController, dao) }
        composable("create") { CreateSetScreen(navController, dao) }
    }
}
