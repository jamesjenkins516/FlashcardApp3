package com.example.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.screens.HomeScreen
import com.example.flashcardapp.screens.CreateSetScreen
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

    val tabs = listOf(Screen.Sets, Screen.CreateSet)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                val backStack by navController.currentBackStackEntryAsState()
                val currentDest = backStack?.destination

                tabs.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDest?.route == screen.route,
                        onClick = {
                            if (screen == Screen.Sets) {
                                navController.navigate(Screen.Sets.route) {
                                    popUpTo(Screen.Sets.route) { inclusive = false }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Sets -> Icons.Filled.List
                                    Screen.CreateSet -> Icons.Filled.Add
                                    Screen.SetDetail -> Icons.Filled.List
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Sets.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Sets.route) {
                HomeScreen(navController = navController, flashcardDao = flashcardDao)
            }
            composable(Screen.CreateSet.route) {
                CreateSetScreen(navController = navController, flashcardDao = flashcardDao)
            }
            composable(
                route = Screen.SetDetail.route,
                arguments = listOf(navArgument("setName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val setName = backStackEntry.arguments?.getString("setName") ?: ""
                SetDetailScreen(
                    navController = navController,
                    flashcardDao = flashcardDao,
                    setName = setName
                )
            }
        }
    }
}

sealed class Screen(val route: String, val label: String) {
    object Sets : Screen("sets", "Sets")
    object CreateSet : Screen("create_set", "Create")
    object SetDetail : Screen("setDetail/{setName}", "Detail")
}

private fun NavDestination?.isTopLevelOf(screen: Screen): Boolean =
    this?.route == screen.route
