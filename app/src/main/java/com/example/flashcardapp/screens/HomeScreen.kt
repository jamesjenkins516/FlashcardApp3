package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.auth.AuthService
import com.example.flashcardapp.data.FlashcardDao
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) {
    // 1) Ensure we have a logged-in user
    val userId = FirebaseAuth.getInstance().currentUser?.uid
        ?: return

    // 2) Observe only this user’s set names
    val setNames by flashcardDao
        .getAllSetNames(userId)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // <-- Sign-out button on the top left
                navigationIcon = {
                    IconButton(onClick = {
                        AuthService.logout()
                        navController.navigate("login") {
                            // Clear entire back stack
                            popUpTo(navController.graph.startDestinationId) {
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
                title = { Text("StudySets") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            contentPadding      = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top spacing
            item { Spacer(Modifier.height(16.dp)) }

            // List each set
            items(setNames) { setName ->
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("setDetail/$setName") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape     = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text     = setName,
                            style    = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector   = Icons.Default.ChevronRight,
                            contentDescription = "Go to $setName"
                        )
                    }
                }
            }
        }
    }
}
