package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.DailyQuestionBank
import com.example.model.DifficultyLevel
import com.example.model.QuestionCountOption
import com.example.model.QuizCategory
import com.example.ui.QuizUiState
import com.example.ui.theme.LocalZadColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePlayTab(
    state: QuizUiState,
    isWideScreen: Boolean,
    onSelectCategory: (QuizCategory) -> Unit,
    onSelectDifficulty: (DifficultyLevel) -> Unit,
    onSelectCountOption: (QuestionCountOption) -> Unit,
    onCycleTheme: () -> Unit,
    onStartQuiz: () -> Unit,
    onStartDailyQuiz: () -> Unit,
    onStartSurvivalQuiz: () -> Unit,
    onStartMistakesQuiz: () -> Unit,
    onOpenCustomBank: () -> Unit,
    onOpenFavorites: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 880.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CompactHeroBanner(
                themeLabel = state.preferences.themeMode.displayName,
                totalBankCount = state.allQuestions.size + DailyQuestionBank.curatedDailyPool.size,
                customCount = state.customQuestions.size,
                onCycleTheme = onCycleTheme,
                onOpenCustomBank = onOpenCustomBank,
            )

            QuickChallengesRow(
                state = state,
                onStartDailyQuiz = onStartDailyQuiz,
                onStartSurvivalQuiz = onStartSurvivalQuiz,
            )

            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    CategoryGridCard(
                        modifier = Modifier.weight(1.1f),
                        state = state,
                        onSelectCategory = onSelectCategory,
                    )
                    Column(
                        modifier = Modifier.weight(0.9f),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        RoundBuilderCard(
                            state = state,
                            onSelectDifficulty = onSelectDifficulty,
                            onSelectCountOption = onSelectCountOption,
                            onStartQuiz = onStartQuiz,
                        )
                        QuickReviewStrip(
                            favoriteCount = state.favoriteQuestions.size,
                            mistakeCount = state.mistakeQuestions.size,
                            onOpenFavorites = onOpenFavorites,
                            onStartMistakes = onStartMistakesQuiz,
                        )
                    }
                }
            } else {
                CategoryGridCard(
                    modifier = Modifier.fillMaxWidth(),
                    state = state,
                    onSelectCategory = onSelectCategory,
                )
                RoundBuilderCard(
                    state = state,
                    onSelectDifficulty = onSelectDifficulty,
                    onSelectCountOption = onSelectCountOption,
                    onStartQuiz = onStartQuiz,
                )
                QuickReviewStrip(
                    favoriteCount = state.favoriteQuestions.size,
                    mistakeCount = state.mistakeQuestions.size,
                    onOpenFavorites = onOpenFavorites,
                    onStartMistakes = onStartMistakesQuiz,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CompactHeroBanner(
    themeLabel: String,
    totalBankCount: Int,
    customCount: Int,
    onCycleTheme: () -> Unit,
    onOpenCustomBank: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp),
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
                                Color(0xE0062E26),
                                Color(0xBD093B31),
                                Color(0x8C0D4A3E),
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        onClick = onOpenCustomBank,
                        color = Color(0x38FAF6EE),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("open_custom_bank_pill"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                contentDescription = null,
                                tint = Color(0xFFF6D68A),
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بنك الأسئلة: $totalBankCount" + if (customCount > 0) " (+$customCount)" else "",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFFAF6EE),
                                maxLines = 1,
                            )
                        }
                    }

                    Surface(
                        onClick = onCycleTheme,
                        color = Color(0x38FAF6EE),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("theme_toggle_button"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Brightness6,
                                contentDescription = "تبديل المظهر",
                                tint = Color(0xFFFAF6EE),
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = themeLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFFAF6EE),
                                maxLines = 1,
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "زاد المعرفة",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color(0xFFFAF6EE),
                    )
                    Text(
                        text = "اختبارات إسلامية موثقة بالشروحات • يعمل دون اتصال بالإنترنت",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFEAE2D2),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickChallengesRow(
    state: QuizUiState,
    onStartDailyQuiz: () -> Unit,
    onStartSurvivalQuiz: () -> Unit,
) {
    val daily = state.dailyProgress
    val completedToday = daily.isCompletedToday
    val zadColors = LocalZadColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Daily Challenge Compact Card
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onStartDailyQuiz)
                .testTag("start_daily_quiz_button"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (completedToday) {
                    zadColors.correctContainer.copy(alpha = 0.65f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                },
            ),
            border = BorderStroke(
                1.dp,
                if (completedToday) zadColors.correct.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (completedToday) zadColors.correct else MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (completedToday) Icons.Filled.CheckCircle else Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    if (daily.currentStreak > 0) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        ) {
                            Text(
                                text = "${daily.currentStreak} أيام",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "الأسئلة اليومية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = if (completedToday) {
                            "أُنجز (${daily.todayCorrect}/${daily.todayTotal}) • مراجعة"
                        } else {
                            "${DailyQuestionBank.DAILY_QUESTION_COUNT} أسئلة • +40 XP"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        // Survival Challenge Compact Card
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onStartSurvivalQuiz)
                .testTag("start_survival_quiz_button"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = zadColors.goldSurface,
            ),
            border = BorderStroke(1.dp, zadColors.goldBorder.copy(alpha = 0.75f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(zadColors.wrong),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    if (state.stats.bestSurvivalStreak > 0) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        ) {
                            Text(
                                text = "أعلى: ${state.stats.bestSurvivalStreak}",
                                style = MaterialTheme.typography.labelMedium,
                                color = zadColors.wrong,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "تحدي البقاء",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "3 أرواح • أطول سلسلة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGridCard(
    modifier: Modifier = Modifier,
    state: QuizUiState,
    onSelectCategory: (QuizCategory) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
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
                Text(
                    text = "اختر القسم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${state.selectedCategory.displayName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            QuizCategory.entries.chunked(2).forEach { rowItems ->
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
                        CategoryTile(
                            modifier = Modifier.weight(1f),
                            category = category,
                            count = count,
                            selected = state.selectedCategory == category,
                            onClick = { onSelectCategory(category) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTile(
    modifier: Modifier = Modifier,
    category: QuizCategory,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("category_tile_${category.key}"),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = if (selected) BorderStroke(1.5.dp, borderColor) else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
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
                    modifier = Modifier.size(17.dp),
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
                    maxLines = 1,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoundBuilderCard(
    state: QuizUiState,
    onSelectDifficulty: (DifficultyLevel) -> Unit,
    onSelectCountOption: (QuestionCountOption) -> Unit,
    onStartQuiz: () -> Unit,
) {
    val canStart = state.matchingQuestions.isNotEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
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
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "تخصيص الجولة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = "المتاح: ${state.matchingQuestions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }

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

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "عدد الأسئلة",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    QuestionCountOption.entries.forEach { option ->
                        val label = if (option == QuestionCountOption.ALL) {
                            "الكل (${state.matchingQuestions.size})"
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

            Button(
                onClick = onStartQuiz,
                enabled = canStart,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
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
                        "ابدأ الاختبار (${state.effectiveQuestionCount} سؤالاً)"
                    } else {
                        "لا توجد أسئلة مطابقة للتصفية"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun QuickReviewStrip(
    favoriteCount: Int,
    mistakeCount: Int,
    onOpenFavorites: () -> Unit,
    onStartMistakes: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedButton(
            onClick = onOpenFavorites,
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("open_favorites_button"),
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "المفضلة ($favoriteCount)",
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        OutlinedButton(
            onClick = onStartMistakes,
            enabled = mistakeCount > 0,
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("start_mistakes_quiz_button"),
        ) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                tint = if (mistakeCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "الأخطاء ($mistakeCount)",
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
