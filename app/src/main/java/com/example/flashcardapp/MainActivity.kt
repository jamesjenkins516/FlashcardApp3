package com.example.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.flashcardapp.data.FlashcardDatabaseInstance
import com.example.flashcardapp.navigation.Navigation
import com.example.flashcardapp.ui.theme.FlashcardAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Room database before launching the UI
        FlashcardDatabaseInstance.init(applicationContext)

        setContent {
            FlashcardAppTheme {
                Navigation()
            }
        }
    }
}
