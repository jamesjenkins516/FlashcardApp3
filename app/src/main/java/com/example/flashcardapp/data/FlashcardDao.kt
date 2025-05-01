package com.example.flashcardapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcardapp.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao //Data access object. These are all of the database operations (Room Database)
interface FlashcardDao {

    //Returns set names to a specific user
    @Query("SELECT DISTINCT setName FROM flashcards WHERE userId = :userId")
    fun getAllSetNames(userId: String): Flow<List<String>>

    ///Returns flashcards to a specific user
    @Query("SELECT * FROM flashcards WHERE setName = :setName AND userId = :userId")
    fun getFlashcardsForSet(setName: String, userId: String): Flow<List<Flashcard>>

    //New flashcard
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    //Deletes all flashcards from a set for a specific user
    @Query("DELETE FROM flashcards WHERE setName = :setName AND userId = :userId")
    suspend fun deleteFlashcardsForSet(setName: String, userId: String)

    //Renames a set
    @Query("""
        UPDATE flashcards
           SET setName = :newName
         WHERE setName = :oldName
           AND userId   = :userId
    """)
    suspend fun renameSet(oldName: String, newName: String, userId: String)

    //This deletes a flashcard
    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
