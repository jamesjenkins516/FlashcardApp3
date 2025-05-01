package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.navigation.Screen
import com.example.flashcardapp.data.FlashcardDao
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnSelectionScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) {
    //Login with firbase
    val userId = FirebaseAuth.getInstance().currentUser?.uid
        ?: return

    //gets users flashcard sets
    val sets by flashcardDao
        .getAllSetNames(userId)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = { //top bar with back button (homepage) and title
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Sets.route) {
                            popUpTo(Screen.Sets.route) { inclusive = false }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Home")
                    }
                },
                title = { Text("Pick a Set") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column( //list the users sets
            modifier            = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (sets.isEmpty()) {
                Text(
                    "You haven’t created any sets yet.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                sets.forEach { setName ->
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("learn/$setName") },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text     = setName,
                            modifier = Modifier.padding(16.dp),
                            style    = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
