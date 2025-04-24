package com.example.languagetrainingapp.database

import androidx.room.*
import com.example.languagetrainingapp.QuestionType
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE type = :type AND difficulty = :level ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(type: QuestionType, level: Int): QuestionEntity?

    @Query("SELECT * FROM questions WHERE type = :type AND difficulty = :level AND id NOT IN (SELECT id FROM questions ORDER BY lastUsed DESC LIMIT :recentlyUsedCount) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestionExcludingRecent(type: QuestionType, level: Int, recentlyUsedCount: Int = 10): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("UPDATE questions SET lastUsed = :timestamp, timesUsed = timesUsed + 1 WHERE id = :id")
    suspend fun updateQuestionUsage(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM questions WHERE type = :type AND difficulty = :level")
    suspend fun getQuestionCount(type: QuestionType, level: Int): Int

    @Query("SELECT * FROM questions")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>
}
