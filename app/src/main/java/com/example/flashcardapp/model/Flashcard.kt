package com.example.flashcardapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


//Flashcard info
@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, //Gives each flashcard a primary key
    val question: String,      
    val answer: String,
    val setName: String,
    val userId: String
)
