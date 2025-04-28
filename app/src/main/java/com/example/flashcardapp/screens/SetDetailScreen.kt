package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.Screen
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.model.Flashcard
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    setName: String
) {
    // 1) load cards
    val cards by flashcardDao
        .getFlashcardsForSet(setName)
        .collectAsState(initial = emptyList())

    // 2) delete‐confirm dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // 3) make the list scrollable
    val listState: LazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(setName) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top    = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start  = 16.dp,
                end    = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Back arrow
            item {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            // If empty, just show the message
            if (cards.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxHeight(0.7f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No cards in this set yet.")
                    }
                }
            } else {
                // Your flashcard cards
                item {
                    var currentIndex by remember { mutableStateOf(0) }
                    var showAnswer  by remember { mutableStateOf(false) }
                    val card = cards[currentIndex]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { showAnswer = !showAnswer },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (showAnswer) card.answer else card.question,
                                modifier = Modifier.padding(16.dp),
                                style    = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Prev/Next row
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
                        ) { Text("Previous") }

                        Text("${currentIndex + 1} of ${cards.size}")

                        Button(
                            onClick = {
                                if (currentIndex < cards.size - 1) {
                                    currentIndex++
                                    showAnswer = false
                                }
                            },
                            enabled = currentIndex < cards.size - 1
                        ) { Text("Next") }
                    }
                }
            }

            // Finally, the delete button at bottom of list
            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor   = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Set")
                }
            }
        }

        // Confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title   = { Text("Delete \"$setName\"?") },
                text    = { Text("This will remove all cards in this set. Are you sure?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        coroutineScope.launch {
                            flashcardDao.deleteFlashcardsForSet(setName)
                            navController.popBackStack(Screen.Sets.route, false)
                        }
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
