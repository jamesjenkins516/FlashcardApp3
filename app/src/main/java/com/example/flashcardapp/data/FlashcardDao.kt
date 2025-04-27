package com.example.flashcardapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcardapp.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    /** Return the list of all distinct set names. */
    @Query("SELECT DISTINCT setName FROM flashcards")
    fun getAllSetNames(): Flow<List<String>>

    /** Return all flashcards belonging to the given set. */
    @Query("SELECT * FROM flashcards WHERE setName = :setName")
    fun getFlashcardsForSet(setName: String): Flow<List<Flashcard>>

    /** Insert (or replace on conflict) a single flashcard. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)
}
