// app/src/main/java/com/example/flashcardapp/screens/SetDetailScreen.kt

package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
            // Empty-state message
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
            // Flashcard learning UI
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Track which card we’re on, and whether its answer is shown
                var currentIndex by remember { mutableStateOf(0) }
                var showAnswer  by remember { mutableStateOf(false) }

                val card = cards[currentIndex]

                // 2. The card itself—tap to toggle question/answer
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { showAnswer = !showAnswer },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text  = if (showAnswer) card.answer else card.question,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Navigation row: Previous • index/total • Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                showAnswer = false
                            }
                        },
                        enabled = currentIndex > 0
                    ) {
                        Text("Previous")
                    }
                    Text("${currentIndex + 1} of ${cards.size}")
                    Button(
                        onClick = {
                            if (currentIndex < cards.size - 1) {
                                currentIndex++
                                showAnswer = false
                            }
                        },
                        enabled = currentIndex < cards.size - 1
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}
