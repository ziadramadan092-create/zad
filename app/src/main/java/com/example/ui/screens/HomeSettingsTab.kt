package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FontScaleOption
import com.example.notifications.DailyReminderScheduler
import com.example.ui.QuizUiState
import com.example.ui.theme.LocalZadColors
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeSettingsTab(
    state: QuizUiState,
    onToggleTimer: (Boolean, Int) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onSelectFontScale: (FontScaleOption) -> Unit,
    onUpdateReminder: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
    onCycleTheme: () -> Unit,
    onShowMessage: (String) -> Unit,
    onOpenAddQuestion: () -> Unit,
    onOpenCustomBank: () -> Unit,
    onOpenAboutDeveloper: () -> Unit,
) {
    val context = LocalContext.current
    val preferences = state.preferences
    val zadColors = LocalZadColors.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 860.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "الإعدادات والمحتوى",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            // Section 1: Gameplay Preferences
            SettingsGroupCard(title = "تجربة الاختبار") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SettingsSwitchRow(
                        icon = Icons.Filled.Timer,
                        title = "المؤقت الزمني لكل سؤال",
                        subtitle = if (preferences.timerEnabled) {
                            "مفعّل (${preferences.timerSeconds} ثانية)"
                        } else {
                            "مغلق — أجب دون استعجال"
                        },
                        checked = preferences.timerEnabled,
                        switchTag = "timer_switch",
                        onCheckedChange = { checked -> onToggleTimer(checked, preferences.timerSeconds) },
                    )
                    if (preferences.timerEnabled) {
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
                                    selected = preferences.timerSeconds == sec,
                                    onClick = { onToggleTimer(true, sec) },
                                    label = { Text("$sec ث") },
                                    modifier = Modifier.testTag("timer_duration_$sec"),
                                )
                            }
                        }
                    }
                }

                SettingsDivider()

                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    SettingsSwitchRow(
                        icon = if (preferences.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        title = "المؤثرات الصوتية",
                        subtitle = if (preferences.soundEnabled) "مفعّلة عند الإجابة" else "صامت",
                        checked = preferences.soundEnabled,
                        switchTag = "sound_switch",
                        onCheckedChange = onToggleSound,
                    )
                }

                SettingsDivider()

                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    SettingsSwitchRow(
                        icon = Icons.Filled.Vibration,
                        title = "الاهتزاز التفاعلي",
                        subtitle = if (preferences.hapticsEnabled) "اهتزاز لطيف عند الاختيار" else "مغلق",
                        checked = preferences.hapticsEnabled,
                        switchTag = "haptics_switch",
                        onCheckedChange = onToggleHaptics,
                    )
                }

                SettingsDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.FormatSize,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
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

            // Section 2: Appearance & Daily Reminder
            SettingsGroupCard(title = "المظهر والتنبيهات") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SettingsSwitchRow(
                        icon = Icons.Filled.NotificationsActive,
                        title = "التذكير اليومي للأسئلة",
                        subtitle = if (preferences.reminderEnabled) {
                            String.format(Locale.US, "مفعّل يومياً الساعة %02d:%02d", preferences.reminderHour, preferences.reminderMinute)
                        } else {
                            "تنبيه يومي لطيف لمواصلة التعلّم"
                        },
                        checked = preferences.reminderEnabled,
                        switchTag = "reminder_switch",
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
                    )

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

                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.Filled.Brightness6,
                    title = "مظهر التطبيق",
                    subtitle = "الحالي: ${preferences.themeMode.displayName} (اضغط للتبديل)",
                    onClick = onCycleTheme,
                )
            }

            // Section 3: Custom Question Bank
            SettingsGroupCard(title = "بنك الأسئلة والمحتوى") {
                SettingsActionRow(
                    icon = Icons.Filled.AddCircleOutline,
                    title = "إضافة سؤال جديد",
                    subtitle = "أضف سؤالاً مخصصاً بشرحه واحفظه على جهازك",
                    tag = "open_add_question_button",
                    onClick = onOpenAddQuestion,
                )

                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.AutoMirrored.Filled.LibraryBooks,
                    title = "إدارة الأسئلة المخصصة والنسخ الاحتياطي",
                    subtitle = if (state.customQuestions.isNotEmpty()) {
                        "${state.customQuestions.size} سؤالاً مضافاً • استيراد وتصدير JSON"
                    } else {
                        "استيراد وتصدير حزم أسئلة بصيغة JSON"
                    },
                    onClick = onOpenCustomBank,
                )
            }

            // Section 4: About Developer
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAboutDeveloper)
                    .testTag("developer_credit_footer"),
                shape = RoundedCornerShape(18.dp),
                color = zadColors.goldSurface.copy(alpha = 0.75f),
                border = BorderStroke(1.dp, zadColors.goldBorder.copy(alpha = 0.7f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp),
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.developer_credit),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "عن التطبيق والتواصل مع المطور",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "عن المطور والتطبيق",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SettingsGroupCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    switchTag: String,
    onCheckedChange: (Boolean) -> Unit,
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(switchTag),
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tag: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (tag != null) Modifier.testTag(tag) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.ChevronLeft,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
