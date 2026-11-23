package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.LifelineState
import com.example.model.QuizMode
import com.example.model.QuizQuestion
import com.example.model.SurvivalState
import com.example.ui.QuizUiState
import com.example.ui.theme.LocalZadColors
import com.example.util.QuizShareAndExportUtils

@Composable
fun QuizScreen(
    state: QuizUiState,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onToggleTimer: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onUseFiftyFifty: () -> Unit,
    onUseHint: () -> Unit,
    onUseFreezeTimer: () -> Unit,
    onRequestExit: () -> Unit,
) {
    BackHandler { onRequestExit() }

    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val question = state.currentQuestion
    val totalCount = state.sessionQuestions.size
    val currentNumber = state.currentQuestionIndex + 1
    val isSurvivalOver = state.activeQuizMode == QuizMode.SURVIVAL && state.survivalState.livesRemaining <= 0
    val isLastQuestion = isSurvivalOver || currentNumber >= totalCount
    val fontScale = state.preferences.fontScale.multiplier
    val animatedProgress by animateFloatAsState(
        targetValue = state.quizProgressFraction,
        animationSpec = tween(durationMillis = 300),
        label = "quiz_progress",
    )

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
                    modifier = Modifier.widthIn(max = 820.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    QuizTopHeader(
                        mode = state.activeQuizMode,
                        survivalState = state.survivalState,
                        currentNumber = currentNumber,
                        totalCount = totalCount,
                        progressPercent = state.quizProgressPercent,
                        progressFraction = animatedProgress,
                        timerEnabled = state.sessionTimerEnabled,
                        timerFrozen = state.lifelineState.timerFrozenThisQuestion,
                        remainingSeconds = state.remainingSeconds,
                        isAnswerLocked = state.isAnswerLocked,
                        soundEnabled = state.preferences.soundEnabled,
                        onToggleTimer = onToggleTimer,
                        onToggleSound = onToggleSound,
                        onRequestExit = onRequestExit,
                    )

                    if (question != null) {
                        QuestionCard(
                            question = question,
                            isFavorite = state.isCurrentQuestionFavorite,
                            fontScale = fontScale,
                            onToggleFavorite = { onToggleFavorite(question.questionText) },
                        )

                        LifelinesBar(
                            lifelines = state.lifelineState,
                            isAnswerLocked = state.isAnswerLocked,
                            timerEnabled = state.sessionTimerEnabled,
                            onUseFiftyFifty = {
                                if (state.preferences.hapticsEnabled) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onUseFiftyFifty()
                            },
                            onUseHint = {
                                if (state.preferences.hapticsEnabled) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onUseHint()
                            },
                            onUseFreezeTimer = {
                                if (state.preferences.hapticsEnabled) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onUseFreezeTimer()
                            },
                        )

                        AnimatedVisibility(
                            visible = state.lifelineState.activeHintText != null && !state.isAnswerLocked,
                            enter = fadeIn(tween(180)) + expandVertically(tween(200)),
                        ) {
                            HintBannerCard(hintText = state.lifelineState.activeHintText.orEmpty())
                        }

                        OptionsSection(
                            question = question,
                            isWideScreen = isWideScreen,
                            fontScale = fontScale,
                            selectedOptionIndex = state.selectedOptionIndex,
                            eliminatedIndices = state.lifelineState.eliminatedOptionIndices,
                            isAnswerLocked = state.isAnswerLocked,
                            onSelectOption = { idx ->
                                if (state.preferences.hapticsEnabled) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onSelectOption(idx)
                            },
                        )

                        AnimatedVisibility(
                            visible = state.isAnswerLocked,
                            enter = fadeIn(tween(220)) + expandVertically(tween(240)),
                        ) {
                            ExplanationCard(
                                question = question,
                                selectedOptionIndex = state.selectedOptionIndex,
                                isTimedOut = state.isTimedOut,
                                fontScale = fontScale,
                                onShareQuestion = {
                                    QuizShareAndExportUtils.shareQuestion(context, question)
                                },
                            )
                        }

                        if (state.isAnswerLocked) {
                            Button(
                                onClick = onNextQuestion,
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(vertical = 14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("next_question_button"),
                            ) {
                                Text(
                                    text = if (isLastQuestion) "عرض النتيجة النهائية" else "السؤال التالي",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun QuizTopHeader(
    mode: QuizMode,
    survivalState: SurvivalState,
    currentNumber: Int,
    totalCount: Int,
    progressPercent: Int,
    progressFraction: Float,
    timerEnabled: Boolean,
    timerFrozen: Boolean,
    remainingSeconds: Int,
    isAnswerLocked: Boolean,
    soundEnabled: Boolean,
    onToggleTimer: () -> Unit,
    onToggleSound: () -> Unit,
    onRequestExit: () -> Unit,
) {
    val zadColors = LocalZadColors.current
    val isUrgent = timerEnabled && !timerFrozen && !isAnswerLocked && remainingSeconds <= 5
    val modeBadgeText = when (mode) {
        QuizMode.DAILY -> "الأسئلة اليومية"
        QuizMode.SURVIVAL -> "تحدي البقاء"
        QuizMode.FAVORITES -> "المفضلة"
        QuizMode.MISTAKES -> "مراجعة الأخطاء"
        QuizMode.STANDARD -> null
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onRequestExit,
                    modifier = Modifier.testTag("exit_quiz_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "العودة للإعدادات",
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = if (mode == QuizMode.SURVIVAL) {
                                "السؤال $currentNumber"
                            } else {
                                "السؤال $currentNumber/$totalCount"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                        )
                        if (modeBadgeText != null) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                            ) {
                                Text(
                                    text = modeBadgeText,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text = if (mode == QuizMode.SURVIVAL) {
                            "سلسلة متواصلة: ${survivalState.currentStreak}"
                        } else {
                            "الإنجاز: $progressPercent%"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("session_sound_toggle"),
                ) {
                    Icon(
                        imageVector = if (soundEnabled) {
                            Icons.AutoMirrored.Filled.VolumeUp
                        } else {
                            Icons.AutoMirrored.Filled.VolumeOff
                        },
                        contentDescription = if (soundEnabled) "كتم المؤثرات الصوتية" else "تشغيل المؤثرات الصوتية",
                        tint = if (soundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Surface(
                    onClick = onToggleTimer,
                    shape = RoundedCornerShape(50),
                    color = when {
                        !timerEnabled -> MaterialTheme.colorScheme.surfaceVariant
                        timerFrozen -> MaterialTheme.colorScheme.secondaryContainer
                        isUrgent -> zadColors.wrongContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            !timerEnabled -> MaterialTheme.colorScheme.outline
                            timerFrozen -> MaterialTheme.colorScheme.secondary
                            isUrgent -> zadColors.wrong
                            else -> MaterialTheme.colorScheme.primary
                        },
                    ),
                    modifier = Modifier.testTag("session_timer_toggle"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = when {
                                !timerEnabled -> Icons.Filled.TimerOff
                                timerFrozen -> Icons.Filled.AcUnit
                                else -> Icons.Filled.Timer
                            },
                            contentDescription = if (timerEnabled) "إيقاف المؤقت" else "تفعيل المؤقت",
                            tint = when {
                                !timerEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                                timerFrozen -> MaterialTheme.colorScheme.secondary
                                isUrgent -> zadColors.wrong
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                !timerEnabled -> "بلا مؤقت"
                                timerFrozen -> "مجمّد ($remainingSeconds ث)"
                                else -> "$remainingSeconds ث"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = when {
                                !timerEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                                timerFrozen -> MaterialTheme.colorScheme.onSecondaryContainer
                                isUrgent -> zadColors.wrong
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            },
                        )
                    }
                }
            }
        }

        if (mode == QuizMode.SURVIVAL) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("survival_lives_bar"),
                shape = RoundedCornerShape(14.dp),
                color = zadColors.wrongContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, zadColors.wrong.copy(alpha = 0.4f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "الأرواح:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        repeat(survivalState.maxLives) { idx ->
                            val alive = idx < survivalState.livesRemaining
                            Icon(
                                imageVector = if (alive) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (alive) "روح متبقية" else "روح مفقودة",
                                tint = if (alive) zadColors.wrong else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "سلسلة: ${survivalState.currentStreak}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        } else {
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun QuestionCard(
    question: QuizQuestion,
    isFavorite: Boolean,
    fontScale: Float,
    onToggleFavorite: () -> Unit,
) {
    val baseStyle = MaterialTheme.typography.headlineSmall
    val scaledStyle = baseStyle.copy(
        fontSize = baseStyle.fontSize * fontScale,
        lineHeight = baseStyle.lineHeight * fontScale,
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = question.category.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = question.difficulty.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("quiz_favorite_button"),
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = if (isFavorite) "إزالة من المفضلة" else "حفظ في المفضلة",
                        tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = question.questionText,
                style = scaledStyle,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun LifelinesBar(
    lifelines: LifelineState,
    isAnswerLocked: Boolean,
    timerEnabled: Boolean,
    onUseFiftyFifty: () -> Unit,
    onUseHint: () -> Unit,
    onUseFreezeTimer: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LifelinePill(
            modifier = Modifier
                .weight(1f)
                .testTag("lifeline_5050"),
            icon = Icons.Filled.Filter2,
            label = "حذف إجابتين",
            countLeft = lifelines.fiftyFiftyLeft,
            enabled = !isAnswerLocked && lifelines.fiftyFiftyLeft > 0 && lifelines.eliminatedOptionIndices.isEmpty(),
            onClick = onUseFiftyFifty,
        )
        LifelinePill(
            modifier = Modifier
                .weight(1f)
                .testTag("lifeline_hint"),
            icon = Icons.Filled.TipsAndUpdates,
            label = "تلميح",
            countLeft = lifelines.hintLeft,
            enabled = !isAnswerLocked && lifelines.hintLeft > 0 && lifelines.activeHintText == null,
            onClick = onUseHint,
        )
        LifelinePill(
            modifier = Modifier
                .weight(1f)
                .testTag("lifeline_freeze"),
            icon = Icons.Filled.AcUnit,
            label = "تجميد المؤقت",
            countLeft = lifelines.freezeLeft,
            enabled = !isAnswerLocked && timerEnabled && lifelines.freezeLeft > 0 && !lifelines.timerFrozenThisQuestion,
            onClick = onUseFreezeTimer,
        )
    }
}

@Composable
private fun LifelinePill(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    countLeft: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.alpha(if (enabled) 1f else 0.45f),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "$countLeft",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HintBannerCard(hintText: String) {
    val zadColors = LocalZadColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hint_banner_card"),
        shape = RoundedCornerShape(16.dp),
        color = zadColors.goldSurface,
        border = BorderStroke(1.dp, zadColors.goldBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.TipsAndUpdates,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "تلميح: $hintText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun OptionsSection(
    question: QuizQuestion,
    isWideScreen: Boolean,
    fontScale: Float,
    selectedOptionIndex: Int?,
    eliminatedIndices: Set<Int>,
    isAnswerLocked: Boolean,
    onSelectOption: (Int) -> Unit,
) {
    val letters = listOf("أ", "ب", "ج", "د")

    if (isWideScreen) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            question.options.chunked(2).forEachIndexed { rowIdx, pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    pair.forEachIndexed { colIdx, text ->
                        val index = rowIdx * 2 + colIdx
                        OptionItemCard(
                            modifier = Modifier.weight(1f),
                            letter = letters.getOrElse(index) { "" },
                            text = text,
                            index = index,
                            fontScale = fontScale,
                            correctIndex = question.correctIndex,
                            selectedIndex = selectedOptionIndex,
                            isEliminated = index in eliminatedIndices,
                            isLocked = isAnswerLocked,
                            onClick = { onSelectOption(index) },
                        )
                    }
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            question.options.forEachIndexed { index, text ->
                OptionItemCard(
                    modifier = Modifier.fillMaxWidth(),
                    letter = letters.getOrElse(index) { "" },
                    text = text,
                    index = index,
                    fontScale = fontScale,
                    correctIndex = question.correctIndex,
                    selectedIndex = selectedOptionIndex,
                    isEliminated = index in eliminatedIndices,
                    isLocked = isAnswerLocked,
                    onClick = { onSelectOption(index) },
                )
            }
        }
    }
}

@Composable
private fun OptionItemCard(
    modifier: Modifier = Modifier,
    letter: String,
    text: String,
    index: Int,
    fontScale: Float,
    correctIndex: Int,
    selectedIndex: Int?,
    isEliminated: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
) {
    val zadColors = LocalZadColors.current
    val isCorrectOption = index == correctIndex
    val isSelectedOption = index == selectedIndex

    val targetContainer = when {
        isEliminated -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        !isLocked -> MaterialTheme.colorScheme.surface
        isCorrectOption -> zadColors.correctContainer
        isSelectedOption -> zadColors.wrongContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val targetBorder = when {
        isEliminated -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        !isLocked -> MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        isCorrectOption -> zadColors.correct
        isSelectedOption -> zadColors.wrong
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }

    val containerColor by animateColorAsState(targetValue = targetContainer, label = "opt_bg_$index")
    val borderColor by animateColorAsState(targetValue = targetBorder, label = "opt_border_$index")
    val contentAlpha = when {
        isEliminated -> 0.3f
        isLocked && !isCorrectOption && !isSelectedOption -> 0.6f
        else -> 1f
    }

    val baseOptStyle = MaterialTheme.typography.bodyLarge
    val scaledOptStyle = baseOptStyle.copy(
        fontSize = baseOptStyle.fontSize * fontScale,
        lineHeight = baseOptStyle.lineHeight * fontScale,
    )

    Surface(
        modifier = modifier
            .alpha(contentAlpha)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isLocked && !isEliminated, onClick = onClick)
            .testTag("quiz_option_$index"),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(if (isLocked && (isCorrectOption || isSelectedOption)) 2.dp else 1.dp, borderColor),
        tonalElevation = if (!isLocked && !isEliminated) 1.dp else 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isLocked && isCorrectOption -> zadColors.correct
                            isLocked && isSelectedOption -> zadColors.wrong
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isLocked && (isCorrectOption || isSelectedOption) -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    },
                )
            }

            Text(
                text = text,
                style = scaledOptStyle,
                fontWeight = if (isLocked && (isCorrectOption || isSelectedOption)) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isLocked && isCorrectOption -> zadColors.onCorrectContainer
                    isLocked && isSelectedOption -> zadColors.onWrongContainer
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f),
            )

            if (isLocked && isCorrectOption) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "إجابة صحيحة",
                    tint = zadColors.correct,
                )
            } else if (isLocked && isSelectedOption) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "إجابة خاطئة",
                    tint = zadColors.wrong,
                )
            }
        }
    }
}

@Composable
private fun ExplanationCard(
    question: QuizQuestion,
    selectedOptionIndex: Int?,
    isTimedOut: Boolean,
    fontScale: Float,
    onShareQuestion: () -> Unit,
) {
    val zadColors = LocalZadColors.current
    val isCorrect = selectedOptionIndex == question.correctIndex
    val baseBodyStyle = MaterialTheme.typography.bodyLarge
    val scaledBodyStyle = baseBodyStyle.copy(
        fontSize = baseBodyStyle.fontSize * fontScale,
        lineHeight = baseBodyStyle.lineHeight * fontScale,
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("explanation_box"),
        shape = RoundedCornerShape(20.dp),
        color = zadColors.goldSurface,
        border = BorderStroke(1.5.dp, zadColors.goldBorder),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    Icon(
                        imageVector = if (isTimedOut) Icons.Filled.WarningAmber else Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = when {
                            isTimedOut -> zadColors.wrong
                            isCorrect -> zadColors.correct
                            else -> MaterialTheme.colorScheme.secondary
                        },
                    )
                    Text(
                        text = when {
                            isTimedOut -> "انتهى الوقت • فائدة وشرح"
                            isCorrect -> "أحسنت! فائدة وشرح"
                            else -> "فائدة وشرح"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                IconButton(
                    onClick = onShareQuestion,
                    modifier = Modifier.testTag("share_question_button"),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "مشاركة السؤال والفائدة",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Text(
                text = question.explanation,
                style = scaledBodyStyle,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
