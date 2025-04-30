

package com.example.flashcardapp.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.*
import com.example.flashcardapp.ui.theme.FlashcardAppTheme

@Composable
fun Navigation() {
    // ❶ Theme toggle state
    var darkThemeEnabled by rememberSaveable { mutableStateOf(false) }

    val navController = rememberNavController()
    val context       = LocalContext.current
    val dao           = FlashcardDatabaseInstance.flashcardDao(context)

    // ❷ Wrap everything in your theme
    FlashcardAppTheme(darkTheme = darkThemeEnabled) {
        NavHost(
            navController    = navController,
            startDestination = "login"
        ) {
            // Auth flow
            composable("login") {
                LoginScreen(navController)
            }
            composable("signup") {
                SignupScreen(navController)
            }

            // Main screens
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

            // Detail view
            composable(
                route = "setDetail/{setName}",
                arguments = listOf(navArgument("setName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val setName = backStackEntry.arguments!!.getString("setName")!!
                SetDetailScreen(
                    navController = navController,
                    flashcardDao  = dao,
                    setName       = setName
                )
            }

            // Edit screen
            composable(
                route = "edit/{setName}",
                arguments = listOf(navArgument("setName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val setName = backStackEntry.arguments!!.getString("setName")!!
                EditSetScreen(
                    navController      = navController,
                    flashcardDao       = dao,
                    originalSetName    = setName
                )
            }

            // Settings screen
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
