package com.example

import com.example.data.DailyQuestionBank
import com.example.data.DefaultQuestions
import com.example.model.BadgeCatalog
import com.example.model.DifficultyLevel
import com.example.model.LevelSystem
import com.example.model.QuizCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizBankAndLogicTest {

    @Test
    fun defaultQuestionBank_hasOver300ValidDistinctQuestionsAcrossAllCategories() {
        val questions = DefaultQuestions.questions
        assertTrue("Expected more than 300 default questions, got ${questions.size}", questions.size > 300)
        assertEquals(310, questions.size)
        assertEquals(questions.size, questions.map { it.id }.distinct().size)
        assertEquals(questions.size, questions.map { it.questionText.trim() }.distinct().size)

        val dailyTexts = DailyQuestionBank.curatedDailyPool.map { it.questionText.trim() }.toSet()
        assertTrue(
            "Default questions should not duplicate daily curated questions",
            questions.none { it.questionText.trim() in dailyTexts },
        )

        QuizCategory.selectableCategories.forEach { category ->
            val count = questions.count { it.category == category }
            assertEquals("Category ${category.displayName} should have 62 questions", 62, count)
        }

        DifficultyLevel.specificLevels.forEach { level ->
            val count = questions.count { it.difficulty == level }
            assertTrue("Difficulty ${level.displayName} should have >= 60 questions", count >= 60)
        }

        questions.forEach { q ->
            assertTrue("Question ${q.id} text must not be blank", q.questionText.isNotBlank())
            assertEquals("Question ${q.id} must have 4 options", 4, q.options.size)
            assertTrue("Question ${q.id} options must be non-blank", q.options.all { it.isNotBlank() })
            assertEquals("Question ${q.id} options must be distinct", 4, q.options.distinct().size)
            assertTrue("Question ${q.id} correctIndex out of bounds", q.correctIndex in 0..3)
            assertTrue("Question ${q.id} explanation must not be blank", q.explanation.isNotBlank())
        }
    }

    @Test
    fun dailyQuestionBank_isDistinctFromDefaultAndRotatesCleanlyPerDay() {
        val defaultIds = DefaultQuestions.questions.map { it.id }.toSet()
        val dailyPool = DailyQuestionBank.curatedDailyPool

        assertEquals(30, dailyPool.size)
        assertEquals(dailyPool.size, dailyPool.map { it.id }.distinct().size)
        assertTrue(dailyPool.none { it.id in defaultIds })

        dailyPool.forEach { q ->
            assertTrue(q.questionText.isNotBlank())
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.distinct().size)
            assertTrue(q.correctIndex in 0..3)
            assertTrue(q.explanation.isNotBlank())
        }

        // Consecutive days inside a 6-day cycle must have completely distinct question IDs
        val baseDay = 20_000L
        val day0Ids = DailyQuestionBank.getQuestionsForDay(baseDay).map { it.id }.toSet()
        val day1Ids = DailyQuestionBank.getQuestionsForDay(baseDay + 1).map { it.id }.toSet()
        val day2Ids = DailyQuestionBank.getQuestionsForDay(baseDay + 2).map { it.id }.toSet()

        assertEquals(5, day0Ids.size)
        assertEquals(5, day1Ids.size)
        assertEquals(5, day2Ids.size)
        assertTrue(day0Ids.intersect(day1Ids).isEmpty())
        assertTrue(day1Ids.intersect(day2Ids).isEmpty())
        assertTrue(day0Ids.intersect(day2Ids).isEmpty())

        // Same day returns identical deterministic questions
        assertEquals(
            DailyQuestionBank.getQuestionsForDay(baseDay).map { it.id },
            DailyQuestionBank.getQuestionsForDay(baseDay).map { it.id },
        )
    }

    @Test
    fun levelSystem_andBadges_computeAccurately() {
        val level1 = LevelSystem.computeLevelInfo(0)
        assertEquals(1, level1.level)
        assertFalse(level1.isMaxLevel)
        assertEquals(0f, level1.progressFraction, 0.001f)

        val level2 = LevelSystem.computeLevelInfo(100)
        assertEquals(2, level2.level)

        val maxLevel = LevelSystem.computeLevelInfo(5000)
        assertEquals(10, maxLevel.level)
        assertTrue(maxLevel.isMaxLevel)
        assertEquals(1f, maxLevel.progressFraction, 0.001f)

        assertEquals(11, BadgeCatalog.all.size)
        assertEquals(BadgeCatalog.all.size, BadgeCatalog.all.map { it.id }.distinct().size)
        assertTrue(BadgeCatalog.all.all { it.targetValue > 0 && it.title.isNotBlank() })
    }

    @Test
    fun fontScaleOption_resolvesCleanly() {
        assertEquals(com.example.model.FontScaleOption.NORMAL, com.example.model.FontScaleOption.fromKey("unknown"))
        assertEquals(1.15f, com.example.model.FontScaleOption.fromKey("large").multiplier, 0.001f)
        assertEquals(1.3f, com.example.model.FontScaleOption.fromKey("xlarge").multiplier, 0.001f)
    }
}
