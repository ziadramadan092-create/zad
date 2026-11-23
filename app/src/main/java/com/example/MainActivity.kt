package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.QuizSoundPlayer
import com.example.data.QuizRepository
import com.example.data.local.AppDatabase
import com.example.data.local.StatsDataStore
import com.example.model.AppScreen
import com.example.ui.QuizViewModel
import com.example.ui.components.AboutDeveloperDialog
import com.example.ui.components.AddQuestionDialog
import com.example.ui.components.AnalyticsDialog
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.CustomBankDialog
import com.example.ui.components.FavoritesDialog
import com.example.ui.components.LevelAndBadgesDialog
import com.example.ui.screens.HomeSetupScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.theme.ZadAlMarifahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val statsDataStore = StatsDataStore(applicationContext)
        val repository = QuizRepository(
            questionDao = database.questionDao(),
            statsDataStore = statsDataStore,
        )

        setContent {
            val viewModel: QuizViewModel = viewModel(
                factory = QuizViewModel.Factory(repository),
            )
            ZadAlMarifahApp(viewModel = viewModel)
        }
    }
}

@Composable
fun ZadAlMarifahApp(viewModel: QuizViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val soundPlayer = remember(context) { QuizSoundPlayer(context) }

    DisposableEffect(soundPlayer) {
        onDispose { soundPlayer.release() }
    }

    LaunchedEffect(viewModel) {
        viewModel.soundEvents.collect { cue ->
            soundPlayer.play(cue)
        }
    }

    LaunchedEffect(state.toastMessage) {
        val msg = state.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ZadAlMarifahTheme(themeMode = state.preferences.themeMode) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && state.allQuestions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Crossfade(
                        targetState = state.currentScreen,
                        animationSpec = tween(durationMillis = 240),
                        label = "screen_crossfade",
                    ) { screen ->
                        when (screen) {
                            AppScreen.HOME -> HomeSetupScreen(
                                state = state,
                                onSelectCategory = viewModel::selectCategory,
                                onSelectDifficulty = viewModel::selectDifficulty,
                                onSelectCountOption = viewModel::selectCountOption,
                                onToggleTimer = viewModel::updateTimerSetting,
                                onToggleSound = viewModel::updateSoundEnabled,
                                onToggleHaptics = viewModel::updateHapticsEnabled,
                                onSelectFontScale = viewModel::updateFontScale,
                                onUpdateReminder = viewModel::updateReminderSettings,
                                onShowMessage = viewModel::showMessage,
                                onCycleTheme = viewModel::cycleThemeMode,
                                onStartQuiz = viewModel::startQuiz,
                                onStartDailyQuiz = viewModel::startDailyQuiz,
                                onStartSurvivalQuiz = viewModel::startSurvivalQuiz,
                                onStartMistakesQuiz = viewModel::startMistakesQuiz,
                                onOpenAddQuestion = viewModel::openAddQuestionDialog,
                                onOpenCustomBank = viewModel::openCustomBankDialog,
                                onOpenBadges = viewModel::openBadgesDialog,
                                onOpenAnalytics = viewModel::openAnalyticsDialog,
                                onOpenFavorites = viewModel::openFavoritesDialog,
                                onOpenAboutDeveloper = viewModel::openAboutDeveloperDialog,
                                onRequestResetStats = viewModel::requestResetStats,
                            )

                            AppScreen.QUIZ -> QuizScreen(
                                state = state,
                                onSelectOption = viewModel::selectOption,
                                onNextQuestion = viewModel::nextQuestion,
                                onToggleTimer = viewModel::toggleSessionTimer,
                                onToggleSound = viewModel::toggleSoundEnabled,
                                onToggleFavorite = viewModel::toggleFavorite,
                                onUseFiftyFifty = viewModel::useFiftyFifty,
                                onUseHint = viewModel::useHint,
                                onUseFreezeTimer = viewModel::useFreezeTimer,
                                onRequestExit = viewModel::requestExitQuiz,
                            )

                            AppScreen.RESULT -> ResultScreen(
                                state = state,
                                onRestartSameSettings = viewModel::restartSameSettings,
                                onChooseNewCategory = viewModel::goHome,
                                onOpenBadges = viewModel::openBadgesDialog,
                                onToggleFavorite = viewModel::toggleFavorite,
                                onRequestResetStats = viewModel::requestResetStats,
                            )
                        }
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(16.dp),
                ) { data ->
                    Snackbar(snackbarData = data)
                }

                if (state.showAddQuestionDialog) {
                    AddQuestionDialog(
                        onDismiss = viewModel::dismissAddQuestionDialog,
                        onSave = viewModel::addCustomQuestion,
                    )
                }

                if (state.showCustomBankDialog) {
                    CustomBankDialog(
                        customQuestions = state.customQuestions,
                        onDismiss = viewModel::dismissCustomBankDialog,
                        onAddNew = viewModel::openAddQuestionDialog,
                        onDeleteQuestion = viewModel::deleteCustomQuestion,
                        onImportJson = viewModel::importQuestionsFromJson,
                        onExportSuccess = {
                            viewModel.showMessage("تم تصدير الأسئلة المخصصة بنجاح")
                        },
                        onExportError = { msg ->
                            viewModel.showMessage(msg)
                        },
                    )
                }

                if (state.showBadgesDialog) {
                    LevelAndBadgesDialog(
                        levelInfo = state.levelInfo,
                        badges = state.badges,
                        onDismiss = viewModel::dismissBadgesDialog,
                    )
                }

                if (state.showAnalyticsDialog) {
                    AnalyticsDialog(
                        stats = state.stats,
                        userProgress = state.userProgress,
                        dailyProgress = state.dailyProgress,
                        onDismiss = viewModel::dismissAnalyticsDialog,
                    )
                }

                if (state.showFavoritesDialog) {
                    FavoritesDialog(
                        favoriteQuestions = state.favoriteQuestions,
                        onDismiss = viewModel::dismissFavoritesDialog,
                        onRemoveFavorite = viewModel::toggleFavorite,
                        onStartFavoritesQuiz = viewModel::startFavoritesQuiz,
                    )
                }

                if (state.showAboutDeveloperDialog) {
                    AboutDeveloperDialog(
                        totalQuestionCount = state.allQuestions.size,
                        onEmailCopied = {
                            viewModel.showMessage("تم نسخ البريد الإلكتروني للمطور")
                        },
                        onDismiss = viewModel::dismissAboutDeveloperDialog,
                    )
                }

                if (state.showResetStatsDialog) {
                    ConfirmationDialog(
                        title = "تصفير الإحصائيات",
                        message = "هل تريد بالتأكيد مسح أعلى نتيجة وإحصائيات الأسئلة اليومية والمستوى والأوسمة؟ لن يتم حذف أسئلتك المضافة.",
                        confirmText = "نعم، تصفير",
                        dismissText = "إلغاء",
                        isDestructive = true,
                        onConfirm = viewModel::confirmResetStats,
                        onDismiss = viewModel::dismissResetStats,
                    )
                }

                if (state.showExitQuizDialog) {
                    ConfirmationDialog(
                        title = "إنهاء الجولة الحالية",
                        message = "هل تريد الخروج من الاختبار الحالي والعودة إلى شاشة البداية؟",
                        confirmText = "خروج",
                        dismissText = "متابعة الاختبار",
                        isDestructive = true,
                        onConfirm = viewModel::confirmExitQuiz,
                        onDismiss = viewModel::dismissExitQuiz,
                    )
                }
            }
        }
    }
}
