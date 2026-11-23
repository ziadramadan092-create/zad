package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundCue
import com.example.data.DailyQuestionBank
import com.example.data.QuizRepository
import com.example.model.AnsweredRecord
import com.example.model.AppScreen
import com.example.model.BadgeCatalog
import com.example.model.BadgeProgress
import com.example.model.DailyProgress
import com.example.model.DifficultyLevel
import com.example.model.FontScaleOption
import com.example.model.LevelInfo
import com.example.model.LifelineState
import com.example.model.PerformanceBreakdown
import com.example.model.QuestionCountOption
import com.example.model.QuizCategory
import com.example.model.QuizMode
import com.example.model.QuizPreferences
import com.example.model.QuizQuestion
import com.example.model.QuizStats
import com.example.model.RoundRewards
import com.example.model.SurvivalState
import com.example.model.ThemeMode
import com.example.model.UserProgress
import com.example.util.QuizShareAndExportUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = true,
    val currentScreen: AppScreen = AppScreen.HOME,
    val allQuestions: List<QuizQuestion> = emptyList(),
    val stats: QuizStats = QuizStats(),
    val preferences: QuizPreferences = QuizPreferences(),
    val userProgress: UserProgress = UserProgress(),
    val dailyProgress: DailyProgress = DailyProgress(),
    val dailyDateLabel: String = DailyQuestionBank.getCurrentDayInfo().displayDateArabic,
    val favoriteTexts: Set<String> = emptySet(),
    val mistakeTexts: Set<String> = emptySet(),

    // Home setup filters
    val selectedCategory: QuizCategory = QuizCategory.ALL,
    val selectedDifficulty: DifficultyLevel = DifficultyLevel.ALL,
    val selectedCountOption: QuestionCountOption = QuestionCountOption.FIVE,

    // Modals
    val showAddQuestionDialog: Boolean = false,
    val showCustomBankDialog: Boolean = false,
    val showBadgesDialog: Boolean = false,
    val showAnalyticsDialog: Boolean = false,
    val showFavoritesDialog: Boolean = false,
    val showAboutDeveloperDialog: Boolean = false,
    val showResetStatsDialog: Boolean = false,
    val showExitQuizDialog: Boolean = false,
    val toastMessage: String? = null,

    // Active quiz session
    val activeQuizMode: QuizMode = QuizMode.STANDARD,
    val sessionQuestions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerLocked: Boolean = false,
    val isTimedOut: Boolean = false,
    val sessionTimerEnabled: Boolean = false,
    val remainingSeconds: Int = 25,
    val answeredRecords: List<AnsweredRecord> = emptyList(),
    val lastRoundRewards: RoundRewards? = null,
    val lifelineState: LifelineState = LifelineState(),
    val survivalState: SurvivalState = SurvivalState(),
) {
    val levelInfo: LevelInfo
        get() = userProgress.levelInfo

    val badges: List<BadgeProgress>
        get() = BadgeCatalog.all.map { def ->
            val currentVal = when (def.id) {
                BadgeCatalog.FIRST_STEP -> userProgress.totalCombinedQuizzes
                BadgeCatalog.PERSISTENT_50 -> userProgress.totalCombinedCorrect
                BadgeCatalog.SCHOLAR_100 -> userProgress.totalCombinedCorrect
                BadgeCatalog.PERFECTIONIST -> userProgress.perfectRoundsCount
                BadgeCatalog.MASTER_QURAN -> userProgress.quranCorrect
                BadgeCatalog.MASTER_SEERAH -> userProgress.seerahCorrect
                BadgeCatalog.MASTER_FIQH -> userProgress.fiqhCorrect
                BadgeCatalog.MASTER_PROPHETS -> userProgress.prophetsCorrect
                BadgeCatalog.DAILY_CHALLENGER -> dailyProgress.totalCompletedDays
                BadgeCatalog.DAILY_STREAK_3 -> dailyProgress.currentStreak
                BadgeCatalog.SURVIVAL_10 -> stats.bestSurvivalStreak
                else -> 0
            }
            BadgeProgress(
                definition = def,
                isUnlocked = def.id in userProgress.unlockedBadgeIds,
                currentValue = minOf(currentVal, def.targetValue),
            )
        }

    val unlockedBadgesCount: Int
        get() = badges.count { it.isUnlocked }

    val matchingQuestions: List<QuizQuestion>
        get() = allQuestions.filter { q ->
            (selectedCategory == QuizCategory.ALL || q.category == selectedCategory) &&
                (selectedDifficulty == DifficultyLevel.ALL || q.difficulty == selectedDifficulty)
        }

    val effectiveQuestionCount: Int
        get() = minOf(selectedCountOption.count, matchingQuestions.size)

    val customQuestions: List<QuizQuestion>
        get() = allQuestions.filter { it.isCustom }

    val favoriteQuestions: List<QuizQuestion>
        get() {
            val combined = allQuestions + DailyQuestionBank.curatedDailyPool
            return combined
                .distinctBy { it.questionText }
                .filter { it.questionText in favoriteTexts }
        }

    val mistakeQuestions: List<QuizQuestion>
        get() {
            val combined = allQuestions + DailyQuestionBank.curatedDailyPool
            return combined
                .distinctBy { it.questionText }
                .filter { it.questionText in mistakeTexts }
        }

    val currentQuestion: QuizQuestion?
        get() = sessionQuestions.getOrNull(currentQuestionIndex)

    val isCurrentQuestionFavorite: Boolean
        get() = currentQuestion?.questionText in favoriteTexts

    val quizProgressFraction: Float
        get() {
            if (sessionQuestions.isEmpty()) return 0f
            val answeredOrCurrent = if (isAnswerLocked) currentQuestionIndex + 1 else currentQuestionIndex
            return (answeredOrCurrent.toFloat() / sessionQuestions.size).coerceIn(0f, 1f)
        }

    val quizProgressPercent: Int
        get() = (quizProgressFraction * 100).toInt()

    val roundCorrectCount: Int
        get() = answeredRecords.count { it.isCorrect }

    val roundTotalCount: Int
        get() = answeredRecords.size

    val roundMasteryPercent: Int
        get() = if (roundTotalCount == 0) 0 else ((roundCorrectCount * 100f) / roundTotalCount).toInt()

    val categoryBreakdowns: List<PerformanceBreakdown>
        get() = answeredRecords
            .groupBy { it.question.category }
            .map { (cat, list) ->
                PerformanceBreakdown(
                    label = cat.displayName,
                    correct = list.count { it.isCorrect },
                    total = list.size,
                )
            }

    val difficultyBreakdowns: List<PerformanceBreakdown>
        get() = answeredRecords
            .groupBy { it.question.difficulty }
            .map { (diff, list) ->
                PerformanceBreakdown(
                    label = diff.displayName,
                    correct = list.count { it.isCorrect },
                    total = list.size,
                )
            }
}

class QuizViewModel(
    private val repository: QuizRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _soundEvents = MutableSharedFlow<SoundCue>(extraBufferCapacity = 4)
    val soundEvents: SharedFlow<SoundCue> = _soundEvents.asSharedFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureDefaultQuestionsSeeded()
        }

        viewModelScope.launch {
            combine(
                repository.allQuestions,
                repository.stats,
                repository.preferences,
                repository.userProgress,
                repository.dailyProgress,
            ) { questions, stats, prefs, userProgress, dailyProgress ->
                BaseSnapshot(questions, stats, prefs, userProgress, dailyProgress)
            }.combine(
                combine(repository.favoriteTexts, repository.mistakeTexts) { favs, mistakes -> favs to mistakes },
            ) { base, (favs, mistakes) ->
                val dayInfo = DailyQuestionBank.getCurrentDayInfo()
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        allQuestions = base.questions,
                        stats = base.stats,
                        preferences = base.preferences,
                        userProgress = base.userProgress,
                        dailyProgress = base.dailyProgress,
                        dailyDateLabel = dayInfo.displayDateArabic,
                        favoriteTexts = favs,
                        mistakeTexts = mistakes,
                    )
                }
            }.collect {}
        }
    }

    private data class BaseSnapshot(
        val questions: List<QuizQuestion>,
        val stats: QuizStats,
        val preferences: QuizPreferences,
        val userProgress: UserProgress,
        val dailyProgress: DailyProgress,
    )

    fun selectCategory(category: QuizCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectDifficulty(difficulty: DifficultyLevel) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun selectCountOption(option: QuestionCountOption) {
        _uiState.update { it.copy(selectedCountOption = option) }
    }

    fun updateTimerSetting(enabled: Boolean, seconds: Int = _uiState.value.preferences.timerSeconds) {
        viewModelScope.launch {
            repository.updateTimerSettings(enabled, seconds)
        }
    }

    fun cycleThemeMode() {
        val next = when (_uiState.value.preferences.themeMode) {
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
        }
        viewModelScope.launch {
            repository.updateThemeMode(next)
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSoundEnabled(enabled)
        }
    }

    fun toggleSoundEnabled() {
        updateSoundEnabled(!_uiState.value.preferences.soundEnabled)
    }

    fun updateHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateHapticsEnabled(enabled)
        }
    }

    fun updateFontScale(option: FontScaleOption) {
        viewModelScope.launch {
            repository.updateFontScale(option)
        }
    }

    fun updateReminderSettings(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.updateReminderSettings(enabled, hour, minute)
        }
    }

    fun toggleFavorite(questionText: String) {
        viewModelScope.launch {
            val nowFav = repository.toggleFavorite(questionText)
            _uiState.update {
                it.copy(toastMessage = if (nowFav) "تمت إضافة السؤال إلى المفضلة" else "تمت إزالة السؤال من المفضلة")
            }
        }
    }

    fun startQuiz() {
        val state = _uiState.value
        val pool = state.matchingQuestions
        if (pool.isEmpty()) {
            _uiState.update {
                it.copy(toastMessage = "لا توجد أسئلة مطابقة للتصفية الحالية، جرّب تغيير القسم أو الصعوبة.")
            }
            return
        }

        val targetCount = minOf(state.selectedCountOption.count, pool.size)
        val selectedQuestions = pool.shuffled().take(targetCount)
        launchSession(mode = QuizMode.STANDARD, questions = selectedQuestions)
    }

    fun startDailyQuiz() {
        val (dayInfo, dailyQuestions) = repository.getTodayDailyQuestions()
        _uiState.update { it.copy(dailyDateLabel = dayInfo.displayDateArabic) }
        launchSession(mode = QuizMode.DAILY, questions = dailyQuestions)
    }

    fun startSurvivalQuiz() {
        val state = _uiState.value
        val pool = state.matchingQuestions.ifEmpty { state.allQuestions }
        if (pool.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "لا توجد أسئلة متاحة حالياً.") }
            return
        }
        launchSession(mode = QuizMode.SURVIVAL, questions = pool.shuffled())
    }

    fun startFavoritesQuiz() {
        val favs = _uiState.value.favoriteQuestions
        if (favs.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "قائمة المفضلة فارغة حالياً.") }
            return
        }
        launchSession(mode = QuizMode.FAVORITES, questions = favs.shuffled())
    }

    fun startMistakesQuiz() {
        val mistakes = _uiState.value.mistakeQuestions
        if (mistakes.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "رائع! لا توجد أخطاء مسجلة للمراجعة حالياً.") }
            return
        }
        launchSession(mode = QuizMode.MISTAKES, questions = mistakes.shuffled())
    }

    private fun launchSession(
        mode: QuizMode,
        questions: List<QuizQuestion>,
    ) {
        val state = _uiState.value
        val timerEnabled = state.preferences.timerEnabled
        val seconds = state.preferences.timerSeconds
        val lifelineCharges = if (mode == QuizMode.SURVIVAL) 2 else 1

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.QUIZ,
                activeQuizMode = mode,
                sessionQuestions = questions,
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                isAnswerLocked = false,
                isTimedOut = false,
                sessionTimerEnabled = timerEnabled,
                remainingSeconds = seconds,
                answeredRecords = emptyList(),
                lastRoundRewards = null,
                showExitQuizDialog = false,
                lifelineState = LifelineState(
                    fiftyFiftyLeft = lifelineCharges,
                    hintLeft = lifelineCharges,
                    freezeLeft = lifelineCharges,
                ),
                survivalState = SurvivalState(livesRemaining = 3, maxLives = 3, currentStreak = 0),
            )
        }

        if (timerEnabled) {
            startQuestionTimer(seconds)
        } else {
            stopTimer()
        }
    }

    fun restartSameSettings() {
        when (_uiState.value.activeQuizMode) {
            QuizMode.DAILY -> startDailyQuiz()
            QuizMode.SURVIVAL -> startSurvivalQuiz()
            QuizMode.FAVORITES -> startFavoritesQuiz()
            QuizMode.MISTAKES -> startMistakesQuiz()
            QuizMode.STANDARD -> startQuiz()
        }
    }

    fun useFiftyFifty() {
        val state = _uiState.value
        val q = state.currentQuestion ?: return
        if (state.isAnswerLocked || state.lifelineState.fiftyFiftyLeft <= 0 || state.lifelineState.eliminatedOptionIndices.isNotEmpty()) {
            return
        }
        val wrongIndices = (0..3).filter { it != q.correctIndex }.shuffled().take(2).toSet()
        _uiState.update {
            it.copy(
                lifelineState = it.lifelineState.copy(
                    fiftyFiftyLeft = it.lifelineState.fiftyFiftyLeft - 1,
                    eliminatedOptionIndices = wrongIndices,
                ),
            )
        }
    }

    fun useHint() {
        val state = _uiState.value
        val q = state.currentQuestion ?: return
        if (state.isAnswerLocked || state.lifelineState.hintLeft <= 0 || state.lifelineState.activeHintText != null) {
            return
        }
        val raw = q.explanation.trim()
        val hintText = if (raw.length > 72) raw.take(72).trimEnd() + "..." else raw
        _uiState.update {
            it.copy(
                lifelineState = it.lifelineState.copy(
                    hintLeft = it.lifelineState.hintLeft - 1,
                    activeHintText = hintText,
                ),
            )
        }
    }

    fun useFreezeTimer() {
        val state = _uiState.value
        if (state.isAnswerLocked || !state.sessionTimerEnabled || state.lifelineState.freezeLeft <= 0 || state.lifelineState.timerFrozenThisQuestion) {
            return
        }
        stopTimer()
        _uiState.update {
            it.copy(
                lifelineState = it.lifelineState.copy(
                    freezeLeft = it.lifelineState.freezeLeft - 1,
                    timerFrozenThisQuestion = true,
                ),
            )
        }
    }

    fun selectOption(optionIndex: Int) {
        val state = _uiState.value
        if (state.currentScreen != AppScreen.QUIZ || state.isAnswerLocked) return
        if (optionIndex in state.lifelineState.eliminatedOptionIndices) return
        val question = state.currentQuestion ?: return

        stopTimer()
        val isCorrect = optionIndex == question.correctIndex
        val record = AnsweredRecord(
            question = question,
            selectedIndex = optionIndex,
            isTimedOut = false,
            isCorrect = isCorrect,
        )

        val updatedSurvival = if (state.activeQuizMode == QuizMode.SURVIVAL) {
            if (isCorrect) {
                state.survivalState.copy(currentStreak = state.survivalState.currentStreak + 1)
            } else {
                state.survivalState.copy(
                    livesRemaining = (state.survivalState.livesRemaining - 1).coerceAtLeast(0),
                )
            }
        } else {
            state.survivalState
        }

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerLocked = true,
                isTimedOut = false,
                answeredRecords = it.answeredRecords + record,
                survivalState = updatedSurvival,
            )
        }

        if (state.preferences.soundEnabled) {
            _soundEvents.tryEmit(if (isCorrect) SoundCue.CORRECT else SoundCue.WRONG)
        }
    }

    fun toggleSessionTimer() {
        val state = _uiState.value
        if (state.currentScreen != AppScreen.QUIZ) return

        val nextEnabled = !state.sessionTimerEnabled
        val duration = state.preferences.timerSeconds
        _uiState.update {
            it.copy(
                sessionTimerEnabled = nextEnabled,
                remainingSeconds = if (nextEnabled && !it.isAnswerLocked) duration else it.remainingSeconds,
            )
        }

        if (nextEnabled && !state.isAnswerLocked && !state.lifelineState.timerFrozenThisQuestion) {
            startQuestionTimer(duration)
        } else {
            stopTimer()
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.currentScreen != AppScreen.QUIZ || !state.isAnswerLocked) return

        val isSurvivalEnded = state.activeQuizMode == QuizMode.SURVIVAL && state.survivalState.livesRemaining <= 0
        val nextIdx = state.currentQuestionIndex + 1

        if (isSurvivalEnded || nextIdx >= state.sessionQuestions.size) {
            finishQuiz()
        } else {
            val seconds = state.preferences.timerSeconds
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIdx,
                    selectedOptionIndex = null,
                    isAnswerLocked = false,
                    isTimedOut = false,
                    remainingSeconds = seconds,
                    lifelineState = it.lifelineState.copy(
                        eliminatedOptionIndices = emptySet(),
                        activeHintText = null,
                        timerFrozenThisQuestion = false,
                    ),
                )
            }
            if (state.sessionTimerEnabled) {
                startQuestionTimer(seconds)
            }
        }
    }

    private fun finishQuiz() {
        stopTimer()
        val state = _uiState.value
        val mode = state.activeQuizMode
        val records = state.answeredRecords
        val streakAchieved = state.survivalState.currentStreak

        viewModelScope.launch {
            val rewards = repository.recordRoundCompletion(
                mode = mode,
                records = records,
                survivalStreakAchieved = streakAchieved,
            )
            if (state.preferences.soundEnabled) {
                _soundEvents.tryEmit(SoundCue.COMPLETE)
            }
            _uiState.update {
                it.copy(
                    currentScreen = AppScreen.RESULT,
                    lastRoundRewards = rewards,
                )
            }
        }
    }

    private fun startQuestionTimer(seconds: Int) {
        stopTimer()
        timerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                _uiState.update { it.copy(remainingSeconds = remaining) }
                delay(1000L)
                remaining--
            }
            handleTimeout()
        }
    }

    private fun handleTimeout() {
        val state = _uiState.value
        if (state.currentScreen != AppScreen.QUIZ || state.isAnswerLocked) return
        val question = state.currentQuestion ?: return

        val record = AnsweredRecord(
            question = question,
            selectedIndex = null,
            isTimedOut = true,
            isCorrect = false,
        )

        val updatedSurvival = if (state.activeQuizMode == QuizMode.SURVIVAL) {
            state.survivalState.copy(
                livesRemaining = (state.survivalState.livesRemaining - 1).coerceAtLeast(0),
            )
        } else {
            state.survivalState
        }

        _uiState.update {
            it.copy(
                remainingSeconds = 0,
                selectedOptionIndex = null,
                isAnswerLocked = true,
                isTimedOut = true,
                answeredRecords = it.answeredRecords + record,
                survivalState = updatedSurvival,
            )
        }

        if (state.preferences.soundEnabled) {
            _soundEvents.tryEmit(SoundCue.WRONG)
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun requestExitQuiz() {
        _uiState.update { it.copy(showExitQuizDialog = true) }
    }

    fun dismissExitQuiz() {
        _uiState.update { it.copy(showExitQuizDialog = false) }
    }

    fun confirmExitQuiz() {
        stopTimer()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.HOME,
                showExitQuizDialog = false,
                sessionQuestions = emptyList(),
                answeredRecords = emptyList(),
                lastRoundRewards = null,
            )
        }
    }

    fun goHome() {
        stopTimer()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.HOME,
                sessionQuestions = emptyList(),
                answeredRecords = emptyList(),
                lastRoundRewards = null,
            )
        }
    }

    fun openAddQuestionDialog() { _uiState.update { it.copy(showAddQuestionDialog = true) } }
    fun dismissAddQuestionDialog() { _uiState.update { it.copy(showAddQuestionDialog = false) } }

    fun openCustomBankDialog() { _uiState.update { it.copy(showCustomBankDialog = true) } }
    fun dismissCustomBankDialog() { _uiState.update { it.copy(showCustomBankDialog = false) } }

    fun openBadgesDialog() { _uiState.update { it.copy(showBadgesDialog = true) } }
    fun dismissBadgesDialog() { _uiState.update { it.copy(showBadgesDialog = false) } }

    fun openAnalyticsDialog() { _uiState.update { it.copy(showAnalyticsDialog = true) } }
    fun dismissAnalyticsDialog() { _uiState.update { it.copy(showAnalyticsDialog = false) } }

    fun openFavoritesDialog() { _uiState.update { it.copy(showFavoritesDialog = true) } }
    fun dismissFavoritesDialog() { _uiState.update { it.copy(showFavoritesDialog = false) } }

    fun openAboutDeveloperDialog() { _uiState.update { it.copy(showAboutDeveloperDialog = true) } }
    fun dismissAboutDeveloperDialog() { _uiState.update { it.copy(showAboutDeveloperDialog = false) } }

    fun addCustomQuestion(
        category: QuizCategory,
        difficulty: DifficultyLevel,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
    ) {
        viewModelScope.launch {
            repository.addCustomQuestion(
                category = category,
                difficulty = difficulty,
                questionText = questionText,
                options = options,
                correctIndex = correctIndex,
                explanation = explanation,
            )
            _uiState.update {
                it.copy(
                    showAddQuestionDialog = false,
                    toastMessage = "تم حفظ السؤال في بنك الأسئلة المحلي بنجاح",
                )
            }
        }
    }

    fun importQuestionsFromJson(jsonString: String) {
        viewModelScope.launch {
            runCatching {
                val parsed = QuizShareAndExportUtils.parseQuestionsFromJson(jsonString)
                if (parsed.isEmpty()) {
                    _uiState.update { it.copy(toastMessage = "لم يتم العثور على أسئلة صالحة داخل الملف") }
                } else {
                    val inserted = repository.importCustomQuestions(parsed)
                    _uiState.update {
                        it.copy(
                            toastMessage = if (inserted > 0) {
                                "تم استيراد $inserted سؤالاً جديداً بنجاح"
                            } else {
                                "جميع الأسئلة في الملف موجودة مسبقاً"
                            },
                        )
                    }
                }
            }.onFailure {
                _uiState.update { it.copy(toastMessage = "صيغة ملف JSON غير صالحة") }
            }
        }
    }

    fun showMessage(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun deleteCustomQuestion(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomQuestion(id)
            _uiState.update { it.copy(toastMessage = "تم حذف السؤال المخصص") }
        }
    }

    fun requestResetStats() { _uiState.update { it.copy(showResetStatsDialog = true) } }
    fun dismissResetStats() { _uiState.update { it.copy(showResetStatsDialog = false) } }

    fun confirmResetStats() {
        viewModelScope.launch {
            repository.resetStats()
            _uiState.update {
                it.copy(
                    showResetStatsDialog = false,
                    toastMessage = "تم تصفير الإحصائيات والمستوى والأوسمة بنجاح",
                )
            }
        }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    override fun onCleared() {
        stopTimer()
        super.onCleared()
    }

    class Factory(
        private val repository: QuizRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                return QuizViewModel(repository = repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
