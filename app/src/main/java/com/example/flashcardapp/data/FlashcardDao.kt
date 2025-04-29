// app/src/main/java/com/example/flashcardapp/data/FlashcardDao.kt

package com.example.flashcardapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcardapp.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    /** Return only the distinct set names belonging to the given user */
    @Query("SELECT DISTINCT setName FROM flashcards WHERE userId = :userId")
    fun getAllSetNames(userId: String): Flow<List<String>>

    /** Return only the flashcards for a specific set and user */
    @Query("SELECT * FROM flashcards WHERE setName = :setName AND userId = :userId")
    fun getFlashcardsForSet(setName: String, userId: String): Flow<List<Flashcard>>

    /** Insert or replace a flashcard */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    /** Delete all flashcards in a set for a specific user */
    @Query("DELETE FROM flashcards WHERE setName = :setName AND userId = :userId")
    suspend fun deleteFlashcardsForSet(setName: String, userId: String)

    /** Rename an entire set for this user */
    @Query("""
        UPDATE flashcards
           SET setName = :newName
         WHERE setName = :oldName
           AND userId   = :userId
    """)
    suspend fun renameSet(oldName: String, newName: String, userId: String)

    /** Delete a single flashcard */
    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
