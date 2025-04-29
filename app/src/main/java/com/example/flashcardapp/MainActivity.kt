package com.example.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.navigation.Screen
import com.example.flashcardapp.screens.*
import com.example.flashcardapp.ui.theme.FlashcardAppTheme

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
    // theme toggle
    var darkThemeEnabled by rememberSaveable { mutableStateOf(false) }

    FlashcardAppTheme(darkTheme = darkThemeEnabled) {
        val navController = rememberNavController()
        val context       = LocalContext.current
        val dao           = FlashcardDatabaseInstance.flashcardDao(context)

        // bottom-bar visibility
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute    = backStackEntry?.destination?.route
        val showBottomBar   = currentRoute != Screen.Login.route
                && currentRoute != Screen.Signup.route
                && currentRoute != Screen.Settings.route

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
                                selected       = currentRoute == screen.route,
                                onClick        = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState    = true
                                    }
                                },
                                icon           = {
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
                                label          = { Text(screen.label) },
                                alwaysShowLabel = false
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController    = navController,
                startDestination = Screen.Login.route,
                modifier         = Modifier.padding(innerPadding)
            ) {
                // auth
                composable(Screen.Login.route)  { LoginScreen(navController) }
                composable(Screen.Signup.route) { SignupScreen(navController) }

                // main tabs
                composable(Screen.Sets.route) {
                    HomeScreen(
                        navController    = navController,
                        flashcardDao     = dao,
                        onSettingsClick  = { navController.navigate(Screen.Settings.route) }
                    )
                }
                composable(Screen.CreateSet.route) {
                    CreateSetScreen(navController, dao)
                }
                composable(Screen.Learn.route) {
                    LearnSelectionScreen(navController, dao)
                }

                // detail & quiz
                composable(
                    route     = Screen.LearnQuiz.route,
                    arguments = listOf(navArgument("setName") { type = NavType.StringType })
                ) { back ->
                    val setName = back.arguments!!.getString("setName")!!
                    LearnQuizScreen(navController, dao, setName)
                }
                composable(
                    route     = Screen.SetDetail.route,
                    arguments = listOf(navArgument("setName") { type = NavType.StringType })
                ) { back ->
                    val setName = back.arguments!!.getString("setName")!!
                    SetDetailScreen(navController, dao, setName)
                }

                // ⚙️ edit set
                composable(
                    route     = Screen.EditSet.route,
                    arguments = listOf(navArgument("setName") { type = NavType.StringType })
                ) { back ->
                    val setName = back.arguments!!.getString("setName")!!
                    EditSetScreen(navController, dao, setName)
                }

                // settings
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        navController      = navController,
                        darkThemeEnabled   = darkThemeEnabled,
                        onToggleDarkTheme  = { darkThemeEnabled = it }
                    )
                }
            }
        }
    }
}
