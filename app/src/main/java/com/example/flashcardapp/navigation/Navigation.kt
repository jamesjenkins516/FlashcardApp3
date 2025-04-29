package com.example.flashcardapp.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.CreateSetScreen
import com.example.flashcardapp.screens.HomeScreen
import com.example.flashcardapp.screens.LoginScreen
import com.example.flashcardapp.screens.SetDetailScreen
import com.example.flashcardapp.screens.SettingsScreen
import com.example.flashcardapp.screens.SignupScreen
import com.example.flashcardapp.ui.theme.FlashcardAppTheme

@Composable
fun Navigation() {
    // ❶ Hold the dark/light flag in remembered state
    var darkThemeEnabled by rememberSaveable { mutableStateOf(false) }

    val navController = rememberNavController()
    val context       = LocalContext.current
    val dao           = FlashcardDatabaseInstance.flashcardDao(context)

    // ❷ Wrap all routes in your theme
    FlashcardAppTheme(darkTheme = darkThemeEnabled) {
        NavHost(
            navController    = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(navController)
            }
            composable("signup") {
                SignupScreen(navController)
            }
            composable("home") {
                HomeScreen(
                    navController    = navController,
                    flashcardDao     = dao,
                    onSettingsClick  = { navController.navigate("settings") }
                )
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
                    flashcardDao  = dao,
                    setName       = setName
                )
            }
            // ➕ New Settings route
            composable("settings") {
                SettingsScreen(
                    navController      = navController,
                    darkThemeEnabled   = darkThemeEnabled,
                    onToggleDarkTheme  = { darkThemeEnabled = it }
                )
            }
        }
    }
}
