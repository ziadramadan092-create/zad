package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CategoryPerformance
import com.example.model.DailyProgress
import com.example.model.QuizStats
import com.example.model.UserProgress
import com.example.ui.theme.LocalZadColors

@Composable
fun AnalyticsDialog(
    stats: QuizStats,
    userProgress: UserProgress,
    dailyProgress: DailyProgress,
    onDismiss: () -> Unit,
) {
    val zadColors = LocalZadColors.current
    val performances = userProgress.categoryPerformances
    val attemptedCategories = performances.filter { it.answered > 0 }
    val strongest = attemptedCategories.maxByOrNull { it.accuracyPercent }
    val needsPractice = attemptedCategories.minByOrNull { it.accuracyPercent }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تحليل الأداء والإحصائيات",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_analytics_dialog_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "إغلاق التحليلات",
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AnalyticsMiniMetric(
                            modifier = Modifier.weight(1f),
                            label = "الدقة العامة",
                            value = if (stats.totalQuestionsAnswered > 0) "${stats.overallAccuracyPercent}%" else "—",
                        )
                        AnalyticsMiniMetric(
                            modifier = Modifier.weight(1f),
                            label = "سلسلة البقاء",
                            value = "${stats.bestSurvivalStreak}",
                        )
                        AnalyticsMiniMetric(
                            modifier = Modifier.weight(1f),
                            label = "تتابع اليومي",
                            value = "${dailyProgress.currentStreak} يوم",
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = "الرسم البياني لإتقان الأقسام",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )

                            CategoryBarChart(performances = performances)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "تفصيل الأقسام",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        performances.forEach { perf ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = perf.category.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Text(
                                        text = if (perf.answered > 0) {
                                            "${perf.correct}/${perf.answered} (${perf.accuracyPercent}%)"
                                        } else {
                                            "لم يُختبر بعد"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { perf.accuracyPercent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = if (perf.accuracyPercent >= 75) zadColors.correct else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round,
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = zadColors.goldSurface,
                        border = BorderStroke(1.dp, zadColors.goldBorder),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp),
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "توصية التعلّم الذكية",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                val advice = when {
                                    strongest == null -> "ابدأ بعض الجولات في الأقسام المختلفة لنرسم لك خريطة نقاط قوتك وتقدمك."
                                    strongest.category == needsPractice?.category ->
                                        "أداؤك في «${strongest.category.displayName}» بنسبة ${strongest.accuracyPercent}%. جرّب بقية الأقسام لتوسيع معارفك!"
                                    else ->
                                        "أقوى أقسامك حالياً هو «${strongest.category.displayName}» (${strongest.accuracyPercent}%). ننصحك بتكثيف التدريب في «${needsPractice?.category?.displayName}» (${needsPractice?.accuracyPercent}%)."
                                }
                                Text(
                                    text = advice,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("analytics_done_button"),
                ) {
                    Text("إغلاق")
                }
            }
        }
    }
}

@Composable
private fun AnalyticsMiniMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CategoryBarChart(performances: List<CategoryPerformance>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
        ) {
            val barCount = performances.size.coerceAtLeast(1)
            val slotWidth = size.width / barCount
            val barWidth = (slotWidth * 0.45f).coerceAtMost(44.dp.toPx())
            val maxBarHeight = size.height - 12.dp.toPx()

            // Horizontal reference lines
            listOf(0.25f, 0.5f, 0.75f, 1f).forEach { fraction ->
                val y = size.height - (maxBarHeight * fraction)
                drawLine(
                    color = trackColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            performances.forEachIndexed { idx, perf ->
                val centerX = slotWidth * idx + slotWidth / 2f
                val left = centerX - barWidth / 2f

                // Background track
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(left, size.height - maxBarHeight),
                    size = Size(barWidth, maxBarHeight),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                )

                val valueFraction = (perf.accuracyPercent / 100f).coerceIn(0f, 1f)
                val barHeight = (maxBarHeight * valueFraction).coerceAtLeast(if (perf.answered > 0) 6.dp.toPx() else 0f)
                if (barHeight > 0f) {
                    drawRoundRect(
                        color = if (perf.accuracyPercent >= 70) primaryColor else secondaryColor,
                        topLeft = Offset(left, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            performances.forEach { perf ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${perf.accuracyPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = perf.category.shortName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
