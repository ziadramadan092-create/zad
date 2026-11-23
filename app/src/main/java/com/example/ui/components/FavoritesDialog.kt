package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.QuizQuestion
import com.example.ui.theme.LocalZadColors
import com.example.util.QuizShareAndExportUtils

@Composable
fun FavoritesDialog(
    favoriteQuestions: List<QuizQuestion>,
    onDismiss: () -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onStartFavoritesQuiz: () -> Unit,
) {
    val context = LocalContext.current
    val zadColors = LocalZadColors.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 660.dp),
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
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(26.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الأسئلة المفضلة (${favoriteQuestions.size})",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_favorites_dialog_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "إغلاق المفضلة",
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                if (favoriteQuestions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(44.dp),
                        )
                        Text(
                            text = "قائمة المفضلة فارغة حالياً",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "اضغط على أيقونة النجمة بجانب أي سؤال أثناء الاختبار أو المراجعة لحفظه هنا والرجوع إليه لاحقاً.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(favoriteQuestions, key = { it.questionText }) { q ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "${q.category.displayName} • ${q.difficulty.displayName}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Row {
                                            IconButton(
                                                onClick = { QuizShareAndExportUtils.shareQuestion(context, q) },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Share,
                                                    contentDescription = "مشاركة السؤال",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                )
                                            }
                                            IconButton(
                                                onClick = { onRemoveFavorite(q.questionText) },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Bookmark,
                                                    contentDescription = "إزالة من المفضلة",
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = q.questionText,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = zadColors.correct,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "الإجابة: ${q.options.getOrElse(q.correctIndex) { "" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = zadColors.correct,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }

                                    Text(
                                        text = q.explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            onStartFavoritesQuiz()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_favorites_quiz_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ابدأ اختباراً من المفضلة (${favoriteQuestions.size})")
                    }
                }
            }
        }
    }
}
