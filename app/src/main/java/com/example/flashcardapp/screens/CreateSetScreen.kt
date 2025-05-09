package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.flashcardapp.navigation.Screen
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.model.Flashcard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch




class CardUiState(question: String = "", answer: String = "") {
    var question by mutableStateOf(question)
    var answer   by mutableStateOf(answer)
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSetScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) { //gets the user ID from firebase
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val scope  = rememberCoroutineScope()

    //Set name and list of cards
    var setName by rememberSaveable { mutableStateOf("") }
    val cards   = remember { mutableStateListOf(CardUiState()) }


    //This is the top bar on the page that says create new set and has backbutton to homepage
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Create New Set") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding -> //styling for the flashcard creation text boxes
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Where you input the name of the set
            OutlinedTextField(
                value = setName,
                onValueChange = { setName = it },
                label = { Text("Set Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            //For each card it shows a Question and Answer text box as well as a remove button
            cards.forEachIndexed { index, card ->
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = card.question,
                            onValueChange = { card.question = it },
                            label = { Text("Question ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = card.answer,
                            onValueChange = { card.answer = it },
                            label = { Text("Answer ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextButton(
                            onClick = { cards.removeAt(index) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                            Spacer(Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }
                }
            }

            //Add question button below the last flashcard (allows you to make a new flashcard)
            TextButton(onClick = { cards.add(CardUiState()) }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add Question")
            }

            Spacer(Modifier.weight(1f))

            //Saves the flashcard set and moves you to setdetail screen
            Button(
                onClick = {
                    val name = setName.trim()
                    if (name.isNotEmpty()) {
                        scope.launch {
                            cards.forEach { c ->
                                if (c.question.isNotBlank() || c.answer.isNotBlank()) {
                                    flashcardDao.insertFlashcard(
                                        Flashcard(
                                            question = c.question.trim(),
                                            answer   = c.answer.trim(),
                                            setName  = name,
                                            userId   = userId
                                        )
                                    )
                                }
                            }
                        }
                        navController.navigate(
                            Screen.SetDetail.route.replace("{setName}", name)
                        ) {
                            popUpTo(Screen.CreateSet.route) { inclusive = true }
                        }
                    }
                },
                enabled = setName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save Flashcards")
            }
        }
    }
}
