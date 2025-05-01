package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.navigation.Screen
import com.example.flashcardapp.data.FlashcardDao
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnQuizScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    setName: String
) {
    //Login in with firebase
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    //Load and shuffles a users flashcards (user specific)
    val allCards by flashcardDao
        .getFlashcardsForSet(setName, userId)
        .collectAsState(initial = emptyList())
    val questions = remember(allCards) { allCards.shuffled() }

    //Quiz State
    var currentIndex    by remember { mutableStateOf(0) }
    var correctCount    by remember { mutableStateOf(0) }
    var answerSubmitted by remember { mutableStateOf(false) }
    var selectedAnswer  by remember { mutableStateOf<String?>(null) }
    var showScoreDialog by remember { mutableStateOf(false) }

    val card = questions.getOrNull(currentIndex)

    //Allows for scrolling
    val scrollState = rememberScrollState()

    Scaffold( //title of set you are studying and back button
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack(Screen.Learn.route, false)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Quiz: $setName") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (card == null) {
            // no cards in this set
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No cards to quiz.", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        Column( //This tells you what question you are on
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text  = "Q${currentIndex + 1}/${questions.size}: ${card.question}",
                style = MaterialTheme.typography.titleMedium
            )

            //This adds the correct answer and 3 wrong ones and shuffles the order
            val options = remember(allCards, currentIndex) {
                val wrongs = allCards
                    .filter   { it.question != card.question }
                    .shuffled()
                    .take(3)
                    .map      { it.answer }
                (wrongs + card.answer).shuffled()
            }

            options.forEach { answer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !answerSubmitted) {
                            selectedAnswer = answer
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            answerSubmitted && answer == card.answer   ->
                                MaterialTheme.colorScheme.primaryContainer
                            answerSubmitted && answer == selectedAnswer ->
                                MaterialTheme.colorScheme.errorContainer
                            selectedAnswer == answer                    ->
                                MaterialTheme.colorScheme.secondaryContainer
                            else                                         ->
                                MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Text(
                        text     = answer,
                        modifier = Modifier.padding(16.dp),
                        style    = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            val isLast = currentIndex == questions.lastIndex
            Row( //sumbit and next buttons
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (!answerSubmitted) {
                            if (selectedAnswer == card.answer) correctCount++
                            if (isLast) showScoreDialog = true
                            else        answerSubmitted = true
                        } else {
                            currentIndex++
                            answerSubmitted = false
                            selectedAnswer = null
                        }
                    },
                    enabled = selectedAnswer != null
                ) {
                    Text(
                        when {
                            !answerSubmitted && isLast -> "Finish"
                            !answerSubmitted           -> "Submit"
                            else                        -> "Next"
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        if (showScoreDialog) { //shows you how many you got right out of the toal questions
            AlertDialog(
                onDismissRequest = { /* no-op */ },
                title            = { Text("Quiz Completed") },
                text             = { Text("You scored $correctCount out of ${questions.size}") },
                confirmButton    = {
                    TextButton(onClick = {
                        showScoreDialog = false
                        navController.navigate(Screen.Learn.route) {
                            popUpTo(Screen.Learn.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Text("Back to Learn")
                    }
                }
            )
        }
    }
}
