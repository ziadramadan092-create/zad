package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.DifficultyLevel
import com.example.model.FontScaleOption
import com.example.model.QuestionCountOption
import com.example.model.QuizCategory
import com.example.ui.QuizUiState

private enum class HomeTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
) {
    PLAY("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_play"),
    PROGRESS("التقدم", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_tab_progress"),
    SETTINGS("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_tab_settings"),
}

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
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.PLAY) }

    BackHandler(enabled = selectedTab != HomeTab.PLAY) {
        selectedTab = HomeTab.PLAY
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp,
                    ) {
                        HomeTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label,
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                },
                                modifier = Modifier.testTag(tab.testTag),
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HomeTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label,
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag),
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    Crossfade(
                        targetState = selectedTab,
                        animationSpec = tween(durationMillis = 180),
                        label = "home_tab_crossfade",
                    ) { tab ->
                        when (tab) {
                            HomeTab.PLAY -> HomePlayTab(
                                state = state,
                                isWideScreen = isWideScreen,
                                onSelectCategory = onSelectCategory,
                                onSelectDifficulty = onSelectDifficulty,
                                onSelectCountOption = onSelectCountOption,
                                onCycleTheme = onCycleTheme,
                                onStartQuiz = onStartQuiz,
                                onStartDailyQuiz = onStartDailyQuiz,
                                onStartSurvivalQuiz = onStartSurvivalQuiz,
                                onStartMistakesQuiz = onStartMistakesQuiz,
                                onOpenCustomBank = onOpenCustomBank,
                                onOpenFavorites = onOpenFavorites,
                            )

                            HomeTab.PROGRESS -> HomeProgressTab(
                                state = state,
                                onOpenBadges = onOpenBadges,
                                onOpenAnalytics = onOpenAnalytics,
                                onOpenFavorites = onOpenFavorites,
                                onStartMistakesQuiz = onStartMistakesQuiz,
                                onRequestResetStats = onRequestResetStats,
                            )

                            HomeTab.SETTINGS -> HomeSettingsTab(
                                state = state,
                                onToggleTimer = onToggleTimer,
                                onToggleSound = onToggleSound,
                                onToggleHaptics = onToggleHaptics,
                                onSelectFontScale = onSelectFontScale,
                                onUpdateReminder = onUpdateReminder,
                                onCycleTheme = onCycleTheme,
                                onShowMessage = onShowMessage,
                                onOpenAddQuestion = onOpenAddQuestion,
                                onOpenCustomBank = onOpenCustomBank,
                                onOpenAboutDeveloper = onOpenAboutDeveloper,
                            )
                        }
                    }
                }
            }
        }
    }
}
