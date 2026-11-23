package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.DifficultyLevel
import com.example.model.QuizCategory
import com.example.model.QuizQuestion
import com.example.ui.theme.LocalZadColors
import com.example.util.QuizShareAndExportUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onSave: (
        category: QuizCategory,
        difficulty: DifficultyLevel,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
    ) -> Unit,
) {
    var category by remember { mutableStateOf(QuizCategory.GENERAL) }
    var difficulty by remember { mutableStateOf(DifficultyLevel.MEDIUM) }
    var questionText by remember { mutableStateOf("") }
    var option0 by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var option3 by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val optionLabels = listOf("الخيار أ", "الخيار ب", "الخيار ج", "الخيار د")

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
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إضافة سؤال جديد",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_question_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "إغلاق نافذة إضافة السؤال",
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
                    Text(
                        text = "القسم",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        QuizCategory.selectableCategories.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.displayName) },
                                modifier = Modifier.testTag("add_category_${cat.key}"),
                            )
                        }
                    }

                    Text(
                        text = "مستوى الصعوبة",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        DifficultyLevel.specificLevels.forEach { level ->
                            FilterChip(
                                selected = difficulty == level,
                                onClick = { difficulty = level },
                                label = { Text(level.displayName) },
                                modifier = Modifier.testTag("add_difficulty_${level.key}"),
                            )
                        }
                    }

                    OutlinedTextField(
                        value = questionText,
                        onValueChange = {
                            questionText = it
                            validationError = null
                        },
                        label = { Text("نص السؤال") },
                        placeholder = { Text("اكتب السؤال بوضوح...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("question_text_input"),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                    )

                    Text(
                        text = "الاختيارات الأربعة (اختر الدائرة بجانب الإجابة الصحيحة)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    val values = listOf(option0, option1, option2, option3)
                    val setters: List<(String) -> Unit> = listOf(
                        { option0 = it; validationError = null },
                        { option1 = it; validationError = null },
                        { option2 = it; validationError = null },
                        { option3 = it; validationError = null },
                    )

                    values.forEachIndexed { idx, value ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = correctIndex == idx,
                                onClick = { correctIndex = idx },
                                modifier = Modifier.testTag("correct_option_radio_$idx"),
                            )
                            OutlinedTextField(
                                value = value,
                                onValueChange = setters[idx],
                                label = { Text(optionLabels[idx]) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("option_input_$idx"),
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                    }

                    OutlinedTextField(
                        value = explanation,
                        onValueChange = {
                            explanation = it
                            validationError = null
                        },
                        label = { Text("فائدة وشرح الإجابة") },
                        placeholder = { Text("أضف توضيحاً مختصراً أو دليلاً يظهر بعد الإجابة...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("explanation_input"),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                    )

                    if (validationError != null) {
                        Text(
                            text = validationError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("cancel_add_question_button"),
                    ) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            val opts = listOf(option0.trim(), option1.trim(), option2.trim(), option3.trim())
                            when {
                                questionText.trim().length < 5 ->
                                    validationError = "يرجى كتابة نص سؤال واضح لا يقل عن 5 أحرف."
                                opts.any { it.isEmpty() } ->
                                    validationError = "يرجى تعبئة جميع الاختيارات الأربعة."
                                opts.distinct().size < 4 ->
                                    validationError = "يرجى التأكد من عدم تكرار الاختيارات."
                                explanation.trim().length < 5 ->
                                    validationError = "يرجى كتابة فائدة أو شرح مختصر للإجابة."
                                else ->
                                    onSave(
                                        category,
                                        difficulty,
                                        questionText.trim(),
                                        opts,
                                        correctIndex,
                                        explanation.trim(),
                                    )
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("save_question_button"),
                    ) {
                        Text("حفظ السؤال")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBankDialog(
    customQuestions: List<QuizQuestion>,
    onDismiss: () -> Unit,
    onAddNew: () -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    onImportJson: (String) -> Unit,
    onExportSuccess: () -> Unit,
    onExportError: (String) -> Unit,
) {
    val context = LocalContext.current
    val zadColors = LocalZadColors.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) {
            runCatching {
                val json = QuizShareAndExportUtils.serializeQuestionsToJson(customQuestions)
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray(Charsets.UTF_8))
                } ?: error("تعذر فتح الملف للكتابة")
            }.onSuccess {
                onExportSuccess()
            }.onFailure { err ->
                onExportError(err.localizedMessage ?: "تعذر تصدير الأسئلة")
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                } ?: ""
            }.onSuccess { text ->
                onImportJson(text)
            }.onFailure { err ->
                onExportError(err.localizedMessage ?: "تعذر قراءة الملف المحدد")
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 620.dp),
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
                    Text(
                        text = "الأسئلة المضافة محلياً (${customQuestions.size})",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_custom_bank_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "إغلاق",
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                if (customQuestions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "لم تُضف أسئلة مخصصة بعد",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "يمكنك إضافة أسئلتك الخاصة وحفظها محلياً لتظهر تلقائياً في الاختبارات.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(customQuestions, key = { it.id }) { q ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
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
                                        IconButton(
                                            onClick = { onDeleteQuestion(q.id) },
                                            modifier = Modifier.testTag("delete_custom_question_${q.id}"),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.DeleteOutline,
                                                contentDescription = "حذف السؤال",
                                                tint = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                    Text(
                                        text = q.questionText,
                                        style = MaterialTheme.typography.titleSmall,
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
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { exportLauncher.launch("zad_custom_questions.json") },
                        enabled = customQuestions.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("export_custom_questions_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تصدير JSON")
                    }
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("import_custom_questions_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("استيراد JSON")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onDismiss()
                        onAddNew()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_from_custom_bank_button"),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إضافة سؤال جديد")
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("dialog_confirm_button"),
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_dismiss_button"),
            ) {
                Text(
                    text = dismissText,
                    color = if (isDestructive) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}
