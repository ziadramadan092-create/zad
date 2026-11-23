package com.example.data.questions

import com.example.model.DifficultyLevel
import com.example.model.QuizCategory
import com.example.model.QuizQuestion

internal fun q(
    id: Long,
    category: QuizCategory,
    difficulty: DifficultyLevel,
    text: String,
    a: String,
    b: String,
    c: String,
    d: String,
    correct: Int,
    explanation: String,
): QuizQuestion = QuizQuestion(
    id = id,
    category = category,
    difficulty = difficulty,
    questionText = text,
    options = listOf(a, b, c, d),
    correctIndex = correct,
    explanation = explanation,
)
