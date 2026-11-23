package com.example.data

import com.example.data.local.QuestionDao
import com.example.data.local.QuestionEntity
import com.example.data.local.StatsDataStore
import com.example.model.AnsweredRecord
import com.example.model.DailyProgress
import com.example.model.DifficultyLevel
import com.example.model.FontScaleOption
import com.example.model.QuizCategory
import com.example.model.QuizMode
import com.example.model.QuizPreferences
import com.example.model.QuizQuestion
import com.example.model.QuizStats
import com.example.model.RoundRewards
import com.example.model.ThemeMode
import com.example.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class QuizRepository(
    private val questionDao: QuestionDao,
    private val statsDataStore: StatsDataStore,
) {
    val allQuestions: Flow<List<QuizQuestion>> =
        questionDao.observeAllQuestions().map { entities ->
            entities.map { it.toDomain() }
        }

    val stats: Flow<QuizStats> = statsDataStore.statsFlow

    val preferences: Flow<QuizPreferences> = statsDataStore.preferencesFlow

    val userProgress: Flow<UserProgress> = statsDataStore.userProgressFlow

    val dailyProgress: Flow<DailyProgress> = statsDataStore.dailyProgressFlow

    val favoriteTexts: Flow<Set<String>> = statsDataStore.favoriteQuestionTextsFlow

    val mistakeTexts: Flow<Set<String>> = statsDataStore.mistakeQuestionTextsFlow

    suspend fun ensureDefaultQuestionsSeeded() {
        val existingTexts = questionDao.getBuiltInQuestionTexts().toHashSet()
        val missing = DefaultQuestions.questions.filter { it.questionText !in existingTexts }
        if (missing.isNotEmpty()) {
            val isFreshInstall = existingTexts.isEmpty()
            val entities = missing.map {
                QuestionEntity.fromDomain(it, preserveId = isFreshInstall)
            }
            questionDao.insertAll(entities)
        }
    }

    fun getTodayDailyQuestions(): Pair<DailyQuestionBank.DailyDayInfo, List<QuizQuestion>> {
        val dayInfo = DailyQuestionBank.getCurrentDayInfo()
        val questions = DailyQuestionBank.getQuestionsForDay(dayInfo.epochDay)
        return dayInfo to questions
    }

    suspend fun addCustomQuestion(
        category: QuizCategory,
        difficulty: DifficultyLevel,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
    ) {
        val entity = QuestionEntity(
            categoryKey = category.key,
            difficultyKey = difficulty.key,
            questionText = questionText.trim(),
            option0 = options.getOrElse(0) { "" }.trim(),
            option1 = options.getOrElse(1) { "" }.trim(),
            option2 = options.getOrElse(2) { "" }.trim(),
            option3 = options.getOrElse(3) { "" }.trim(),
            correctIndex = correctIndex.coerceIn(0, 3),
            explanation = explanation.trim(),
            isCustom = true,
        )
        questionDao.insertQuestion(entity)
    }

    suspend fun importCustomQuestions(questions: List<QuizQuestion>): Int {
        if (questions.isEmpty()) return 0
        val existing = allQuestions.first().map { it.questionText.trim() }.toHashSet()
        val toInsert = questions
            .filter { it.questionText.trim() !in existing }
            .map { q ->
                QuestionEntity(
                    categoryKey = q.category.key,
                    difficultyKey = q.difficulty.key,
                    questionText = q.questionText.trim(),
                    option0 = q.options.getOrElse(0) { "" }.trim(),
                    option1 = q.options.getOrElse(1) { "" }.trim(),
                    option2 = q.options.getOrElse(2) { "" }.trim(),
                    option3 = q.options.getOrElse(3) { "" }.trim(),
                    correctIndex = q.correctIndex.coerceIn(0, 3),
                    explanation = q.explanation.trim(),
                    isCustom = true,
                )
            }
        if (toInsert.isNotEmpty()) {
            questionDao.insertAll(toInsert)
        }
        return toInsert.size
    }

    suspend fun deleteCustomQuestion(id: Long) {
        questionDao.deleteCustomQuestionById(id)
    }

    suspend fun toggleFavorite(questionText: String): Boolean =
        statsDataStore.toggleFavorite(questionText)

    suspend fun recordRoundCompletion(
        mode: QuizMode,
        records: List<AnsweredRecord>,
        survivalStreakAchieved: Int = 0,
    ): RoundRewards {
        return statsDataStore.recordRoundCompletion(
            mode = mode,
            records = records,
            survivalStreakAchieved = survivalStreakAchieved,
        )
    }

    suspend fun resetStats() {
        statsDataStore.resetStats()
    }

    suspend fun updateTimerSettings(enabled: Boolean, seconds: Int) {
        statsDataStore.updateTimerSettings(enabled, seconds)
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        statsDataStore.updateThemeMode(mode)
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        statsDataStore.updateSoundEnabled(enabled)
    }

    suspend fun updateHapticsEnabled(enabled: Boolean) {
        statsDataStore.updateHapticsEnabled(enabled)
    }

    suspend fun updateFontScale(option: FontScaleOption) {
        statsDataStore.updateFontScale(option)
    }

    suspend fun updateReminderSettings(enabled: Boolean, hour: Int, minute: Int) {
        statsDataStore.updateReminderSettings(enabled, hour, minute)
    }
}
