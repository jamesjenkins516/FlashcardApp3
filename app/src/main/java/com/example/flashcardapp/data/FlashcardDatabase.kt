package com.example.flashcardapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flashcardapp.model.Flashcard

@Database(
    entities = [Flashcard::class],
    version = 2,
    exportSchema = false
)

//Allows you to use methods such as adding or deleting flashcards
abstract class FlashcardDatabase : RoomDatabase() {
    abstract val flashcardDao: FlashcardDao
}
