package com.example.flashcardapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    val setName: String,
    val userId: String
)
