package com.example.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardApp() {
    val navController = rememberNavController()
    val context       = LocalContext.current
    val flashcardDao  = FlashcardDatabaseInstance.flashcardDao(context)

    // Determine whether to show bottom bar
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute    = backStackEntry?.destination?.route
    val showBottomBar   = currentRoute != "login" && currentRoute != "signup"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    val tabs = listOf(Screen.Sets, Screen.CreateSet, Screen.Learn)
                    tabs.forEach { screen ->
                        NavigationBarItem(
                            selected    = currentRoute == screen.route,
                            onClick     = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = when (screen) {
                                        Screen.Sets      -> Icons.Filled.List
                                        Screen.CreateSet -> Icons.Filled.Add
                                        Screen.Learn     -> Icons.Filled.School
                                        else             -> Icons.Filled.List
                                    },
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController   = navController,
            startDestination = "login",
            modifier         = Modifier.padding(innerPadding)
        ) {
            // **Auth flow**
            composable("login")  { LoginScreen(navController) }
            composable("signup") { SignupScreen(navController) }

            // **Main tabs**
            composable(Screen.Sets.route) {
                HomeScreen(navController, flashcardDao)
            }
            composable(Screen.CreateSet.route) {
                CreateSetScreen(navController, flashcardDao)
            }
            composable(Screen.Learn.route) {
                LearnSelectionScreen(navController, flashcardDao)
            }

            // **Hidden/detail routes**
            composable(
                route = Screen.LearnQuiz.route,
                arguments = listOf(navArgument("setName") {
                    type = NavType.StringType
                })
            ) { backStack ->
                val setName = backStack.arguments?.getString("setName") ?: ""
                LearnQuizScreen(navController, flashcardDao, setName)
            }
            composable(
                route = Screen.SetDetail.route,
                arguments = listOf(navArgument("setName") {
                    type = NavType.StringType
                })
            ) { backStack ->
                val setName = backStack.arguments?.getString("setName") ?: ""
                SetDetailScreen(navController, flashcardDao, setName)
            }
        }
    }
}

// Reuse your existing sealed class for routes:
sealed class Screen(val route: String, val label: String) {
    object Sets      : Screen("sets",       "Sets")
    object CreateSet : Screen("create_set", "Create")
    object Learn     : Screen("learn",      "Learn")
    object LearnQuiz : Screen("learn/{setName}",   "Quiz")
    object SetDetail : Screen("setDetail/{setName}", "Detail")
}
