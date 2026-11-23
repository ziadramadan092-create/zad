package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BadgeCatalog
import com.example.model.BadgeProgress
import com.example.model.LevelInfo
import com.example.ui.theme.LocalZadColors

@Composable
fun LevelAndBadgesDialog(
    levelInfo: LevelInfo,
    badges: List<BadgeProgress>,
    onDismiss: () -> Unit,
) {
    val zadColors = LocalZadColors.current
    val unlockedCount = badges.count { it.isUnlocked }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp),
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
                            imageVector = Icons.Filled.WorkspacePremium,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(26.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "المستوى والأوسمة",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_badges_dialog_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "إغلاق نافذة المستويات والأوسمة",
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${levelInfo.level}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column {
                                    Text(
                                        text = "المستوى ${levelInfo.level}: ${levelInfo.title}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "مجموع النقاط: ${levelInfo.totalXp} XP",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Text(
                                    text = "$unlockedCount / ${badges.size} وسام",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { levelInfo.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                        )

                        Text(
                            text = if (levelInfo.isMaxLevel) {
                                "بلغت أعلى مستوى في زاد المعرفة!"
                            } else {
                                "متبقي ${(levelInfo.nextLevelMinXp ?: 0) - levelInfo.totalXp} XP للوصول إلى المستوى ${levelInfo.level + 1}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "قائمة الأوسمة والإنجازات",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(badges, key = { it.definition.id }) { badge ->
                        BadgeItemCard(badge = badge)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("badges_done_button"),
                ) {
                    Text("إغلاق")
                }
            }
        }
    }
}

@Composable
private fun BadgeItemCard(badge: BadgeProgress) {
    val zadColors = LocalZadColors.current
    val unlocked = badge.isUnlocked

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) {
                zadColors.correctContainer.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            },
        ),
        border = BorderStroke(
            1.dp,
            if (unlocked) zadColors.correct else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (unlocked) zadColors.correct else MaterialTheme.colorScheme.surface,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (unlocked) badgeIcon(badge.definition.id) else Icons.Filled.Lock,
                    contentDescription = if (unlocked) "وسام مفتوح" else "وسام مقفل",
                    tint = if (unlocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = badge.definition.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (unlocked) zadColors.onCorrectContainer else MaterialTheme.colorScheme.onSurface,
                    )
                    if (unlocked) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "مكتمل",
                            tint = zadColors.correct,
                            modifier = Modifier.size(18.dp),
                        )
                    } else {
                        Text(
                            text = "${badge.currentValue}/${badge.definition.targetValue}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Text(
                    text = badge.definition.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (!unlocked) {
                    LinearProgressIndicator(
                        progress = { badge.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(50)),
                        strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

fun badgeIcon(badgeId: String): ImageVector = when (badgeId) {
    BadgeCatalog.FIRST_STEP -> Icons.Filled.Star
    BadgeCatalog.PERSISTENT_50 -> Icons.Filled.School
    BadgeCatalog.SCHOLAR_100 -> Icons.Filled.EmojiEvents
    BadgeCatalog.PERFECTIONIST -> Icons.Filled.AutoAwesome
    BadgeCatalog.DAILY_CHALLENGER -> Icons.Filled.CalendarToday
    BadgeCatalog.DAILY_STREAK_3 -> Icons.Filled.LocalFireDepartment
    BadgeCatalog.SURVIVAL_10 -> Icons.Filled.LocalFireDepartment
    else -> Icons.Filled.MilitaryTech
}
