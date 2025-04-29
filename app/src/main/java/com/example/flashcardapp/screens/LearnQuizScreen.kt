package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.model.Flashcard
import com.example.flashcardapp.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnQuizScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    setName: String
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
        ?: return

    // Load & shuffle only this user’s cards
    val allCards by flashcardDao
        .getFlashcardsForSet(setName, userId)
        .collectAsState(initial = emptyList())

    val questions = remember(allCards) { allCards.shuffled() }
    // … rest of your quiz state & UI remains the same …
}
