

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
    //Allows you to change app to darkmode
    var darkThemeEnabled by rememberSaveable { mutableStateOf(false) }

    val navController = rememberNavController()
    val context       = LocalContext.current
    val dao           = FlashcardDatabaseInstance.flashcardDao(context)




    FlashcardAppTheme(darkTheme = darkThemeEnabled) {
        NavHost(
            navController    = navController,
            startDestination = "login"  //login screen when the app first opens
        ) {
            //Login/Singup
            composable("login") {
                LoginScreen(navController)
            }
            composable("signup") {
                SignupScreen(navController)
            }

            //HomeScreen which shows all sets as well as settings button
            composable("home") {
                HomeScreen(
                    navController    = navController,
                    flashcardDao     = dao,
                    onSettingsClick  = { navController.navigate("settings") }
                )
            }
            //CreateSetScreen lets you make new flashcard sets
            composable("create") {
                CreateSetScreen(navController = navController, flashcardDao = dao)
            }

            //SetDetailScreen lets you view flashcards, delete them, go to edit set page, and add to test to calendar
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

            //Edit Screen where you can modify flashcards or delete them
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

            //Switch between dark and light mode
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
