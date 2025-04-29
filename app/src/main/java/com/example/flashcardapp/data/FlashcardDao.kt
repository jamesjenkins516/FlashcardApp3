package com.example.flashcardapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcardapp.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    /** Only return distinct set names for this user */
    @Query("SELECT DISTINCT setName FROM flashcards WHERE userId = :userId")
    fun getAllSetNames(userId: String): Flow<List<String>>

    /** Only return cards for this user’s set */
    @Query("SELECT * FROM flashcards WHERE setName = :setName AND userId = :userId")
    fun getFlashcardsForSet(setName: String, userId: String): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Query("DELETE FROM flashcards WHERE setName = :setName AND userId = :userId")
    suspend fun deleteFlashcardsForSet(setName: String, userId: String)
}
