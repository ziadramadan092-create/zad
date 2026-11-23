package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.FontScaleOption
import com.example.model.QuizPreferences
import com.example.notifications.DailyReminderScheduler
import com.example.ui.theme.LocalZadColors
import java.util.Locale

@Composable
fun SurvivalAndSmartReviewCard(
    bestSurvivalStreak: Int,
    favoriteCount: Int,
    mistakeCount: Int,
    onStartSurvival: () -> Unit,
    onOpenFavorites: () -> Unit,
    onStartMistakes: () -> Unit,
) {
    val zadColors = LocalZadColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("survival_and_smart_review_card"),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = zadColors.wrong,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = "أنماط التحدي والمراجعة الذكية",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                if (bestSurvivalStreak > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = zadColors.goldSurface,
                        border = BorderStroke(1.dp, zadColors.goldBorder),
                    ) {
                        Text(
                            text = "أفضل بقاء: $bestSurvivalStreak",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = zadColors.wrongContainer.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, zadColors.wrong.copy(alpha = 0.45f)),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = zadColors.wrong,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "تحدي البقاء (3 أرواح)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = "أجب عن أكبر عدد متواصل من الأسئلة بـ 3 أرواح فقط ووسائل مساعدة مضاعفة!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        onClick = onStartSurvival,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = zadColors.wrong,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_survival_quiz_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ابدأ تحدي البقاء",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onOpenFavorites,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
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
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdvancedPreferencesSection(
    preferences: QuizPreferences,
    onToggleHaptics: (Boolean) -> Unit,
    onSelectFontScale: (FontScaleOption) -> Unit,
    onUpdateReminder: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            DailyReminderScheduler.scheduleDailyReminder(
                context = context,
                hour = preferences.reminderHour,
                minute = preferences.reminderMinute,
            )
            onUpdateReminder(true, preferences.reminderHour, preferences.reminderMinute)
            val timeStr = String.format(Locale.US, "%02d:%02d", preferences.reminderHour, preferences.reminderMinute)
            onShowMessage("تم تفعيل التذكير اليومي الساعة $timeStr")
        } else {
            onUpdateReminder(false, preferences.reminderHour, preferences.reminderMinute)
            onShowMessage("يرجى السماح بالإشعارات لتفعيل التذكير اليومي")
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Haptic Feedback Toggle
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
                        imageVector = Icons.Filled.Vibration,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "الاهتزاز التفاعلي (Haptics)",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = if (preferences.hapticsEnabled) "اهتزاز لطيف عند اختيار الإجابة" else "مغلق",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Switch(
                    checked = preferences.hapticsEnabled,
                    onCheckedChange = onToggleHaptics,
                    modifier = Modifier.testTag("haptics_switch"),
                )
            }
        }

        // Font Scale Selector
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.FormatSize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "حجم خط الأسئلة والشروحات",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    FontScaleOption.entries.forEach { option ->
                        FilterChip(
                            selected = preferences.fontScale == option,
                            onClick = { onSelectFontScale(option) },
                            label = { Text(option.displayName) },
                            modifier = Modifier.testTag("font_scale_${option.key}"),
                        )
                    }
                }
            }
        }

        // Daily Reminder Notification
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
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "التذكير اليومي للأسئلة",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = if (preferences.reminderEnabled) {
                                    String.format(Locale.US, "مفعّل يومياً الساعة %02d:%02d", preferences.reminderHour, preferences.reminderMinute)
                                } else {
                                    "تنبيه يومي لطيف لمواصلة التعلّم"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Switch(
                        checked = preferences.reminderEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    !DailyReminderScheduler.hasNotificationPermission(context)
                                ) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    DailyReminderScheduler.scheduleDailyReminder(
                                        context = context,
                                        hour = preferences.reminderHour,
                                        minute = preferences.reminderMinute,
                                    )
                                    onUpdateReminder(true, preferences.reminderHour, preferences.reminderMinute)
                                    val timeStr = String.format(Locale.US, "%02d:%02d", preferences.reminderHour, preferences.reminderMinute)
                                    onShowMessage("تم تفعيل التذكير اليومي الساعة $timeStr")
                                }
                            } else {
                                DailyReminderScheduler.cancelDailyReminder(context)
                                onUpdateReminder(false, preferences.reminderHour, preferences.reminderMinute)
                                onShowMessage("تم إيقاف التذكير اليومي")
                            }
                        },
                        modifier = Modifier.testTag("reminder_switch"),
                    )
                }

                if (preferences.reminderEnabled) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        listOf(
                            8 to "08:00 ص",
                            14 to "02:00 م",
                            20 to "08:00 م",
                            22 to "10:00 م",
                        ).forEach { (hour, label) ->
                            FilterChip(
                                selected = preferences.reminderHour == hour,
                                onClick = {
                                    DailyReminderScheduler.scheduleDailyReminder(context, hour, 0)
                                    onUpdateReminder(true, hour, 0)
                                    onShowMessage("تم ضبط التذكير اليومي الساعة $label")
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag("reminder_hour_$hour"),
                            )
                        }
                    }
                }
            }
        }
    }
}
