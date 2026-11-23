package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY isCustom DESC, id ASC")
    fun observeAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT questionText FROM questions WHERE isCustom = 0")
    suspend fun getBuiltInQuestionTexts(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Query("DELETE FROM questions WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomQuestionById(id: Long)
}
