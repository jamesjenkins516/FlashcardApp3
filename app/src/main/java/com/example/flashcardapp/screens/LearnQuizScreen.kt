package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.Screen
import com.example.flashcardapp.data.FlashcardDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnQuizScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    setName: String
) {
    // 1. Load the cards for this set and shuffle them
    val allCards by flashcardDao
        .getFlashcardsForSet(setName)
        .collectAsState(initial = emptyList())
    val questions = remember(allCards) { allCards.shuffled() }

    // 2. Quiz state
    var currentIndex by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var answerSubmitted by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showScoreDialog by remember { mutableStateOf(false) }

    // Shortcut for the current card
    val card = questions.getOrNull(currentIndex)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quiz: $setName") })
        }
    ) { padding ->
        if (card == null) {
            // No cards in this set
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No cards in this set.", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question text
            Text(
                text = "Q${currentIndex + 1}/${questions.size}: ${card.question}",
                style = MaterialTheme.typography.titleMedium
            )

            // Build four answer choices (one correct + three random wrongs)
            val options = remember(allCards, currentIndex) {
                val wrongs = allCards
                    .filter { it.question != card.question }
                    .shuffled()
                    .take(3)
                    .map { it.answer }
                (wrongs + card.answer).shuffled()
            }

            // Render each choice
            options.forEach { answer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !answerSubmitted) {
                            selectedAnswer = answer
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            // after submission: correct answer
                            answerSubmitted && answer == card.answer ->
                                MaterialTheme.colorScheme.primaryContainer
                            // after submission: wrong answer you chose
                            answerSubmitted && answer == selectedAnswer ->
                                MaterialTheme.colorScheme.errorContainer
                            // before submission: highlight your current pick
                            selectedAnswer == answer ->
                                MaterialTheme.colorScheme.secondaryContainer
                            else ->
                                MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Text(
                        text = answer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Moved button immediately below answers, right-aligned
            val isLast = currentIndex == questions.lastIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (!answerSubmitted) {
                            // Submit current selection
                            if (selectedAnswer == card.answer) {
                                correctCount++
                            }
                            if (isLast) {
                                // end of quiz → show final score
                                showScoreDialog = true
                            } else {
                                answerSubmitted = true
                            }
                        } else {
                            // Move to next question
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
                            !answerSubmitted && !isLast -> "Submit"
                            answerSubmitted && !isLast -> "Next"
                            else -> ""
                        }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }

        // Final score dialog
        if (showScoreDialog) {
            AlertDialog(
                onDismissRequest = { /* block outside touches */ },
                title = { Text("Quiz Completed") },
                text = { Text("You scored $correctCount out of ${questions.size}") },
                confirmButton = {
                    TextButton(onClick = {
                        showScoreDialog = false
                        navController.navigate(Screen.Learn.route) {
                            popUpTo(Screen.Learn.route) { inclusive = false }
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
