package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.Screen
import com.example.flashcardapp.data.FlashcardDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnSelectionScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) {
    // Observe all set names
    val sets by flashcardDao
        .getAllSetNames()
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pick a Set") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
