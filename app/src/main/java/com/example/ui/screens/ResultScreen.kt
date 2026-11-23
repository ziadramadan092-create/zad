package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.AnsweredRecord
import com.example.model.PerformanceBreakdown
import com.example.model.QuizMode
import com.example.model.RoundRewards
import com.example.ui.QuizUiState
import com.example.ui.components.badgeIcon
import com.example.ui.theme.LocalZadColors
import com.example.util.QuizShareAndExportUtils

@Composable
fun ResultScreen(
    state: QuizUiState,
    onRestartSameSettings: () -> Unit,
    onChooseNewCategory: () -> Unit,
    onOpenBadges: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRequestResetStats: () -> Unit,
) {
    BackHandler { onChooseNewCategory() }

    val context = LocalContext.current
    val wrongRecords = remember(state.answeredRecords) {
        state.answeredRecords.filter { !it.isCorrect }
    }
    var showOnlyWrong by remember(state.answeredRecords) {
        mutableStateOf(wrongRecords.isNotEmpty())
    }
    val displayedRecords = if (showOnlyWrong) wrongRecords else state.answeredRecords

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
                    modifier = Modifier.widthIn(max = 840.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MasteryHeroCard(
                        mode = state.activeQuizMode,
                        survivalStreak = state.survivalState.currentStreak,
                        correct = state.roundCorrectCount,
                        total = state.roundTotalCount,
                        percent = state.roundMasteryPercent,
                    )

                    state.lastRoundRewards?.let { rewards ->
                        RoundRewardsCard(
                            rewards = rewards,
                            isDailyMode = state.activeQuizMode == QuizMode.DAILY,
                            onOpenBadges = onOpenBadges,
                        )
                    }

                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            BreakdownCard(
                                modifier = Modifier.weight(1f),
                                title = "الأداء حسب القسم",
                                breakdowns = state.categoryBreakdowns,
                            )
                            BreakdownCard(
                                modifier = Modifier.weight(1f),
                                title = "الأداء حسب الصعوبة",
                                breakdowns = state.difficultyBreakdowns,
                            )
                        }
                    } else {
                        BreakdownCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = "الأداء حسب القسم",
                            breakdowns = state.categoryBreakdowns,
                        )
                        BreakdownCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = "الأداء حسب مستوى الصعوبة",
                            breakdowns = state.difficultyBreakdowns,
                        )
                    }

                    ResultActionsCard(
                        mode = state.activeQuizMode,
                        onShareResult = {
                            QuizShareAndExportUtils.shareResultSummary(
                                context = context,
                                correct = state.roundCorrectCount,
                                total = state.roundTotalCount,
                                percent = state.roundMasteryPercent,
                                levelNumber = state.levelInfo.level,
                                levelTitle = state.levelInfo.title,
                                xpEarned = state.lastRoundRewards?.xpEarned ?: 0,
                                isSurvival = state.activeQuizMode == QuizMode.SURVIVAL,
                                survivalStreak = state.survivalState.currentStreak,
                            )
                        },
                        onRestartSameSettings = onRestartSameSettings,
                        onChooseNewCategory = onChooseNewCategory,
                        onRequestResetStats = onRequestResetStats,
                    )

                    ReviewSectionHeader(
                        wrongCount = wrongRecords.size,
                        totalCount = state.answeredRecords.size,
                        showOnlyWrong = showOnlyWrong,
                        onToggleFilter = { showOnlyWrong = it },
                    )

                    if (displayedRecords.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = LocalZadColors.current.correctContainer,
                            border = BorderStroke(1.dp, LocalZadColors.current.correct),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = LocalZadColors.current.correct,
                                    modifier = Modifier.size(34.dp),
                                )
                                Text(
                                    text = "ما شاء الله! لم تخطئ في أي سؤال في هذه الجولة",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LocalZadColors.current.onCorrectContainer,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "يمكنك اختيار «جميع أسئلة الجولة» لمراجعة كل الفوائد والشروحات.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LocalZadColors.current.onCorrectContainer,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            displayedRecords.forEachIndexed { idx, record ->
                                val isFav = record.question.questionText in state.favoriteTexts
                                ReviewQuestionCard(
                                    index = idx + 1,
                                    record = record,
                                    isFavorite = isFav,
                                    onToggleFavorite = { onToggleFavorite(record.question.questionText) },
                                    onShare = { QuizShareAndExportUtils.shareQuestion(context, record.question) },
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoundRewardsCard(
    rewards: RoundRewards,
    isDailyMode: Boolean,
    onOpenBadges: () -> Unit,
) {
    val zadColors = LocalZadColors.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("round_rewards_card"),
        shape = RoundedCornerShape(20.dp),
        color = zadColors.goldSurface,
        border = BorderStroke(1.5.dp, zadColors.goldBorder),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rewards.xpEarned > 0) {
                            "نقاط الخبرة المكتسبة: +${rewards.xpEarned} XP"
                        } else if (isDailyMode) {
                            "مراجعة التحدي اليومي (أتممت تحدي اليوم مسبقاً)"
                        } else {
                            "التقدم والمستوى"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                TextButton(
                    onClick = onOpenBadges,
                    modifier = Modifier.testTag("result_open_badges_button"),
                ) {
                    Text("عرض الأوسمة")
                }
            }

            if (rewards.leveledUpFrom != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = zadColors.correctContainer,
                    border = BorderStroke(1.dp, zadColors.correct),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WorkspacePremium,
                            contentDescription = null,
                            tint = zadColors.correct,
                        )
                        Text(
                            text = "ترقية جديدة! وصلت إلى المستوى ${rewards.newLevelInfo.level}: ${rewards.newLevelInfo.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = zadColors.onCorrectContainer,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            if (rewards.newlyUnlockedBadges.isNotEmpty()) {
                Text(
                    text = "أوسمة جديدة حصلت عليها في هذه الجولة:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rewards.newlyUnlockedBadges.forEach { badge ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = badgeIcon(badge.id),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = badge.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "المستوى الحالي: ${rewards.newLevelInfo.level} (${rewards.newLevelInfo.title}) • ${rewards.newLevelInfo.totalXp} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LinearProgressIndicator(
                    progress = { rewards.newLevelInfo.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun MasteryHeroCard(
    mode: QuizMode,
    survivalStreak: Int,
    correct: Int,
    total: Int,
    percent: Int,
) {
    val zadColors = LocalZadColors.current
    val animatedSweep by animateFloatAsState(
        targetValue = (percent / 100f).coerceIn(0f, 1f) * 360f,
        animationSpec = tween(durationMillis = 700),
        label = "mastery_sweep",
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = when {
        percent >= 80 -> zadColors.correct
        percent >= 50 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    val titleText = when (mode) {
        QuizMode.DAILY -> "نتيجة الأسئلة اليومية"
        QuizMode.SURVIVAL -> "نتيجة تحدي البقاء"
        QuizMode.FAVORITES -> "نتيجة اختبار المفضلة"
        QuizMode.MISTAKES -> "نتيجة مراجعة الأخطاء"
        QuizMode.STANDARD -> "نتيجة الجولة"
    }

    val encouragement = when {
        mode == QuizMode.SURVIVAL && survivalStreak >= 10 ->
            "صمود رائع! حققت سلسلة من $survivalStreak إجابات صحيحة في تحدي البقاء."
        percent >= 90 -> "ما شاء الله! إتقان باهر وحصيلة معرفية مباركة، زادك الله علماً ونفعاً."
        percent >= 70 -> "أحسنت! أداء متميز ومعلومات طيبة، واصل المراجعة والتدرج."
        percent >= 50 -> "بداية موفقة! راجع الشروحات أدناه وستلاحظ تقدماً سريعاً في الجولة القادمة."
        else -> "طلب العلم رحلة مستمرة؛ تصفح الفوائد والشروحات وجرّب جولة جديدة لتثبيت المعلومات."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_summary_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (mode == QuizMode.SURVIVAL) Icons.Filled.LocalFireDepartment else Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            if (mode == QuizMode.SURVIVAL) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = zadColors.goldSurface,
                    border = BorderStroke(1.dp, zadColors.goldBorder),
                ) {
                    Text(
                        text = "أطول سلسلة في الجولة: $survivalStreak إجابة صحيحة",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    )
                }
            }

            Box(
                modifier = Modifier.size(136.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = animatedSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = progressColor,
                    )
                    Text(
                        text = "$correct من $total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = encouragement,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun BreakdownCard(
    modifier: Modifier = Modifier,
    title: String,
    breakdowns: List<PerformanceBreakdown>,
) {
    Card(
        modifier = modifier,
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
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            breakdowns.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = "${item.correct}/${item.total} (${item.percentage}%)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    LinearProgressIndicator(
                        progress = { item.percentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultActionsCard(
    mode: QuizMode,
    onShareResult: () -> Unit,
    onRestartSameSettings: () -> Unit,
    onChooseNewCategory: () -> Unit,
    onRequestResetStats: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onShareResult,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("share_result_button"),
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "مشاركة النتيجة مع الأصدقاء",
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = onRestartSameSettings,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("restart_same_settings_button"),
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (mode == QuizMode.DAILY) "مراجعة أسئلة اليوم" else "إعادة الجولة",
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            OutlinedButton(
                onClick = onChooseNewCategory,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("choose_new_category_button"),
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "الشاشة الرئيسية",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        TextButton(
            onClick = onRequestResetStats,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("result_reset_stats_button"),
        ) {
            Icon(
                imageVector = Icons.Filled.RestartAlt,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("تصفير الإحصائيات المحفوظة")
        }
    }
}

@Composable
private fun ReviewSectionHeader(
    wrongCount: Int,
    totalCount: Int,
    showOnlyWrong: Boolean,
    onToggleFilter: (Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "مراجعة الأسئلة والشروحات",
            style = MaterialTheme.typography.titleLarge,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = showOnlyWrong,
                onClick = { onToggleFilter(true) },
                label = { Text("الأسئلة الخاطئة فقط ($wrongCount)") },
                modifier = Modifier.testTag("review_filter_wrong"),
            )
            FilterChip(
                selected = !showOnlyWrong,
                onClick = { onToggleFilter(false) },
                label = { Text("جميع أسئلة الجولة ($totalCount)") },
                modifier = Modifier.testTag("review_filter_all"),
            )
        }
    }
}

@Composable
private fun ReviewQuestionCard(
    index: Int,
    record: AnsweredRecord,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
) {
    val zadColors = LocalZadColors.current
    val question = record.question
    val correctText = question.options.getOrElse(question.correctIndex) { "" }
    val selectedText = when {
        record.isTimedOut -> "انتهى الوقت دون اختيار"
        record.selectedIndex != null -> question.options.getOrElse(record.selectedIndex) { "" }
        else -> "لم يتم الاختيار"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (record.isCorrect) zadColors.correct.copy(alpha = 0.5f) else zadColors.wrong.copy(alpha = 0.55f),
        ),
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
                Text(
                    text = "$index. ${question.category.displayName} • ${question.difficulty.displayName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "مشاركة السؤال",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = if (isFavorite) "إزالة من المفضلة" else "حفظ في المفضلة",
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (record.isCorrect) zadColors.correctContainer else zadColors.wrongContainer,
                    ) {
                        Text(
                            text = if (record.isCorrect) "صحيحة" else "مراجعة",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (record.isCorrect) zadColors.onCorrectContainer else zadColors.onWrongContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleMedium,
            )

            if (!record.isCorrect) {
                Text(
                    text = "إجابتك: $selectedText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = zadColors.wrong,
                    fontWeight = FontWeight.Medium,
                )
            }

            Text(
                text = "الإجابة الصحيحة: $correctText",
                style = MaterialTheme.typography.bodyMedium,
                color = zadColors.correct,
                fontWeight = FontWeight.SemiBold,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp),
                )
                Text(
                    text = question.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
