package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.DailyQuestionBank
import com.example.model.AnsweredRecord
import com.example.model.BadgeCatalog
import com.example.model.BadgeDefinition
import com.example.model.DailyProgress
import com.example.model.FontScaleOption
import com.example.model.LevelSystem
import com.example.model.QuizCategory
import com.example.model.QuizMode
import com.example.model.QuizPreferences
import com.example.model.QuizStats
import com.example.model.RoundRewards
import com.example.model.ThemeMode
import com.example.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zad_al_marifah_prefs")

class StatsDataStore(private val context: Context) {

    private object Keys {
        // Standard & Survival Quiz Stats
        val HIGHEST_SCORE_PERCENT = intPreferencesKey("highest_score_percent")
        val HIGHEST_SCORE_CORRECT = intPreferencesKey("highest_score_correct")
        val HIGHEST_SCORE_TOTAL = intPreferencesKey("highest_score_total")
        val TOTAL_QUESTIONS_ANSWERED = intPreferencesKey("total_questions_answered")
        val TOTAL_CORRECT_ANSWERS = intPreferencesKey("total_correct_answers")
        val COMPLETED_QUIZZES = intPreferencesKey("completed_quizzes")
        val BEST_SURVIVAL_STREAK = intPreferencesKey("best_survival_streak")

        // Preferences
        val TIMER_ENABLED = booleanPreferencesKey("timer_enabled")
        val TIMER_SECONDS = intPreferencesKey("timer_seconds")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val FONT_SCALE = stringPreferencesKey("font_scale")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")

        // Leveling & Badges & Per-Category Analytics
        val TOTAL_XP = intPreferencesKey("total_xp")
        val UNLOCKED_BADGES = stringSetPreferencesKey("unlocked_badges")
        val QURAN_CORRECT = intPreferencesKey("quran_correct")
        val SEERAH_CORRECT = intPreferencesKey("seerah_correct")
        val FIQH_CORRECT = intPreferencesKey("fiqh_correct")
        val PROPHETS_CORRECT = intPreferencesKey("prophets_correct")
        val GENERAL_CORRECT = intPreferencesKey("general_correct")
        val QURAN_ANSWERED = intPreferencesKey("quran_answered")
        val SEERAH_ANSWERED = intPreferencesKey("seerah_answered")
        val FIQH_ANSWERED = intPreferencesKey("fiqh_answered")
        val PROPHETS_ANSWERED = intPreferencesKey("prophets_answered")
        val GENERAL_ANSWERED = intPreferencesKey("general_answered")
        val PERFECT_ROUNDS_COUNT = intPreferencesKey("perfect_rounds_count")
        val COMBINED_CORRECT = intPreferencesKey("combined_correct")
        val COMBINED_QUIZZES = intPreferencesKey("combined_quizzes")

        // Favorites & Smart Mistake Review
        val FAVORITE_QUESTION_TEXTS = stringSetPreferencesKey("favorite_question_texts")
        val MISTAKE_QUESTION_TEXTS = stringSetPreferencesKey("mistake_question_texts")

        // Separate Daily Questions Progress
        val DAILY_LAST_DATE = stringPreferencesKey("daily_last_date")
        val DAILY_LAST_EPOCH_DAY = longPreferencesKey("daily_last_epoch_day")
        val DAILY_TODAY_CORRECT = intPreferencesKey("daily_today_correct")
        val DAILY_TODAY_TOTAL = intPreferencesKey("daily_today_total")
        val DAILY_TOTAL_COMPLETED_DAYS = intPreferencesKey("daily_total_completed_days")
        val DAILY_STREAK = intPreferencesKey("daily_streak")
        val DAILY_TOTAL_ANSWERED = intPreferencesKey("daily_total_answered")
        val DAILY_TOTAL_CORRECT = intPreferencesKey("daily_total_correct")
        val DAILY_BEST_PERCENT = intPreferencesKey("daily_best_percent")
    }

    val statsFlow: Flow<QuizStats> = context.dataStore.data.map { prefs ->
        QuizStats(
            highestScorePercent = prefs[Keys.HIGHEST_SCORE_PERCENT] ?: 0,
            highestScoreCorrect = prefs[Keys.HIGHEST_SCORE_CORRECT] ?: 0,
            highestScoreTotal = prefs[Keys.HIGHEST_SCORE_TOTAL] ?: 0,
            totalQuestionsAnswered = prefs[Keys.TOTAL_QUESTIONS_ANSWERED] ?: 0,
            totalCorrectAnswers = prefs[Keys.TOTAL_CORRECT_ANSWERS] ?: 0,
            completedQuizzes = prefs[Keys.COMPLETED_QUIZZES] ?: 0,
            bestSurvivalStreak = prefs[Keys.BEST_SURVIVAL_STREAK] ?: 0,
        )
    }

    val preferencesFlow: Flow<QuizPreferences> = context.dataStore.data.map { prefs ->
        QuizPreferences(
            timerEnabled = prefs[Keys.TIMER_ENABLED] ?: false,
            timerSeconds = prefs[Keys.TIMER_SECONDS] ?: 25,
            themeMode = ThemeMode.fromKey(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.key),
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            fontScale = FontScaleOption.fromKey(prefs[Keys.FONT_SCALE] ?: FontScaleOption.NORMAL.key),
            reminderEnabled = prefs[Keys.REMINDER_ENABLED] ?: false,
            reminderHour = prefs[Keys.REMINDER_HOUR] ?: 20,
            reminderMinute = prefs[Keys.REMINDER_MINUTE] ?: 0,
        )
    }

    val userProgressFlow: Flow<UserProgress> = context.dataStore.data.map { prefs ->
        val qCorrect = prefs[Keys.QURAN_CORRECT] ?: 0
        val sCorrect = prefs[Keys.SEERAH_CORRECT] ?: 0
        val fCorrect = prefs[Keys.FIQH_CORRECT] ?: 0
        val pCorrect = prefs[Keys.PROPHETS_CORRECT] ?: 0
        val gCorrect = prefs[Keys.GENERAL_CORRECT] ?: 0

        UserProgress(
            totalXp = prefs[Keys.TOTAL_XP] ?: 0,
            unlockedBadgeIds = prefs[Keys.UNLOCKED_BADGES] ?: emptySet(),
            quranCorrect = qCorrect,
            seerahCorrect = sCorrect,
            fiqhCorrect = fCorrect,
            prophetsCorrect = pCorrect,
            generalCorrect = gCorrect,
            quranAnswered = maxOf(prefs[Keys.QURAN_ANSWERED] ?: 0, qCorrect),
            seerahAnswered = maxOf(prefs[Keys.SEERAH_ANSWERED] ?: 0, sCorrect),
            fiqhAnswered = maxOf(prefs[Keys.FIQH_ANSWERED] ?: 0, fCorrect),
            prophetsAnswered = maxOf(prefs[Keys.PROPHETS_ANSWERED] ?: 0, pCorrect),
            generalAnswered = maxOf(prefs[Keys.GENERAL_ANSWERED] ?: 0, gCorrect),
            perfectRoundsCount = prefs[Keys.PERFECT_ROUNDS_COUNT] ?: 0,
            totalCombinedCorrect = prefs[Keys.COMBINED_CORRECT] ?: 0,
            totalCombinedQuizzes = prefs[Keys.COMBINED_QUIZZES] ?: 0,
        )
    }

    val dailyProgressFlow: Flow<DailyProgress> = context.dataStore.data.map { prefs ->
        val dayInfo = DailyQuestionBank.getCurrentDayInfo()
        val lastDate = prefs[Keys.DAILY_LAST_DATE] ?: ""
        val isToday = lastDate == dayInfo.dateKey
        val lastEpochDay = prefs[Keys.DAILY_LAST_EPOCH_DAY] ?: -1L
        val storedStreak = prefs[Keys.DAILY_STREAK] ?: 0
        val activeStreak = if (isToday || lastEpochDay == dayInfo.epochDay - 1L) storedStreak else 0

        DailyProgress(
            todayKey = dayInfo.dateKey,
            lastCompletedDate = lastDate,
            todayCorrect = if (isToday) (prefs[Keys.DAILY_TODAY_CORRECT] ?: 0) else 0,
            todayTotal = if (isToday) (prefs[Keys.DAILY_TODAY_TOTAL] ?: 0) else 0,
            totalCompletedDays = prefs[Keys.DAILY_TOTAL_COMPLETED_DAYS] ?: 0,
            currentStreak = activeStreak,
            totalDailyAnswered = prefs[Keys.DAILY_TOTAL_ANSWERED] ?: 0,
            totalDailyCorrect = prefs[Keys.DAILY_TOTAL_CORRECT] ?: 0,
            bestDailyPercent = prefs[Keys.DAILY_BEST_PERCENT] ?: 0,
        )
    }

    val favoriteQuestionTextsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.FAVORITE_QUESTION_TEXTS] ?: emptySet()
    }

    val mistakeQuestionTextsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.MISTAKE_QUESTION_TEXTS] ?: emptySet()
    }

    suspend fun toggleFavorite(questionText: String): Boolean {
        var isNowFavorite = false
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.FAVORITE_QUESTION_TEXTS] ?: emptySet()).toMutableSet()
            isNowFavorite = if (questionText in current) {
                current.remove(questionText)
                false
            } else {
                current.add(questionText)
                true
            }
            prefs[Keys.FAVORITE_QUESTION_TEXTS] = current
        }
        return isNowFavorite
    }

    suspend fun recordRoundCompletion(
        mode: QuizMode,
        records: List<AnsweredRecord>,
        survivalStreakAchieved: Int = 0,
    ): RoundRewards {
        val totalCount = records.size
        val correctCount = records.count { it.isCorrect }
        if (totalCount <= 0) {
            val currentInfo = LevelSystem.computeLevelInfo(0)
            return RoundRewards(0, null, currentInfo, emptyList())
        }

        val dayInfo = DailyQuestionBank.getCurrentDayInfo()
        val percent = ((correctCount * 100f) / totalCount).toInt()
        val isPerfect = correctCount == totalCount && totalCount >= 5

        var resultRewards = RoundRewards(
            xpEarned = 0,
            leveledUpFrom = null,
            newLevelInfo = LevelSystem.computeLevelInfo(0),
            newlyUnlockedBadges = emptyList(),
        )

        context.dataStore.edit { prefs ->
            // Always update mistake tracker (add new mistakes, clear mastered ones)
            val mistakes = (prefs[Keys.MISTAKE_QUESTION_TEXTS] ?: emptySet()).toMutableSet()
            records.forEach { rec ->
                val key = rec.question.questionText
                if (rec.isCorrect) {
                    mistakes.remove(key)
                } else {
                    mistakes.add(key)
                }
            }
            prefs[Keys.MISTAKE_QUESTION_TEXTS] = mistakes

            val oldXp = prefs[Keys.TOTAL_XP] ?: 0
            val oldLevel = LevelSystem.computeLevelInfo(oldXp).level

            if (mode == QuizMode.DAILY) {
                val alreadyCompletedToday = (prefs[Keys.DAILY_LAST_DATE] ?: "") == dayInfo.dateKey
                if (alreadyCompletedToday) {
                    val existingLevelInfo = LevelSystem.computeLevelInfo(oldXp)
                    resultRewards = RoundRewards(
                        xpEarned = 0,
                        leveledUpFrom = null,
                        newLevelInfo = existingLevelInfo,
                        newlyUnlockedBadges = emptyList(),
                    )
                    return@edit
                }

                val lastEpoch = prefs[Keys.DAILY_LAST_EPOCH_DAY] ?: -1L
                val prevStreak = prefs[Keys.DAILY_STREAK] ?: 0
                val newStreak = if (lastEpoch == dayInfo.epochDay - 1L) prevStreak + 1 else 1

                prefs[Keys.DAILY_LAST_DATE] = dayInfo.dateKey
                prefs[Keys.DAILY_LAST_EPOCH_DAY] = dayInfo.epochDay
                prefs[Keys.DAILY_TODAY_CORRECT] = correctCount
                prefs[Keys.DAILY_TODAY_TOTAL] = totalCount
                prefs[Keys.DAILY_TOTAL_COMPLETED_DAYS] = (prefs[Keys.DAILY_TOTAL_COMPLETED_DAYS] ?: 0) + 1
                prefs[Keys.DAILY_STREAK] = newStreak
                prefs[Keys.DAILY_TOTAL_ANSWERED] = (prefs[Keys.DAILY_TOTAL_ANSWERED] ?: 0) + totalCount
                prefs[Keys.DAILY_TOTAL_CORRECT] = (prefs[Keys.DAILY_TOTAL_CORRECT] ?: 0) + correctCount
                val bestDaily = prefs[Keys.DAILY_BEST_PERCENT] ?: 0
                if (percent > bestDaily) {
                    prefs[Keys.DAILY_BEST_PERCENT] = percent
                }
            } else {
                if (mode != QuizMode.SURVIVAL) {
                    val currentBestPercent = prefs[Keys.HIGHEST_SCORE_PERCENT] ?: 0
                    val currentBestTotal = prefs[Keys.HIGHEST_SCORE_TOTAL] ?: 0
                    val isNewBest = percent > currentBestPercent ||
                        (percent == currentBestPercent && totalCount > currentBestTotal)

                    if (isNewBest) {
                        prefs[Keys.HIGHEST_SCORE_PERCENT] = percent
                        prefs[Keys.HIGHEST_SCORE_CORRECT] = correctCount
                        prefs[Keys.HIGHEST_SCORE_TOTAL] = totalCount
                    }
                } else {
                    val currentBestSurvival = prefs[Keys.BEST_SURVIVAL_STREAK] ?: 0
                    if (survivalStreakAchieved > currentBestSurvival) {
                        prefs[Keys.BEST_SURVIVAL_STREAK] = survivalStreakAchieved
                    }
                }

                prefs[Keys.TOTAL_QUESTIONS_ANSWERED] =
                    (prefs[Keys.TOTAL_QUESTIONS_ANSWERED] ?: 0) + totalCount
                prefs[Keys.TOTAL_CORRECT_ANSWERS] =
                    (prefs[Keys.TOTAL_CORRECT_ANSWERS] ?: 0) + correctCount
                prefs[Keys.COMPLETED_QUIZZES] =
                    (prefs[Keys.COMPLETED_QUIZZES] ?: 0) + 1
            }

            // Compute XP earned
            val baseXp = totalCount * 10
            val correctXp = correctCount * 15
            val dailyBonus = if (mode == QuizMode.DAILY) 40 else 0
            val survivalBonus = if (mode == QuizMode.SURVIVAL) survivalStreakAchieved * 5 else 0
            val perfectBonus = if (isPerfect && mode != QuizMode.SURVIVAL) 30 else 0
            val earnedXp = baseXp + correctXp + dailyBonus + survivalBonus + perfectBonus
            val newXp = oldXp + earnedXp
            prefs[Keys.TOTAL_XP] = newXp

            // Category analytics (correct + answered)
            val quranCorrectAdd = records.count { it.isCorrect && it.question.category == QuizCategory.QURAN }
            val seerahCorrectAdd = records.count { it.isCorrect && it.question.category == QuizCategory.SEERAH }
            val fiqhCorrectAdd = records.count { it.isCorrect && it.question.category == QuizCategory.FIQH }
            val prophetsCorrectAdd = records.count { it.isCorrect && it.question.category == QuizCategory.PROPHETS }
            val generalCorrectAdd = records.count { it.isCorrect && it.question.category == QuizCategory.GENERAL }

            val quranAnsAdd = records.count { it.question.category == QuizCategory.QURAN }
            val seerahAnsAdd = records.count { it.question.category == QuizCategory.SEERAH }
            val fiqhAnsAdd = records.count { it.question.category == QuizCategory.FIQH }
            val prophetsAnsAdd = records.count { it.question.category == QuizCategory.PROPHETS }
            val generalAnsAdd = records.count { it.question.category == QuizCategory.GENERAL }

            val quranTotal = (prefs[Keys.QURAN_CORRECT] ?: 0) + quranCorrectAdd
            val seerahTotal = (prefs[Keys.SEERAH_CORRECT] ?: 0) + seerahCorrectAdd
            val fiqhTotal = (prefs[Keys.FIQH_CORRECT] ?: 0) + fiqhCorrectAdd
            val prophetsTotal = (prefs[Keys.PROPHETS_CORRECT] ?: 0) + prophetsCorrectAdd
            val generalTotal = (prefs[Keys.GENERAL_CORRECT] ?: 0) + generalCorrectAdd

            prefs[Keys.QURAN_CORRECT] = quranTotal
            prefs[Keys.SEERAH_CORRECT] = seerahTotal
            prefs[Keys.FIQH_CORRECT] = fiqhTotal
            prefs[Keys.PROPHETS_CORRECT] = prophetsTotal
            prefs[Keys.GENERAL_CORRECT] = generalTotal

            prefs[Keys.QURAN_ANSWERED] = (prefs[Keys.QURAN_ANSWERED] ?: 0) + quranAnsAdd
            prefs[Keys.SEERAH_ANSWERED] = (prefs[Keys.SEERAH_ANSWERED] ?: 0) + seerahAnsAdd
            prefs[Keys.FIQH_ANSWERED] = (prefs[Keys.FIQH_ANSWERED] ?: 0) + fiqhAnsAdd
            prefs[Keys.PROPHETS_ANSWERED] = (prefs[Keys.PROPHETS_ANSWERED] ?: 0) + prophetsAnsAdd
            prefs[Keys.GENERAL_ANSWERED] = (prefs[Keys.GENERAL_ANSWERED] ?: 0) + generalAnsAdd

            val perfectTotal = (prefs[Keys.PERFECT_ROUNDS_COUNT] ?: 0) + if (isPerfect && mode != QuizMode.SURVIVAL) 1 else 0
            val combinedCorrect = (prefs[Keys.COMBINED_CORRECT] ?: 0) + correctCount
            val combinedQuizzes = (prefs[Keys.COMBINED_QUIZZES] ?: 0) + 1

            prefs[Keys.PERFECT_ROUNDS_COUNT] = perfectTotal
            prefs[Keys.COMBINED_CORRECT] = combinedCorrect
            prefs[Keys.COMBINED_QUIZZES] = combinedQuizzes

            val dailyDays = prefs[Keys.DAILY_TOTAL_COMPLETED_DAYS] ?: 0
            val dailyStreak = prefs[Keys.DAILY_STREAK] ?: 0
            val bestSurvival = prefs[Keys.BEST_SURVIVAL_STREAK] ?: 0

            val prevBadges = prefs[Keys.UNLOCKED_BADGES] ?: emptySet()
            val updatedBadges = prevBadges.toMutableSet()
            val newlyUnlocked = mutableListOf<BadgeDefinition>()

            fun checkBadge(badgeId: String, condition: Boolean) {
                if (condition && badgeId !in prevBadges) {
                    updatedBadges.add(badgeId)
                    BadgeCatalog.all.firstOrNull { it.id == badgeId }?.let { newlyUnlocked.add(it) }
                }
            }

            checkBadge(BadgeCatalog.FIRST_STEP, combinedQuizzes >= 1)
            checkBadge(BadgeCatalog.PERSISTENT_50, combinedCorrect >= 50)
            checkBadge(BadgeCatalog.SCHOLAR_100, combinedCorrect >= 100)
            checkBadge(BadgeCatalog.PERFECTIONIST, perfectTotal >= 1)
            checkBadge(BadgeCatalog.MASTER_QURAN, quranTotal >= 10)
            checkBadge(BadgeCatalog.MASTER_SEERAH, seerahTotal >= 10)
            checkBadge(BadgeCatalog.MASTER_FIQH, fiqhTotal >= 10)
            checkBadge(BadgeCatalog.MASTER_PROPHETS, prophetsTotal >= 10)
            checkBadge(BadgeCatalog.DAILY_CHALLENGER, dailyDays >= 1)
            checkBadge(BadgeCatalog.DAILY_STREAK_3, dailyStreak >= 3)
            checkBadge(BadgeCatalog.SURVIVAL_10, bestSurvival >= 10)

            prefs[Keys.UNLOCKED_BADGES] = updatedBadges

            val newLevelInfo = LevelSystem.computeLevelInfo(newXp)
            resultRewards = RoundRewards(
                xpEarned = earnedXp,
                leveledUpFrom = if (newLevelInfo.level > oldLevel) oldLevel else null,
                newLevelInfo = newLevelInfo,
                newlyUnlockedBadges = newlyUnlocked,
            )
        }

        return resultRewards
    }

    suspend fun resetStats() {
        context.dataStore.edit { prefs ->
            prefs[Keys.HIGHEST_SCORE_PERCENT] = 0
            prefs[Keys.HIGHEST_SCORE_CORRECT] = 0
            prefs[Keys.HIGHEST_SCORE_TOTAL] = 0
            prefs[Keys.TOTAL_QUESTIONS_ANSWERED] = 0
            prefs[Keys.TOTAL_CORRECT_ANSWERS] = 0
            prefs[Keys.COMPLETED_QUIZZES] = 0
            prefs[Keys.BEST_SURVIVAL_STREAK] = 0

            prefs[Keys.TOTAL_XP] = 0
            prefs[Keys.UNLOCKED_BADGES] = emptySet()
            prefs[Keys.QURAN_CORRECT] = 0
            prefs[Keys.SEERAH_CORRECT] = 0
            prefs[Keys.FIQH_CORRECT] = 0
            prefs[Keys.PROPHETS_CORRECT] = 0
            prefs[Keys.GENERAL_CORRECT] = 0
            prefs[Keys.QURAN_ANSWERED] = 0
            prefs[Keys.SEERAH_ANSWERED] = 0
            prefs[Keys.FIQH_ANSWERED] = 0
            prefs[Keys.PROPHETS_ANSWERED] = 0
            prefs[Keys.GENERAL_ANSWERED] = 0
            prefs[Keys.PERFECT_ROUNDS_COUNT] = 0
            prefs[Keys.COMBINED_CORRECT] = 0
            prefs[Keys.COMBINED_QUIZZES] = 0
            prefs[Keys.MISTAKE_QUESTION_TEXTS] = emptySet()

            prefs[Keys.DAILY_LAST_DATE] = ""
            prefs[Keys.DAILY_LAST_EPOCH_DAY] = -1L
            prefs[Keys.DAILY_TODAY_CORRECT] = 0
            prefs[Keys.DAILY_TODAY_TOTAL] = 0
            prefs[Keys.DAILY_TOTAL_COMPLETED_DAYS] = 0
            prefs[Keys.DAILY_STREAK] = 0
            prefs[Keys.DAILY_TOTAL_ANSWERED] = 0
            prefs[Keys.DAILY_TOTAL_CORRECT] = 0
            prefs[Keys.DAILY_BEST_PERCENT] = 0
        }
    }

    suspend fun updateTimerSettings(enabled: Boolean, seconds: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TIMER_ENABLED] = enabled
            prefs[Keys.TIMER_SECONDS] = seconds.coerceIn(10, 60)
        }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.key
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun updateFontScale(option: FontScaleOption) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FONT_SCALE] = option.key
        }
    }

    suspend fun updateReminderSettings(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.REMINDER_ENABLED] = enabled
            prefs[Keys.REMINDER_HOUR] = hour.coerceIn(0, 23)
            prefs[Keys.REMINDER_MINUTE] = minute.coerceIn(0, 59)
        }
    }
}
