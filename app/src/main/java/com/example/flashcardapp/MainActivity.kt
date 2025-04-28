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
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.CreateSetScreen
import com.example.flashcardapp.screens.HomeScreen
import com.example.flashcardapp.screens.LearnSelectionScreen
import com.example.flashcardapp.screens.LearnQuizScreen
import com.example.flashcardapp.screens.SetDetailScreen

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
    val context = LocalContext.current
    val flashcardDao: FlashcardDao = FlashcardDatabaseInstance.flashcardDao(context)

    // Tabs that appear in the bottom navigation bar
    val tabs = listOf(Screen.Sets, Screen.CreateSet, Screen.Learn)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route

                tabs.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Sets      -> Icons.Filled.List
                                    Screen.CreateSet -> Icons.Filled.Add
                                    Screen.Learn     -> Icons.Filled.School
                                    Screen.LearnQuiz -> Icons.Filled.School    // not shown in bar
                                    Screen.SetDetail -> Icons.Filled.List      // not shown in bar
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Sets.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home screen: list of sets
            composable(Screen.Sets.route) {
                HomeScreen(navController, flashcardDao)
            }
            // Create a new set
            composable(Screen.CreateSet.route) {
                CreateSetScreen(navController, flashcardDao)
            }
            // Learn: first pick a set
            composable(Screen.Learn.route) {
                LearnSelectionScreen(navController, flashcardDao)
            }
            // LearnQuiz: quiz on the selected set
            composable(
                route = Screen.LearnQuiz.route,
                arguments = listOf(navArgument("setName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val setName = backStackEntry.arguments?.getString("setName") ?: ""
                LearnQuizScreen(navController, flashcardDao, setName)
            }
            // Detail view of a set's flashcards
            composable(
                route = Screen.SetDetail.route,
                arguments = listOf(navArgument("setName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val setName = backStackEntry.arguments?.getString("setName") ?: ""
                SetDetailScreen(navController, flashcardDao, setName)
            }
        }
    }
}

sealed class Screen(val route: String, val label: String) {
    object Sets      : Screen("sets",        "Sets")
    object CreateSet : Screen("create_set",  "Create")
    object Learn     : Screen("learn",       "Learn")       // tab: pick a set
    object LearnQuiz : Screen("learn/{setName}", "Quiz")     // hidden: actual quiz
    object SetDetail : Screen("setDetail/{setName}", "Detail")
}
