package com.example.model

enum class QuizCategory(
    val key: String,
    val displayName: String,
    val shortName: String,
    val subtitle: String,
) {
    ALL("all", "الكل", "الكل", "أسئلة شاملة من جميع الأقسام"),
    QURAN("quran", "القرآن الكريم", "القرآن", "السور، الآيات، وعلوم القرآن"),
    SEERAH("seerah", "السيرة النبوية", "السيرة", "حياة النبي ﷺ وغزواته وصحبه"),
    FIQH("fiqh", "الفقه والعبادات", "الفقه", "الطهارة، الصلاة، الصيام، والزكاة"),
    PROPHETS("prophets", "قصص الأنبياء", "الأنبياء", "سير الرسل وعبرهم في القرآن"),
    GENERAL("general", "معلومات إسلامية عامة", "عامة", "التاريخ، الحضارة، والآداب الإسلامية");

    companion object {
        fun fromKey(key: String): QuizCategory =
            entries.firstOrNull { it.key == key } ?: GENERAL

        val selectableCategories: List<QuizCategory> =
            listOf(QURAN, SEERAH, FIQH, PROPHETS, GENERAL)
    }
}

enum class DifficultyLevel(
    val key: String,
    val displayName: String,
) {
    ALL("all", "الكل"),
    EASY("easy", "سهل"),
    MEDIUM("medium", "متوسط"),
    HARD("hard", "صعب");

    companion object {
        fun fromKey(key: String): DifficultyLevel =
            entries.firstOrNull { it.key == key } ?: MEDIUM

        val specificLevels: List<DifficultyLevel> = listOf(EASY, MEDIUM, HARD)
    }
}

enum class QuestionCountOption(
    val count: Int,
    val displayName: String,
) {
    FIVE(5, "5 أسئلة"),
    TEN(10, "10 أسئلة"),
    TWENTY(20, "20 سؤالاً"),
    ALL(Int.MAX_VALUE, "كل الأسئلة"),
}

enum class ThemeMode(val key: String, val displayName: String) {
    SYSTEM("system", "تلقائي"),
    LIGHT("light", "فاتح"),
    DARK("dark", "داكن");

    companion object {
        fun fromKey(key: String): ThemeMode =
            entries.firstOrNull { it.key == key } ?: SYSTEM
    }
}

enum class FontScaleOption(
    val key: String,
    val displayName: String,
    val multiplier: Float,
) {
    NORMAL("normal", "عادي", 1.0f),
    LARGE("large", "كبير", 1.15f),
    XLARGE("xlarge", "كبير جداً", 1.3f);

    companion object {
        fun fromKey(key: String): FontScaleOption =
            entries.firstOrNull { it.key == key } ?: NORMAL
    }
}

enum class QuizMode {
    STANDARD,
    DAILY,
    SURVIVAL,
    FAVORITES,
    MISTAKES,
}

data class QuizQuestion(
    val id: Long,
    val category: QuizCategory,
    val difficulty: DifficultyLevel,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val isCustom: Boolean = false,
)

data class AnsweredRecord(
    val question: QuizQuestion,
    val selectedIndex: Int?,
    val isTimedOut: Boolean,
    val isCorrect: Boolean,
)

data class PerformanceBreakdown(
    val label: String,
    val correct: Int,
    val total: Int,
) {
    val percentage: Int
        get() = if (total == 0) 0 else ((correct * 100f) / total).toInt()
}

data class CategoryPerformance(
    val category: QuizCategory,
    val answered: Int,
    val correct: Int,
) {
    val accuracyPercent: Int
        get() = if (answered == 0) 0 else ((correct * 100f) / answered).toInt()
}

data class QuizStats(
    val highestScorePercent: Int = 0,
    val highestScoreCorrect: Int = 0,
    val highestScoreTotal: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val completedQuizzes: Int = 0,
    val bestSurvivalStreak: Int = 0,
) {
    val overallAccuracyPercent: Int
        get() = if (totalQuestionsAnswered == 0) 0 else ((totalCorrectAnswers * 100f) / totalQuestionsAnswered).toInt()
}

data class QuizPreferences(
    val timerEnabled: Boolean = false,
    val timerSeconds: Int = 25,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val fontScale: FontScaleOption = FontScaleOption.NORMAL,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
)

data class LifelineState(
    val fiftyFiftyLeft: Int = 1,
    val hintLeft: Int = 1,
    val freezeLeft: Int = 1,
    val eliminatedOptionIndices: Set<Int> = emptySet(),
    val activeHintText: String? = null,
    val timerFrozenThisQuestion: Boolean = false,
)

data class SurvivalState(
    val livesRemaining: Int = 3,
    val maxLives: Int = 3,
    val currentStreak: Int = 0,
)

data class LevelThreshold(
    val level: Int,
    val title: String,
    val minXp: Int,
)

data class LevelInfo(
    val level: Int,
    val title: String,
    val totalXp: Int,
    val xpIntoCurrentLevel: Int,
    val xpForNextLevelSpan: Int,
    val nextLevelMinXp: Int?,
    val isMaxLevel: Boolean,
) {
    val progressFraction: Float
        get() = if (isMaxLevel || xpForNextLevelSpan <= 0) {
            1f
        } else {
            (xpIntoCurrentLevel.toFloat() / xpForNextLevelSpan).coerceIn(0f, 1f)
        }
}

object LevelSystem {
    val thresholds: List<LevelThreshold> = listOf(
        LevelThreshold(1, "طالب علم", 0),
        LevelThreshold(2, "قارئ مجتهد", 100),
        LevelThreshold(3, "باحث في المعرفة", 250),
        LevelThreshold(4, "محب للعلم", 450),
        LevelThreshold(5, "متقن في العلوم", 700),
        LevelThreshold(6, "فقيه مجتهد", 1000),
        LevelThreshold(7, "راسخ في المعرفة", 1400),
        LevelThreshold(8, "منارة العلم", 1900),
        LevelThreshold(9, "قدوة المعرفة", 2500),
        LevelThreshold(10, "علّامة زاد المعرفة", 3200),
    )

    fun computeLevelInfo(totalXp: Int): LevelInfo {
        val safeXp = totalXp.coerceAtLeast(0)
        val currentIdx = thresholds.indexOfLast { safeXp >= it.minXp }.coerceAtLeast(0)
        val current = thresholds[currentIdx]
        val next = thresholds.getOrNull(currentIdx + 1)

        return if (next == null) {
            LevelInfo(
                level = current.level,
                title = current.title,
                totalXp = safeXp,
                xpIntoCurrentLevel = safeXp - current.minXp,
                xpForNextLevelSpan = 0,
                nextLevelMinXp = null,
                isMaxLevel = true,
            )
        } else {
            LevelInfo(
                level = current.level,
                title = current.title,
                totalXp = safeXp,
                xpIntoCurrentLevel = safeXp - current.minXp,
                xpForNextLevelSpan = next.minXp - current.minXp,
                nextLevelMinXp = next.minXp,
                isMaxLevel = false,
            )
        }
    }
}

data class BadgeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val targetValue: Int,
)

data class BadgeProgress(
    val definition: BadgeDefinition,
    val isUnlocked: Boolean,
    val currentValue: Int,
) {
    val progressFraction: Float
        get() = if (definition.targetValue <= 0) 0f else (currentValue.toFloat() / definition.targetValue).coerceIn(0f, 1f)
}

object BadgeCatalog {
    const val FIRST_STEP = "first_step"
    const val PERSISTENT_50 = "persistent_50"
    const val SCHOLAR_100 = "scholar_100"
    const val PERFECTIONIST = "perfectionist"
    const val MASTER_QURAN = "master_quran"
    const val MASTER_SEERAH = "master_seerah"
    const val MASTER_FIQH = "master_fiqh"
    const val MASTER_PROPHETS = "master_prophets"
    const val DAILY_CHALLENGER = "daily_challenger"
    const val DAILY_STREAK_3 = "daily_streak_3"
    const val SURVIVAL_10 = "survival_10"

    val all: List<BadgeDefinition> = listOf(
        BadgeDefinition(
            id = FIRST_STEP,
            title = "الخطوة الأولى",
            description = "إتمام أول جولة اختبار بنجاح",
            targetValue = 1,
        ),
        BadgeDefinition(
            id = PERSISTENT_50,
            title = "طالب علم مثابر",
            description = "الإجابة عن 50 سؤالاً إجابة صحيحة",
            targetValue = 50,
        ),
        BadgeDefinition(
            id = SCHOLAR_100,
            title = "المئة المباركة",
            description = "الإجابة عن 100 سؤال إجابة صحيحة",
            targetValue = 100,
        ),
        BadgeDefinition(
            id = PERFECTIONIST,
            title = "الإتقان التام",
            description = "تحقيق نتيجة 100% في جولة من 5 أسئلة فأكثر",
            targetValue = 1,
        ),
        BadgeDefinition(
            id = MASTER_QURAN,
            title = "متقن القرآن الكريم",
            description = "الإجابة عن 10 أسئلة صحيحة في قسم القرآن الكريم",
            targetValue = 10,
        ),
        BadgeDefinition(
            id = MASTER_SEERAH,
            title = "راوي السيرة النبوية",
            description = "الإجابة عن 10 أسئلة صحيحة في قسم السيرة النبوية",
            targetValue = 10,
        ),
        BadgeDefinition(
            id = MASTER_FIQH,
            title = "متقن الفقه والعبادات",
            description = "الإجابة عن 10 أسئلة صحيحة في قسم الفقه والعبادات",
            targetValue = 10,
        ),
        BadgeDefinition(
            id = MASTER_PROPHETS,
            title = "حافظ قصص الأنبياء",
            description = "الإجابة عن 10 أسئلة صحيحة في قسم قصص الأنبياء",
            targetValue = 10,
        ),
        BadgeDefinition(
            id = DAILY_CHALLENGER,
            title = "مداوم على الزاد",
            description = "إتمام قسم الأسئلة اليومية للمرة الأولى",
            targetValue = 1,
        ),
        BadgeDefinition(
            id = DAILY_STREAK_3,
            title = "المواظب المتألق",
            description = "إتمام الأسئلة اليومية لـ 3 أيام متتالية",
            targetValue = 3,
        ),
        BadgeDefinition(
            id = SURVIVAL_10,
            title = "صامد في المعرفة",
            description = "تحقيق سلسلة من 10 إجابات متتالية في تحدي البقاء",
            targetValue = 10,
        ),
    )
}

data class UserProgress(
    val totalXp: Int = 0,
    val unlockedBadgeIds: Set<String> = emptySet(),
    val quranCorrect: Int = 0,
    val seerahCorrect: Int = 0,
    val fiqhCorrect: Int = 0,
    val prophetsCorrect: Int = 0,
    val generalCorrect: Int = 0,
    val quranAnswered: Int = 0,
    val seerahAnswered: Int = 0,
    val fiqhAnswered: Int = 0,
    val prophetsAnswered: Int = 0,
    val generalAnswered: Int = 0,
    val perfectRoundsCount: Int = 0,
    val totalCombinedCorrect: Int = 0,
    val totalCombinedQuizzes: Int = 0,
) {
    val levelInfo: LevelInfo
        get() = LevelSystem.computeLevelInfo(totalXp)

    val categoryPerformances: List<CategoryPerformance>
        get() = listOf(
            CategoryPerformance(QuizCategory.QURAN, quranAnswered, quranCorrect),
            CategoryPerformance(QuizCategory.SEERAH, seerahAnswered, seerahCorrect),
            CategoryPerformance(QuizCategory.FIQH, fiqhAnswered, fiqhCorrect),
            CategoryPerformance(QuizCategory.PROPHETS, prophetsAnswered, prophetsCorrect),
            CategoryPerformance(QuizCategory.GENERAL, generalAnswered, generalCorrect),
        )
}

data class DailyProgress(
    val todayKey: String = "",
    val lastCompletedDate: String = "",
    val todayCorrect: Int = 0,
    val todayTotal: Int = 0,
    val totalCompletedDays: Int = 0,
    val currentStreak: Int = 0,
    val totalDailyAnswered: Int = 0,
    val totalDailyCorrect: Int = 0,
    val bestDailyPercent: Int = 0,
) {
    val isCompletedToday: Boolean
        get() = todayKey.isNotEmpty() && lastCompletedDate == todayKey && todayTotal > 0

    val dailyAccuracyPercent: Int
        get() = if (totalDailyAnswered == 0) 0 else ((totalDailyCorrect * 100f) / totalDailyAnswered).toInt()
}

data class RoundRewards(
    val xpEarned: Int,
    val leveledUpFrom: Int?,
    val newLevelInfo: LevelInfo,
    val newlyUnlockedBadges: List<BadgeDefinition>,
)

enum class AppScreen {
    HOME,
    QUIZ,
    RESULT,
}
