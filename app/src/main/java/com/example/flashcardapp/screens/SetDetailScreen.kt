package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    setName: String
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
        ?: return

    val cards by flashcardDao
        .getFlashcardsForSet(setName, userId)
        .collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
    ) { padding ->
        LazyColumn(
            contentPadding      = PaddingValues(
                top    = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
                start  = 16.dp,
                end    = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.fillMaxSize()
        ) {
            item {
                if (cards.isEmpty()) {
                    Box(
                        Modifier
                            .fillParentMaxHeight(0.7f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No cards in this set yet.")
                    }
                } else {
                    // Your existing flip‐card + Prev/Next UI
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors  = ButtonDefaults.buttonColors(
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

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title            = { Text("Delete \"$setName\"?") },
                text             = { Text("This will remove all cards in this set.") },
                confirmButton    = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            flashcardDao.deleteFlashcardsForSet(setName, userId)
                            navController.popBackStack(Screen.Sets.route, false)
                        }
                    }) { Text("Delete") }
                },
                dismissButton    = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
