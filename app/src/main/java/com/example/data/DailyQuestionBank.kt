package com.example.data

import com.example.model.DifficultyLevel
import com.example.model.QuizCategory
import com.example.model.QuizQuestion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

/**
 * بنك الأسئلة اليومية المنتقاة (30 سؤالاً مستقلاً عن البنك الأساسي).
 * يختار كل يوم 5 أسئلة متوازنة ومختلفة تماماً عن الأيام المتجاورة وتُجدد تلقائياً مع بداية كل يوم جديد.
 */
object DailyQuestionBank {

    const val DAILY_QUESTION_COUNT = 5

    data class DailyDayInfo(
        val dateKey: String,
        val epochDay: Long,
        val displayDateArabic: String,
    )

    fun getCurrentDayInfo(): DailyDayInfo {
        val cal = Calendar.getInstance()
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
        val displayArabic = SimpleDateFormat("EEEE d MMMM", Locale.forLanguageTag("ar")).format(cal.time)
        val localOffset = cal.timeZone.getOffset(cal.timeInMillis)
        val epochDay = (cal.timeInMillis + localOffset) / 86_400_000L
        return DailyDayInfo(
            dateKey = dateKey,
            epochDay = epochDay,
            displayDateArabic = displayArabic,
        )
    }

    /**
     * يُنتج مجموعة يومية ثابتة لليوم الحالي مكونة من 5 أسئلة (سؤال من كل قسم)
     * مع ضمان عدم تكرار نفس الأسئلة في الأيام الستة المتتالية.
     */
    fun getQuestionsForDay(epochDay: Long): List<QuizQuestion> {
        val categories = QuizCategory.selectableCategories
        val cycleIndex = (epochDay / 6L).toInt()
        val dayInCycle = ((epochDay % 6L) + 6L).toInt() % 6

        return categories.mapIndexed { catIdx, category ->
            val pool = curatedDailyPool.filter { it.category == category }
            val shuffledPool = pool.shuffled(Random(cycleIndex * 31L + catIdx * 17L))
            shuffledPool[dayInCycle % shuffledPool.size]
        }
    }

    val curatedDailyPool: List<QuizQuestion> = listOf(
        // ==================== القرآن الكريم (6 أسئلة يومية) ====================
        QuizQuestion(
            id = 10001L,
            category = QuizCategory.QURAN,
            difficulty = DifficultyLevel.EASY,
            questionText = "ما أقصر سورة في القرآن الكريم من حيث عدد الآيات والكلمات؟",
            options = listOf("سورة الإخلاص", "سورة الكوثر", "سورة النصر", "سورة العصر"),
            correctIndex = 1,
            explanation = "سورة الكوثر هي أقصر سورة في القرآن الكريم، وتتكوّن من ثلاث آيات قصيرة، وفيها بشارة للنبي ﷺ بالخير الكثير والحوض المورود."
        ),
        QuizQuestion(
            id = 10002L,
            category = QuizCategory.QURAN,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما السورة التي أشار النبي ﷺ إلى أنها تعدل ثلث القرآن الكريم؟",
            options = listOf("سورة الفاتحة", "سورة الكافرون", "سورة الإخلاص", "سورة الزلزلة"),
            correctIndex = 2,
            explanation = "ثبت في الصحيحين أن سورة الإخلاص ﴿قُلْ هُوَ اللَّهُ أَحَدٌ﴾ تعدل ثلث القرآن؛ لأن القرآن توحيد وأحكام وقصص، وهذه السورة خالصة في التوحيد."
        ),
        QuizQuestion(
            id = 10003L,
            category = QuizCategory.QURAN,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما السورة التي ذُكر فيها اسم نبي الله «يوسف» عليه السلام أكثر من غيرها وسُميت باسمه؟",
            options = listOf("سورة هود", "سورة يوسف", "سورة غافر", "سورة الأنبياء"),
            correctIndex = 1,
            explanation = "سورة يوسف هي السورة الثانية عشرة في المصحف، وتفردت بسرد قصة يوسف عليه السلام كاملة متصلة، وقد وصفها الله بأحسن القصص."
        ),
        QuizQuestion(
            id = 10004L,
            category = QuizCategory.QURAN,
            difficulty = DifficultyLevel.HARD,
            questionText = "ما الآية الوحيدة التي جمعت حروف اللغة العربية الثمانية والعشرين في سورة الفتح؟",
            options = listOf(
                "الآية الأولى: إنا فتحنا لك فتحاً مبيناً",
                "الآية الأخيرة: محمد رسول الله والذين معه أشداء على الكفار",
                "آية البيعة: لقد رضي الله عن المؤمنين",
                "آية السكينة: هو الذي أنزل السكينة في قلوب المؤمنين"
            ),
            correctIndex = 1,
            explanation = "الآية التاسعة والعشرون والأخيرة من سورة الفتح: ﴿مُّحَمَّدٌ رَّسُولُ اللَّهِ...﴾ اشتملت على جميع حروف الهجاء العربية الثمانية والعشرين."
        ),
        QuizQuestion(
            id = 10005L,
            category = QuizCategory.QURAN,
            difficulty = DifficultyLevel.EASY,
            questionText = "في أي سورة وردت آية الكرسي؟",
            options = listOf("سورة آل عمران", "سورة النساء", "سورة البقرة", "سورة المائدة"),
            correctIndex = 2,
            explanation = "آية الكرسي هي الآية رقم 255 من سورة البقرة، وهي أعظم آية في كتاب الله لما اشتملت عليه من توحيد الله وعظيم صفاته."
        ),
        QuizQuestion(
            id = 10006L,
            category = QuizCategory.QURAN,
            difficulty = DifficultyLevel.HARD,
            questionText = "ما السورة التي تُعرف بـ «سورة التوديع» لأنها أُنزلت مبشرةً بقرب أجل النبي ﷺ؟",
            options = listOf("سورة النصر", "سورة الكوثر", "سورة العصر", "سورة الضحى"),
            correctIndex = 0,
            explanation = "سورة النصر ﴿إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ﴾ فهم الصحابة ومنهم ابن عباس وعمر رضي الله عنهما أنها نعيٌ لنبي الله ﷺ وأمرٌ بالاستغفار والتهيؤ للقاء الله."
        ),

        // ==================== السيرة النبوية (6 أسئلة يومية) ====================
        QuizQuestion(
            id = 10007L,
            category = QuizCategory.SEERAH,
            difficulty = DifficultyLevel.EASY,
            questionText = "ما اسم ناقة النبي ﷺ التي أُمر الصحابة بتركها حرةً حتى بركت في موضع المسجد النبوي؟",
            options = listOf("العضباء", "القصواء", "الجدعاء", "الدلدل"),
            correctIndex = 1,
            explanation = "ناقة النبي ﷺ هي «القصواء»، وحين دخل المدينة قال: «دعوها فإنها مأمورة»، فبركت في مربدٍ لغلامين يتيمين فاشتراه النبي ﷺ وبنى فيه مسجده."
        ),
        QuizQuestion(
            id = 10008L,
            category = QuizCategory.SEERAH,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما أول عمل قام به النبي ﷺ عند وصوله إلى المدينة المنورة في بناء المجتمع المسلم؟",
            options = listOf(
                "بناء المسجد النبوي",
                "كتابة عهد خيبر",
                "بناء السور حول المدينة",
                "إرسال السرايا إلى الشام"
            ),
            correctIndex = 0,
            explanation = "بدأ النبي ﷺ ببناء المسجد النبوي ليكون مركز العبادة والتعليم والشورى والأخوة، ثم آخى بين المهاجرين والأنصار وكتب وثيقة المدينة."
        ),
        QuizQuestion(
            id = 10009L,
            category = QuizCategory.SEERAH,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما الاسم القديم للمدينة المنورة قبل هجرة النبي ﷺ إليها؟",
            options = listOf("بكة", "يثرب", "تيماء", "هجر"),
            correctIndex = 1,
            explanation = "كانت المدينة تُسمى «يثرب» قبل الهجرة، فلما هاجر إليها النبي ﷺ سماها الله ورسوله «طيبة» و«المدينة»."
        ),
        QuizQuestion(
            id = 10010L,
            category = QuizCategory.SEERAH,
            difficulty = DifficultyLevel.HARD,
            questionText = "من الصحابي الجليل الذي تولى الأذان للنبي ﷺ إلى جانب بلال رضي الله عنه في المسجد النبوي؟",
            options = listOf("عبد الله بن أم مكتوم", "أبو محذورة", "سعد القرظ", "أنس بن مالك"),
            correctIndex = 0,
            explanation = "كان عبد الله بن أم مكتوم رضي الله عنه يؤذن مع بلال رضي الله عنه؛ فيؤذن بلال بليل ويؤذن ابن أم مكتوم عند طلوع الفجر الصادق."
        ),
        QuizQuestion(
            id = 10011L,
            category = QuizCategory.SEERAH,
            difficulty = DifficultyLevel.EASY,
            questionText = "من مرضعة النبي ﷺ التي عاش في بادية بني سعد في طفولته؟",
            options = listOf("أم أيمن", "حليمة السعدية", "ثويبة الأسلمية", "الشفاء بنت عوف"),
            correctIndex = 1,
            explanation = "حليمة السعدية رضي الله عنها هي مرضعة النبي ﷺ من بني سعد، وقد حلت البركة في ديارهم ومواشيهم طوال إقامة النبي ﷺ عندهم."
        ),
        QuizQuestion(
            id = 10012L,
            category = QuizCategory.SEERAH,
            difficulty = DifficultyLevel.HARD,
            questionText = "في أي سنة هجرية فُتحت مكة المكرمة؟",
            options = listOf("السنة السادسة للهجرة", "السنة السابعة للهجرة", "السنة الثامنة للهجرة", "السنة التاسعة للهجرة"),
            correctIndex = 2,
            explanation = "فتح الله مكة المكرمة لنبيه ﷺ في شهر رمضان من السنة الثامنة للهجرة فتحاً عظيماً، فدخلها متواضعاً خافضاً رأسه شاكراً لله."
        ),

        // ==================== الفقه والعبادات (6 أسئلة يومية) ====================
        QuizQuestion(
            id = 10013L,
            category = QuizCategory.FIQH,
            difficulty = DifficultyLevel.EASY,
            questionText = "كم عدد ركعات صلاة المغرب المفروضة؟",
            options = listOf("ركعتان", "ثلاث ركعات", "أربع ركعات", "خمس ركعات"),
            correctIndex = 1,
            explanation = "صلاة المغرب ثلاث ركعات في الحضر والسفر، يُجهر بالقراءة في الركعتين الأوليين ويُسر في الثالثة."
        ),
        QuizQuestion(
            id = 10014L,
            category = QuizCategory.FIQH,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما حكم صلاة الوتر عند جمهور الفقهاء (الشافعية والمالكية والحنابلة)؟",
            options = listOf("فرض عين", "سنة مؤكدة", "فرض كفاية", "مباحة فقط"),
            correctIndex = 1,
            explanation = "صلاة الوتر سنة مؤكدة واظب عليها النبي ﷺ في الحضر والسفر، وأقلها ركعة واحدة، ووقتها من بعد صلاة العشاء إلى طلوع الفجر."
        ),
        QuizQuestion(
            id = 10015L,
            category = QuizCategory.FIQH,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما العبادة التي يُشترط لها دخول الوقت واستقبال القبلة والطهارة وستر العورة؟",
            options = listOf("الصدقة", "الصلاة", "الذكر المطلق", "صلة الرحم"),
            correctIndex = 1,
            explanation = "هذه من شروط صحة الصلاة التي لا تصح الصلاة إلا بتحققها قبل الدخول في تكبيرة الإحرام مع القدرة."
        ),
        QuizQuestion(
            id = 10016L,
            category = QuizCategory.FIQH,
            difficulty = DifficultyLevel.HARD,
            questionText = "كم عدد أيام صيام «الست من شوال» التي قال فيها النبي ﷺ إنها مع رمضان كصيام الدهر؟",
            options = listOf("ثلاثة أيام", "ستة أيام", "عشرة أيام", "سبعة أيام"),
            correctIndex = 1,
            explanation = "قال النبي ﷺ: «من صام رمضان ثم أتبعه ستاً من شوال كان كصيام الدهر»، لأن الحسنة بعشر أمثالها فشهر بعشرة أشهر وستة أيام بشهرين."
        ),
        QuizQuestion(
            id = 10017L,
            category = QuizCategory.FIQH,
            difficulty = DifficultyLevel.EASY,
            questionText = "بماذا يبدأ المسلم الوضوء إذا أراد التطهر للصلاة؟",
            options = listOf("بالتشهد", "بالنية والتسمية وغسل الكفين", "بمسح الرأس أولاً", "بغسل القدمين"),
            correctIndex = 1,
            explanation = "ينوي المسلم الوضوء بقلبه ويقول «بسم الله»، ثم يغسل كفيه ثلاثاً ثم يتمضمض ويستنشق، وهكذا على الترتيب الوارد في السنة."
        ),
        QuizQuestion(
            id = 10018L,
            category = QuizCategory.FIQH,
            difficulty = DifficultyLevel.HARD,
            questionText = "ما الاسم الشرعي للبدل الذي يُؤدى عند تعذر استخدام الماء في الوضوء أو الغسل؟",
            options = listOf("الاستجمار", "التيمم", "المسح", "النضح"),
            correctIndex = 1,
            explanation = "التيمم بالصعيد الطيب رخصة من الله تعالى للمسلم إذا فُقد الماء أو تعذر استعماله لمرض ونحوه: ﴿فَتَيَمَّمُوا صَعِيدًا طَيِّبًا﴾."
        ),

        // ==================== قصص الأنبياء (6 أسئلة يومية) ====================
        QuizQuestion(
            id = 10019L,
            category = QuizCategory.PROPHETS,
            difficulty = DifficultyLevel.EASY,
            questionText = "من النبي الذي بنى السفينة بأمر الله ونجا فيها مع المؤمنين من الطوفان؟",
            options = listOf("هود عليه السلام", "نوح عليه السلام", "صالح عليه السلام", "لوط عليه السلام"),
            correctIndex = 1,
            explanation = "أوحى الله إلى نوح عليه السلام أن يصنع الفلك بعدما دعا قومه ألف سنة إلا خمسين عاماً، فأنجاه الله ومن آمن معه في السفينة."
        ),
        QuizQuestion(
            id = 10020L,
            category = QuizCategory.PROPHETS,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما النبي الذي اشتهر بالصبر الجميل على فقد الولد والمرض والمال ثم عافاه الله؟",
            options = listOf("أيوب عليه السلام", "زكريا عليه السلام", "إدريس عليه السلام", "إسحاق عليه السلام"),
            correctIndex = 0,
            explanation = "أيوب عليه السلام ضرب أروع الأمثلة في الصبر والرضا، فنادى ربه: ﴿أَنِّي مَسَّنِيَ الضُّرُّ وَأَنتَ أَرْحَمُ الرَّاحِمِينَ﴾ فكشف الله ضره."
        ),
        QuizQuestion(
            id = 10021L,
            category = QuizCategory.PROPHETS,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "من النبي الذي فهم منطق الطير والنمل وسخّر الله له الريح والجن؟",
            options = listOf("داود عليه السلام", "سليمان عليه السلام", "موسى عليه السلام", "يوسف عليه السلام"),
            correctIndex = 1,
            explanation = "دعا سليمان عليه السلام ربه أن يهبه ملكاً لا ينبغي لأحد من بعده، فعلمه الله منطق الطير وسخر له الريح والجن، وشكر الله على نعمه."
        ),
        QuizQuestion(
            id = 10022L,
            category = QuizCategory.PROPHETS,
            difficulty = DifficultyLevel.HARD,
            questionText = "من النبي الذي كفل مريم عليها السلام في المحراب وكان يدخل عليها فيجد عندها رزقاً؟",
            options = listOf("يحيى عليه السلام", "زكريا عليه السلام", "عمران", "هارون عليه السلام"),
            correctIndex = 1,
            explanation = "كفل نبي الله زكريا عليه السلام السيدة مريم عليها السلام، وحين رأى كرامة الله لها بالرزق في غير أوانه دعا ربه فبشره بيحيى عليه السلام."
        ),
        QuizQuestion(
            id = 10023L,
            category = QuizCategory.PROPHETS,
            difficulty = DifficultyLevel.EASY,
            questionText = "من النبي الملقب بـ «كليم الله» لأنه كلمه الله تعالى تكليماً؟",
            options = listOf("إبراهيم عليه السلام", "موسى عليه السلام", "عيسى عليه السلام", "يعقوب عليه السلام"),
            correctIndex = 1,
            explanation = "قال تعالى: ﴿وَكَلَّمَ اللَّهُ مُوسَىٰ تَكْلِيمًا﴾؛ فاختص الله نبيه موسى عليه السلام بهذا التكريم العظيم عند جبل الطور."
        ),
        QuizQuestion(
            id = 10024L,
            category = QuizCategory.PROPHETS,
            difficulty = DifficultyLevel.HARD,
            questionText = "ما اسم القرية أو المكان الذي وُلد فيه عيسى عليه السلام وفق المأثور؟",
            options = listOf("أريحا", "بيت لحم", "الخليل", "عسقلان"),
            correctIndex = 1,
            explanation = "وُلد نبي الله عيسى ابن مريم عليه السلام في بيت لحم بأرض فلسطين، وأنطقه الله في المهد: ﴿قَالَ إِنِّي عَبْدُ اللَّهِ آتَانِيَ الْكِتَابَ وَجَعَلَنِي نَبِيًّا﴾."
        ),

        // ==================== معلومات إسلامية عامة (6 أسئلة يومية) ====================
        QuizQuestion(
            id = 10025L,
            category = QuizCategory.GENERAL,
            difficulty = DifficultyLevel.EASY,
            questionText = "ما القبلة الأولى للمسلمين قبل تحويلها إلى الكعبة المشرفة؟",
            options = listOf("مسجد قباء", "المسجد الأقصى", "مسجد الخيف", "المسجد النبوي"),
            correctIndex = 1,
            explanation = "صلى المسلمون نحو بيت المقدس في مكة وقرابة ستة عشر أو سبعة عشر شهراً في المدينة، ثم أمر الله بالتوجه شطر المسجد الحرام."
        ),
        QuizQuestion(
            id = 10026L,
            category = QuizCategory.GENERAL,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "كم عدد الأشهر الحرم في التقويم الهجري؟",
            options = listOf("شهران", "ثلاثة أشهر", "أربعة أشهر", "خمسة أشهر"),
            correctIndex = 2,
            explanation = "الأشهر الحرم أربعة: ثلاثة متوالية هي ذو القعدة وذو الحجة والمحرم، وواحد فرد هو رجب مضر، وقد حرم الله فيها القتال ابتداءً وعظّم حرمتها."
        ),
        QuizQuestion(
            id = 10027L,
            category = QuizCategory.GENERAL,
            difficulty = DifficultyLevel.MEDIUM,
            questionText = "ما الكنية المشهورة للصحابي الجليل عبد الرحمن بن صخر الدوسي راوي الأحاديث؟",
            options = listOf("أبو ذر الغفاري", "أبو هريرة", "أبو الدرداء", "أبو سعيد الخدري"),
            correctIndex = 1,
            explanation = "هو أبو هريرة رضي الله عنه، أكثر الصحابة روايةً للحديث النبوي الشريف؛ لملازمته التامة للنبي ﷺ ودعائه له ألا ينسى ما يسمعه."
        ),
        QuizQuestion(
            id = 10028L,
            category = QuizCategory.GENERAL,
            difficulty = DifficultyLevel.HARD,
            questionText = "من الإمام صاحب كتاب «الموطأ» وإمام دار الهجرة؟",
            options = listOf("الإمام أبو حنيفة", "الإمام مالك بن أنس", "الإمام الشافعي", "الإمام أحمد بن حنبل"),
            correctIndex = 1,
            explanation = "الإمام مالك بن أنس رحمه الله هو إمام دار الهجرة (المدينة المنورة) ومؤسس المذهب المالكي وصاحب كتاب «الموطأ»."
        ),
        QuizQuestion(
            id = 10029L,
            category = QuizCategory.GENERAL,
            difficulty = DifficultyLevel.EASY,
            questionText = "ما الدعاء المأثور عند الدخول إلى المسجد؟",
            options = listOf(
                "اللهم افتح لي أبواب رحمتك",
                "اللهم إني أسألك من فضلك",
                "سبحان الذي سخر لنا هذا",
                "باسمك ربي وضعت جنبي"
            ),
            correctIndex = 0,
            explanation = "يُسن للمسلم إذا دخل المسجد أن يقدم رجله اليمنى ويقول: «اللهم افتح لي أبواب رحمتك»، وإذا خرج قدم اليسرى وقال: «اللهم إني أسألك من فضلك»."
        ),
        QuizQuestion(
            id = 10030L,
            category = QuizCategory.GENERAL,
            difficulty = DifficultyLevel.HARD,
            questionText = "من العالم المسلم الذي وضع أسس علم الجبر وألّف كتاب «المختصر في حساب الجبر والمقابلة»؟",
            options = listOf("ابن الهيثم", "محمد بن موسى الخوارزمي", "البيروني", "الإدريسي"),
            correctIndex = 1,
            explanation = "محمد بن موسى الخوارزمي رحمه الله هو مؤسس علم الجبر في القرن الثالث الهجري، ومن اسمه اشتُق مصطلح الخوارزميات (Algorithms) في العلوم الحديثة."
        ),
    )
}
