package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.model.Flashcard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSetScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Flashcard Set") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CreateSetScreenContent(
                flashcardDao = flashcardDao,
                onDone = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSetScreenContent(
    flashcardDao: FlashcardDao,
    onDone: () -> Unit
) {
    var setName by remember { mutableStateOf("") }
    val entries = remember { mutableStateListOf(Pair("", "")) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = setName,
            onValueChange = { setName = it },
            label = { Text("Set Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        entries.forEachIndexed { index, (question, answer) ->
            OutlinedTextField(
                value = question,
                onValueChange = { newQ -> entries[index] = newQ to answer },
                label = { Text("Question ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = answer,
                onValueChange = { newA -> entries[index] = question to newA },
                label = { Text("Answer ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        TextButton(
            onClick = { entries += "" to "" },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Add Question")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                if (setName.isNotBlank() && entries.all { it.first.isNotBlank() && it.second.isNotBlank() }) {
                    coroutineScope.launch {
                        entries.forEach { (q, a) ->
                            flashcardDao.insertFlashcard(
                                Flashcard(
                                    setName = setName.trim(),
                                    question = q.trim(),
                                    answer = a.trim(),
                                    userId = userId
                                )
                            )
                        }
                        onDone()
                    }
                }
            },
            enabled = setName.isNotBlank() && entries.all { it.first.isNotBlank() && it.second.isNotBlank() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Flashcards")
        }
    }
}
