package com.example.flashcardapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.flashcardapp.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Query("SELECT DISTINCT setName FROM Flashcard")
    fun getAllSetNames(): Flow<List<String>>

    @Query("SELECT * FROM Flashcard WHERE setName = :setName")
    fun getFlashcardsBySet(setName: String): Flow<List<Flashcard>>

    @Insert
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
