package com.example.flashcardapp.data

import android.content.Context
import androidx.room.Room

object FlashcardDatabaseInstance {
    lateinit var flashcardDao: FlashcardDao

    fun init(context: Context) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            FlashcardDatabase::class.java,
            "flashcard_database"
        ).build()
        flashcardDao = db.flashcardDao()
    }
}
