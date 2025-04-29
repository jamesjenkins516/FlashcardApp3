// app/src/main/java/com/example/flashcardapp/screens/HomeScreen.kt

package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.flashcardapp.data.FlashcardDao
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    onSettingsClick: () -> Unit
) {
    // 1️⃣ Get the current user ID (or bail out)
    val userId = FirebaseAuth.getInstance().currentUser?.uid
        ?: return

    // 2️⃣ Load the list of set names from the DAO
    val sets by flashcardDao
        .getAllSetNames(userId)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    // ◀️ Sign Out button
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out"
                        )
                    }
                },
                title = { Text("Your Flashcard Sets") },
                actions = {
                    // ⚙️ Settings button on the right
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        // no FAB here
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top    = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(sets) { setName ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("setDetail/$setName")
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text  = setName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
