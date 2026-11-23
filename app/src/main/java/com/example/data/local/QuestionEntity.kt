package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.DifficultyLevel
import com.example.model.QuizCategory
import com.example.model.QuizQuestion

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryKey: String,
    val difficultyKey: String,
    val questionText: String,
    val option0: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val correctIndex: Int,
    val explanation: String,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
) {
    fun toDomain(): QuizQuestion = QuizQuestion(
        id = id,
        category = QuizCategory.fromKey(categoryKey),
        difficulty = DifficultyLevel.fromKey(difficultyKey),
        questionText = questionText,
        options = listOf(option0, option1, option2, option3),
        correctIndex = correctIndex.coerceIn(0, 3),
        explanation = explanation,
        isCustom = isCustom,
    )

    companion object {
        fun fromDomain(question: QuizQuestion, preserveId: Boolean = false): QuestionEntity =
            QuestionEntity(
                id = if (preserveId) question.id else 0,
                categoryKey = question.category.key,
                difficultyKey = question.difficulty.key,
                questionText = question.questionText,
                option0 = question.options.getOrElse(0) { "" },
                option1 = question.options.getOrElse(1) { "" },
                option2 = question.options.getOrElse(2) { "" },
                option3 = question.options.getOrElse(3) { "" },
                correctIndex = question.correctIndex.coerceIn(0, 3),
                explanation = question.explanation,
                isCustom = question.isCustom,
            )
    }
}
