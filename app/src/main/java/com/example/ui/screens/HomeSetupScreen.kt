package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.DailyQuestionBank
import com.example.model.DifficultyLevel
import com.example.model.FontScaleOption
import com.example.model.QuestionCountOption
import com.example.model.QuizCategory
import com.example.ui.QuizUiState
import com.example.ui.theme.LocalZadColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeSetupScreen(
    state: QuizUiState,
    onSelectCategory: (QuizCategory) -> Unit,
    onSelectDifficulty: (DifficultyLevel) -> Unit,
    onSelectCountOption: (QuestionCountOption) -> Unit,
    onToggleTimer: (Boolean, Int) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onSelectFontScale: (FontScaleOption) -> Unit,
    onUpdateReminder: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
    onShowMessage: (String) -> Unit,
    onCycleTheme: () -> Unit,
    onStartQuiz: () -> Unit,
    onStartDailyQuiz: () -> Unit,
    onStartSurvivalQuiz: () -> Unit,
    onStartMistakesQuiz: () -> Unit,
    onOpenAddQuestion: () -> Unit,
    onOpenCustomBank: () -> Unit,
    onOpenBadges: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenAboutDeveloper: () -> Unit,
    onRequestResetStats: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val isWideScreen = maxWidth >= 720.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 860.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    HeroBannerCard(
                        themeLabel = state.preferences.themeMode.displayName,
                        totalBankCount = state.allQuestions.size,
                        customCount = state.customQuestions.size,
                        onCycleTheme = onCycleTheme,
                        onOpenCustomBank = onOpenCustomBank,
                    )

                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                LevelAndBadgesCard(
                                    state = state,
                                    onOpenBadges = onOpenBadges,
                                )
                                DailyQuestionsCard(
                                    state = state,
                                    onStartDailyQuiz = onStartDailyQuiz,
                                )
                                SurvivalAndSmartReviewCard(
                                    bestSurvivalStreak = state.stats.bestSurvivalStreak,
                                    favoriteCount = state.favoriteQuestions.size,
                                    mistakeCount = state.mistakeQuestions.size,
                                    onStartSurvival = onStartSurvivalQuiz,
                                    onOpenFavorites = onOpenFavorites,
                                    onStartMistakes = onStartMistakesQuiz,
                                )
                                StatsSummaryCard(
                                    state = state,
                                    onOpenAnalytics = onOpenAnalytics,
                                    onRequestResetStats = onRequestResetStats,
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1.1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                CategorySelectorCard(
                                    state = state,
                                    isWide = true,
                                    onSelectCategory = onSelectCategory,
                                )
                                QuizSettingsCard(
                                    state = state,
                                    onSelectDifficulty = onSelectDifficulty,
                                    onSelectCountOption = onSelectCountOption,
                                    onToggleTimer = onToggleTimer,
                                    onToggleSound = onToggleSound,
                                    onToggleHaptics = onToggleHaptics,
                                    onSelectFontScale = onSelectFontScale,
                                    onUpdateReminder = onUpdateReminder,
                                    onShowMessage = onShowMessage,
                                )
                                ActionButtonsRow(
                                    state = state,
                                    onStartQuiz = onStartQuiz,
                                    onOpenAddQuestion = onOpenAddQuestion,
                                )
                            }
                        }
                    } else {
                        LevelAndBadgesCard(
                            state = state,
                            onOpenBadges = onOpenBadges,
                        )
                        DailyQuestionsCard(
                            state = state,
                            onStartDailyQuiz = onStartDailyQuiz,
                        )
                        SurvivalAndSmartReviewCard(
                            bestSurvivalStreak = state.stats.bestSurvivalStreak,
                            favoriteCount = state.favoriteQuestions.size,
                            mistakeCount = state.mistakeQuestions.size,
                            onStartSurvival = onStartSurvivalQuiz,
                            onOpenFavorites = onOpenFavorites,
                            onStartMistakes = onStartMistakesQuiz,
                        )
                        StatsSummaryCard(
                            state = state,
                            onOpenAnalytics = onOpenAnalytics,
                            onRequestResetStats = onRequestResetStats,
                        )
                        CategorySelectorCard(
                            state = state,
                            isWide = false,
                            onSelectCategory = onSelectCategory,
                        )
                        QuizSettingsCard(
                            state = state,
                            onSelectDifficulty = onSelectDifficulty,
                            onSelectCountOption = onSelectCountOption,
                            onToggleTimer = onToggleTimer,
                            onToggleSound = onToggleSound,
                            onToggleHaptics = onToggleHaptics,
                            onSelectFontScale = onSelectFontScale,
                            onUpdateReminder = onUpdateReminder,
                            onShowMessage = onShowMessage,
                        )
                        ActionButtonsRow(
                            state = state,
                            onStartQuiz = onStartQuiz,
                            onOpenAddQuestion = onOpenAddQuestion,
                        )
                    }

                    DeveloperCreditFooter(onClick = onOpenAboutDeveloper)

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCard(
    themeLabel: String,
    totalBankCount: Int,
    customCount: Int,
    onCycleTheme: () -> Unit,
    onOpenCustomBank: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner_1795354701660),
                contentDescription = "خلفية زخرفية إسلامية لتطبيق زاد المعرفة",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xD907342B),
                                Color(0xB80A4136),
                                Color(0x880D4A3E),
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        onClick = onOpenCustomBank,
                        color = Color(0x33FAF6EE),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("open_custom_bank_pill"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                contentDescription = null,
                                tint = Color(0xFFF6D68A),
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بنك الأسئلة: $totalBankCount" + if (customCount > 0) " ($customCount مضاف)" else "",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFFAF6EE),
                            )
                        }
                    }

                    Surface(
                        onClick = onCycleTheme,
                        color = Color(0x33FAF6EE),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("theme_toggle_button"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Brightness6,
                                contentDescription = "تبديل المظهر",
                                tint = Color(0xFFFAF6EE),
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = themeLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFFAF6EE),
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "زاد المعرفة",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color(0xFFFAF6EE),
                    )
                    Text(
                        text = "اختبارات إسلامية تفاعلية بشروحات موثقة • يعمل دون اتصال بالإنترنت",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE7DFCF),
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelAndBadgesCard(
    state: QuizUiState,
    onOpenBadges: () -> Unit,
) {
    val levelInfo = state.levelInfo
    val zadColors = LocalZadColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onOpenBadges)
            .testTag("level_and_badges_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, zadColors.goldBorder.copy(alpha = 0.85f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${levelInfo.level}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }
                    Column {
                        Text(
                            text = "المستوى ${levelInfo.level} • ${levelInfo.title}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "${levelInfo.totalXp} نقطة خبرة (XP)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = zadColors.goldSurface,
                    border = BorderStroke(1.dp, zadColors.goldBorder),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WorkspacePremium,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.unlockedBadgesCount}/${state.badges.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = "عرض الأوسمة",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { levelInfo.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (levelInfo.isMaxLevel) {
                        "أعلى مستوى في التطبيق"
                    } else {
                        "المستوى التالي عند ${levelInfo.nextLevelMinXp} XP"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "اضغط لعرض الأوسمة والمستويات",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun DailyQuestionsCard(
    state: QuizUiState,
    onStartDailyQuiz: () -> Unit,
) {
    val daily = state.dailyProgress
    val zadColors = LocalZadColors.current
    val completedToday = daily.isCompletedToday

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_questions_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (completedToday) {
                zadColors.correctContainer.copy(alpha = 0.55f)
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            },
        ),
        border = BorderStroke(
            1.2.dp,
            if (completedToday) zadColors.correct else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (completedToday) zadColors.correct else MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (completedToday) Icons.Filled.CheckCircle else Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column {
                        Text(
                            text = "الأسئلة اليومية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = state.dailyDateLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (daily.currentStreak > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = zadColors.goldSurface,
                        border = BorderStroke(1.dp, zadColors.goldBorder),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${daily.currentStreak} أيام",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Text(
                text = if (completedToday) {
                    "أتممت أسئلة اليوم بنجاح (${daily.todayCorrect} من ${daily.todayTotal})! تتجدد المجموعة تلقائياً غداً."
                } else {
                    "مجموعة يومية منتقاة من ${DailyQuestionBank.DAILY_QUESTION_COUNT} أسئلة متجددة كل يوم مع مكافأة +40 XP إضافية."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MiniDailyStatChip(
                    modifier = Modifier.weight(1f),
                    label = "أيام مكتملة",
                    value = "${daily.totalCompletedDays}",
                )
                MiniDailyStatChip(
                    modifier = Modifier.weight(1f),
                    label = "مجاب يومياً",
                    value = "${daily.totalDailyAnswered}",
                )
                MiniDailyStatChip(
                    modifier = Modifier.weight(1f),
                    label = "دقة اليومي",
                    value = if (daily.totalDailyAnswered > 0) "${daily.dailyAccuracyPercent}%" else "—",
                )
            }

            Button(
                onClick = onStartDailyQuiz,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (completedToday) zadColors.correct else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_daily_quiz_button"),
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (completedToday) "مراجعة أسئلة اليوم (${DailyQuestionBank.DAILY_QUESTION_COUNT} أسئلة)" else "ابدأ أسئلة اليوم (${DailyQuestionBank.DAILY_QUESTION_COUNT} أسئلة)",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun MiniDailyStatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun StatsSummaryCard(
    state: QuizUiState,
    onOpenAnalytics: () -> Unit,
    onRequestResetStats: () -> Unit,
) {
    val stats = state.stats
    val zadColors = LocalZadColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إحصائيات الجولات العامة",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                if (stats.totalQuestionsAnswered > 0 || state.userProgress.totalXp > 0) {
                    TextButton(
                        onClick = onRequestResetStats,
                        modifier = Modifier.testTag("home_reset_stats_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصفير")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatPillBox(
                    modifier = Modifier.weight(1f),
                    title = "أعلى نتيجة مسجلة",
                    value = if (stats.highestScoreTotal > 0) {
                        "${stats.highestScorePercent}%"
                    } else {
                        "—"
                    },
                    subtitle = if (stats.highestScoreTotal > 0) {
                        "${stats.highestScoreCorrect} من ${stats.highestScoreTotal}"
                    } else {
                        "ابدأ أول جولة"
                    },
                    containerColor = zadColors.goldSurface,
                    accentColor = MaterialTheme.colorScheme.secondary,
                )

                StatPillBox(
                    modifier = Modifier.weight(1f),
                    title = "الأسئلة المجابة",
                    value = "${stats.totalQuestionsAnswered}",
                    subtitle = if (stats.completedQuizzes > 0) {
                        "${stats.completedQuizzes} جولات مكتملة"
                    } else {
                        "محفوظة محلياً"
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    accentColor = MaterialTheme.colorScheme.primary,
                )

                StatPillBox(
                    modifier = Modifier.weight(1f),
                    title = "نسبة الإتقان",
                    value = if (stats.totalQuestionsAnswered > 0) "${stats.overallAccuracyPercent}%" else "—",
                    subtitle = if (stats.totalQuestionsAnswered > 0) {
                        "${stats.totalCorrectAnswers} صحيحة"
                    } else {
                        "لا توجد بيانات"
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    accentColor = MaterialTheme.colorScheme.tertiary,
                )
            }

            OutlinedButton(
                onClick = onOpenAnalytics,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("open_analytics_button"),
            ) {
                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تحليل الأداء والرسوم البيانية",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun StatPillBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    containerColor: Color,
    accentColor: Color,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CategorySelectorCard(
    state: QuizUiState,
    isWide: Boolean,
    onSelectCategory: (QuizCategory) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "اختر القسم",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            val categories = QuizCategory.entries
            val columns = if (isWide) 2 else 2
            categories.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rowItems.forEach { category ->
                        val count = if (category == QuizCategory.ALL) {
                            state.allQuestions.size
                        } else {
                            state.allQuestions.count { it.category == category }
                        }
                        val selected = state.selectedCategory == category
                        CategoryChoiceTile(
                            modifier = Modifier.weight(1f),
                            category = category,
                            count = count,
                            selected = selected,
                            onClick = { onSelectCategory(category) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChoiceTile(
    modifier: Modifier = Modifier,
    category: QuizCategory,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("category_tile_${category.key}"),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = categoryIcon(category),
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$count سؤال",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun categoryIcon(category: QuizCategory): ImageVector = when (category) {
    QuizCategory.ALL -> Icons.Filled.AutoAwesome
    QuizCategory.QURAN -> Icons.AutoMirrored.Filled.MenuBook
    QuizCategory.SEERAH -> Icons.Filled.HistoryEdu
    QuizCategory.FIQH -> Icons.Filled.Balance
    QuizCategory.PROPHETS -> Icons.Filled.QuestionAnswer
    QuizCategory.GENERAL -> Icons.Filled.Public
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuizSettingsCard(
    state: QuizUiState,
    onSelectDifficulty: (DifficultyLevel) -> Unit,
    onSelectCountOption: (QuestionCountOption) -> Unit,
    onToggleTimer: (Boolean, Int) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onSelectFontScale: (FontScaleOption) -> Unit,
    onUpdateReminder: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
    onShowMessage: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "إعدادات الجولة",
                style = MaterialTheme.typography.titleMedium,
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "مستوى الصعوبة",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    DifficultyLevel.entries.forEach { level ->
                        FilterChip(
                            selected = state.selectedDifficulty == level,
                            onClick = { onSelectDifficulty(level) },
                            label = { Text(level.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            modifier = Modifier.testTag("difficulty_chip_${level.key}"),
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "عدد الأسئلة",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "المتاح: ${state.matchingQuestions.size} سؤال",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    QuestionCountOption.entries.forEach { option ->
                        val label = if (option == QuestionCountOption.ALL) {
                            "كل القسم (${state.matchingQuestions.size})"
                        } else {
                            option.displayName
                        }
                        FilterChip(
                            selected = state.selectedCountOption == option,
                            onClick = { onSelectCountOption(option) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                            modifier = Modifier.testTag("count_chip_${option.name.lowercase()}"),
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "المؤقت الزمني لكل سؤال",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    text = if (state.preferences.timerEnabled) {
                                        "مفعّل (${state.preferences.timerSeconds} ثانية لكل سؤال)"
                                    } else {
                                        "مغلق — أجب براحتك دون استعجال"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Switch(
                            checked = state.preferences.timerEnabled,
                            onCheckedChange = { checked ->
                                onToggleTimer(checked, state.preferences.timerSeconds)
                            },
                            modifier = Modifier.testTag("timer_switch"),
                        )
                    }

                    if (state.preferences.timerEnabled) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "المدة:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            listOf(15, 25, 40).forEach { sec ->
                                FilterChip(
                                    selected = state.preferences.timerSeconds == sec,
                                    onClick = { onToggleTimer(true, sec) },
                                    label = { Text("$sec ث") },
                                    modifier = Modifier.testTag("timer_duration_$sec"),
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (state.preferences.soundEnabled) {
                                Icons.AutoMirrored.Filled.VolumeUp
                            } else {
                                Icons.AutoMirrored.Filled.VolumeOff
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "المؤثرات الصوتية",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = if (state.preferences.soundEnabled) {
                                    "مفعّلة عند الإجابة الصحيحة والخاطئة"
                                } else {
                                    "صامت — بدون أصوات"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Switch(
                        checked = state.preferences.soundEnabled,
                        onCheckedChange = onToggleSound,
                        modifier = Modifier.testTag("sound_switch"),
                    )
                }
            }

            AdvancedPreferencesSection(
                preferences = state.preferences,
                onToggleHaptics = onToggleHaptics,
                onSelectFontScale = onSelectFontScale,
                onUpdateReminder = onUpdateReminder,
                onShowMessage = onShowMessage,
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    state: QuizUiState,
    onStartQuiz: () -> Unit,
    onOpenAddQuestion: () -> Unit,
) {
    val canStart = state.matchingQuestions.isNotEmpty()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onStartQuiz,
            enabled = canStart,
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(vertical = 15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_quiz_button"),
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (canStart) {
                    "ابدأ الاختبار (${state.effectiveQuestionCount} أسئلة)"
                } else {
                    "لا توجد أسئلة مطابقة للتصفية"
                },
                style = MaterialTheme.typography.titleMedium,
            )
        }

        OutlinedButton(
            onClick = onOpenAddQuestion,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("open_add_question_button"),
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إضافة سؤال جديد وحفظه محلياً",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun DeveloperCreditFooter(onClick: () -> Unit) {
    val zadColors = LocalZadColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("developer_credit_footer"),
        shape = RoundedCornerShape(16.dp),
        color = zadColors.goldSurface.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, zadColors.goldBorder.copy(alpha = 0.65f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Code,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.developer_credit),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "عن المطور والتطبيق",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
