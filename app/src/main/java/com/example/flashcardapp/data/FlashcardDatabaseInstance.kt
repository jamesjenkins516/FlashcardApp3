package com.example.flashcardapp.data

import android.content.Context
import androidx.room.Room



//This is used to ensure there is only one database for f;ashcards
object FlashcardDatabaseInstance {
    @Volatile private var INSTANCE: FlashcardDatabase? = null

    private fun getDatabase(context: Context): FlashcardDatabase =
        INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                FlashcardDatabase::class.java,
                "flashcard.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }


    fun flashcardDao(context: Context): FlashcardDao =
        getDatabase(context).flashcardDao
}
