package com.example.util

import android.content.Context
import android.content.Intent
import com.example.model.DifficultyLevel
import com.example.model.QuizCategory
import com.example.model.QuizQuestion
import org.json.JSONArray
import org.json.JSONObject

object QuizShareAndExportUtils {

    fun shareQuestion(context: Context, question: QuizQuestion) {
        val correctAnswer = question.options.getOrElse(question.correctIndex) { "" }
        val text = buildString {
            appendLine("📖 سؤال من تطبيق زاد المعرفة (${question.category.displayName}):")
            appendLine()
            appendLine(question.questionText)
            appendLine()
            appendLine("✅ الإجابة الصحيحة: $correctAnswer")
            appendLine("💡 الفائدة والشرح: ${question.explanation}")
            appendLine()
            append("— زاد المعرفة | المطور Eng-Ziad Ramadan")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة السؤال"))
    }

    fun shareResultSummary(
        context: Context,
        correct: Int,
        total: Int,
        percent: Int,
        levelNumber: Int,
        levelTitle: String,
        xpEarned: Int,
        isSurvival: Boolean,
        survivalStreak: Int,
    ) {
        val text = buildString {
            appendLine("🏆 نتيجتي في تطبيق زاد المعرفة:")
            appendLine()
            if (isSurvival) {
                appendLine("🔥 تحدي البقاء: أجبت عن $survivalStreak إجابات متتالية ($correct من $total)")
            } else {
                appendLine("📊 النتيجة: $correct من $total (نسبة الإتقان: $percent%)")
            }
            if (xpEarned > 0) {
                appendLine("✨ نقاط الخبرة المكتسبة: +$xpEarned XP")
            }
            appendLine("🎓 المستوى الحالي: $levelNumber ($levelTitle)")
            appendLine()
            append("— زاد المعرفة | المطور Eng-Ziad Ramadan")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة النتيجة"))
    }

    fun serializeQuestionsToJson(questions: List<QuizQuestion>): String {
        val array = JSONArray()
        questions.forEach { q ->
            val obj = JSONObject().apply {
                put("category", q.category.key)
                put("difficulty", q.difficulty.key)
                put("questionText", q.questionText)
                put("options", JSONArray(q.options))
                put("correctIndex", q.correctIndex)
                put("explanation", q.explanation)
            }
            array.put(obj)
        }
        val root = JSONObject().apply {
            put("app", "زاد المعرفة")
            put("version", 1)
            put("questions", array)
        }
        return root.toString(2)
    }

    fun parseQuestionsFromJson(jsonString: String): List<QuizQuestion> {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) return emptyList()

        val array = if (trimmed.startsWith("[")) {
            JSONArray(trimmed)
        } else {
            val root = JSONObject(trimmed)
            root.optJSONArray("questions") ?: JSONArray()
        }

        val parsed = mutableListOf<QuizQuestion>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val text = obj.optString("questionText", "").trim()
            val explanation = obj.optString("explanation", "").trim()
            val optionsArray = obj.optJSONArray("options") ?: continue
            if (optionsArray.length() != 4) continue
            val options = (0 until 4).map { idx -> optionsArray.optString(idx, "").trim() }
            if (text.length < 5 || explanation.length < 3 || options.any { it.isEmpty() } || options.distinct().size < 4) {
                continue
            }
            val correctIndex = obj.optInt("correctIndex", 0).coerceIn(0, 3)
            val categoryKey = obj.optString("category", QuizCategory.GENERAL.key)
            val difficultyKey = obj.optString("difficulty", DifficultyLevel.MEDIUM.key)
            val category = QuizCategory.selectableCategories.firstOrNull { it.key == categoryKey } ?: QuizCategory.GENERAL
            val difficulty = DifficultyLevel.specificLevels.firstOrNull { it.key == difficultyKey } ?: DifficultyLevel.MEDIUM

            parsed.add(
                QuizQuestion(
                    id = 0L,
                    category = category,
                    difficulty = difficulty,
                    questionText = text,
                    options = options,
                    correctIndex = correctIndex,
                    explanation = explanation,
                    isCustom = true,
                ),
            )
        }
        return parsed
    }
}
