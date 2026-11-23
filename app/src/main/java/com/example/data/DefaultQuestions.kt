package com.example.data

import com.example.data.questions.FiqhQuestions
import com.example.data.questions.GeneralIslamicQuestions
import com.example.data.questions.ProphetsQuestions
import com.example.data.questions.QuranQuestions
import com.example.data.questions.SeerahQuestions
import com.example.model.QuizQuestion

/**
 * بنك الأسئلة الافتراضي لتطبيق "زاد المعرفة" — 500 سؤال موثق وموزع بالتساوي
 * على الأقسام الخمسة (100 سؤال لكل قسم) ومستويات الصعوبة الثلاثة.
 */
object DefaultQuestions {
    val questions: List<QuizQuestion> = buildList {
        addAll(QuranQuestions.list)
        addAll(SeerahQuestions.list)
        addAll(FiqhQuestions.list)
        addAll(ProphetsQuestions.list)
        addAll(GeneralIslamicQuestions.list)
    }
}
