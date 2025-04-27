package com.example.flashcardapp.data

import androidx.room.*
import com.example.flashcardapp.model.Flashcard

@Dao
interface FlashcardDao {

    @Query("SELECT DISTINCT setName FROM Flashcard WHERE userId = :userId")
    suspend fun getAllSetNames(userId: String): List<String>

    @Query("SELECT * FROM Flashcard WHERE setName = :setName AND userId = :userId")
    suspend fun getFlashcardsBySet(setName: String, userId: String): List<Flashcard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
