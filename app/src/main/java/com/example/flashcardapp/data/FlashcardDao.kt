package com.example.flashcardapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcardapp.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT DISTINCT setName FROM flashcards")
    fun getAllSetNames(): Flow<List<String>>

    @Query("SELECT * FROM flashcards WHERE setName = :setName")
    fun getFlashcardsForSet(setName: String): Flow<List<Flashcard>>

    // ← NEW: fetch every flashcard in the database
    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Query("DELETE FROM flashcards WHERE setName = :setName")
    suspend fun deleteFlashcardsForSet(setName: String)
}
