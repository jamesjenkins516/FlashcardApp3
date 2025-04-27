package com.example.flashcardapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flashcardapp.model.Flashcard

@Database(entities = [Flashcard::class], version = 1)
abstract class FlashcardDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
}
