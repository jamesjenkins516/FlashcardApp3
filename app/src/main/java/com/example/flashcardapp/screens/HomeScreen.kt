package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.auth.AuthService

@Composable
fun HomeScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) {
    var setNames by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        val userId = AuthService.currentUserId
        if (userId != null) {
            setNames = flashcardDao.getAllSetNames(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Study Sets") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (setNames.isEmpty()) {
                Text("No study sets yet. Create one!")
            } else {
                LazyColumn {
                    items(setNames) { setName ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = setName)
                            }
                        }
                    }
                }
            }
        }
    }
}
