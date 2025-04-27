// app/src/main/java/com/example/flashcardapp/screens/SetDetailScreen.kt
package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.model.Flashcard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    setName: String
) {
    // Collect the list of Flashcards for this set from the DAO
    val cards by flashcardDao
        .getFlashcardsForSet(setName)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(setName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (cards.isEmpty()) {
            // Empty‐state message
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Text(
                    text = "No cards in this set yet.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            // List all cards in a LazyColumn
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(cards) { flashcard ->
                    FlashcardRow(flashcard)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun FlashcardRow(flashcard: Flashcard) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = flashcard.question,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = flashcard.answer,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
