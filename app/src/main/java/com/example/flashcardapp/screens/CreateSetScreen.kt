package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.model.Flashcard
import com.example.flashcardapp.auth.AuthService
import kotlinx.coroutines.launch

@Composable
fun CreateSetScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var setName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Flashcard") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = setName,
                onValueChange = { setName = it },
                label = { Text("Set Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text("Answer") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val userId = AuthService.currentUserId
                    if (userId != null && question.isNotBlank() && answer.isNotBlank() && setName.isNotBlank()) {
                        val flashcard = Flashcard(
                            question = question,
                            answer = answer,
                            setName = setName,
                            userId = userId
                        )
                        coroutineScope.launch {
                            flashcardDao.insertFlashcard(flashcard)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Flashcard")
            }
        }
    }
}
