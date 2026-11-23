package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.ThemeMode

@Immutable
data class ZadSemanticColors(
    val correct: Color,
    val correctContainer: Color,
    val onCorrectContainer: Color,
    val wrong: Color,
    val wrongContainer: Color,
    val onWrongContainer: Color,
    val goldSurface: Color,
    val goldBorder: Color,
)

private val LightZadSemanticColors = ZadSemanticColors(
    correct = LightCorrect,
    correctContainer = LightCorrectContainer,
    onCorrectContainer = LightOnCorrectContainer,
    wrong = LightWrong,
    wrongContainer = LightWrongContainer,
    onWrongContainer = LightOnWrongContainer,
    goldSurface = LightGoldSurface,
    goldBorder = LightGoldBorder,
)

private val DarkZadSemanticColors = ZadSemanticColors(
    correct = DarkCorrect,
    correctContainer = DarkCorrectContainer,
    onCorrectContainer = DarkOnCorrectContainer,
    wrong = DarkWrong,
    wrongContainer = DarkWrongContainer,
    onWrongContainer = DarkOnWrongContainer,
    goldSurface = DarkGoldSurface,
    goldBorder = DarkGoldBorder,
)

val LocalZadColors = staticCompositionLocalOf { LightZadSemanticColors }

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = LightWrong,
    errorContainer = LightWrongContainer,
    onErrorContainer = LightOnWrongContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = DarkWrong,
    errorContainer = DarkWrongContainer,
    onErrorContainer = DarkOnWrongContainer,
)

@Composable
fun ZadAlMarifahTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val semanticColors = if (isDark) DarkZadSemanticColors else LightZadSemanticColors

    CompositionLocalProvider(LocalZadColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ZadTypography,
            content = content,
        )
    }
}
