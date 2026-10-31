package com.example.util

import android.content.Context
import android.content.Intent
import java.util.Calendar

data class DailyThought(
    val id: Int,
    val hindi: String,
    val english: String,
    val authorOrContext: String = "भाटी परिवार प्रेरणा"
)

object DailyThoughtHelper {
    private val thoughts = listOf(
        DailyThought(
            id = 1,
            hindi = "एकता में ही परिवार की असली शक्ति है। जहाँ परस्पर विश्वास और स्नेह है, वहाँ सदैव सुख-समृद्धि का वास होता है।",
            english = "Unity is the true strength of family. Where there is mutual trust and love, prosperity always resides."
        ),
        DailyThought(
            id = 2,
            hindi = "सफलता का कोई शार्टकट नहीं होता, निरंतर निष्ठा, कठिन परिश्रम और अपनों का सहयोग ही विजय दिलाता है।",
            english = "There is no shortcut to success; persistent dedication, hard work, and family support bring triumph."
        ),
        DailyThought(
            id = 3,
            hindi = "सच्चा धन तिजोरी में नहीं, बल्कि परिवार के प्रेम, एकता, संस्कारों और परस्पर सहयोग में समाहित होता है।",
            english = "True wealth lies not in lockers, but in family love, harmony, values, and mutual support."
        ),
        DailyThought(
            id = 4,
            hindi = "संगठन में असीम शक्ति है। जब पूरा परिवार एक संकल्प के साथ आगे बढ़ता है, तो कोई भी लक्ष्य असंभव नहीं रहता।",
            english = "There is immense power in unity. When a family moves forward with single resolve, no goal is impossible."
        ),
        DailyThought(
            id = 5,
            hindi = "ईमानदारी और लगन से किया गया प्रत्येक कार्य सदैव मन को शांति और समाज में प्रतिष्ठा प्रदान करता है।",
            english = "Every action performed with honesty and diligence brings peace of mind and honor in society."
        ),
        DailyThought(
            id = 6,
            hindi = "बड़ों का आशीर्वाद और छोटों का आदर ही किसी भी कुल और समाज को गौरवशाली और महान बनाता है।",
            english = "Blessings of elders and affection for youngsters make any community glorious and noble."
        ),
        DailyThought(
            id = 7,
            hindi = "दूसरों के सहयोग और सेवा के लिए उठे हाथ, केवल प्रार्थना के लिए जुड़े हाथों से कहीं अधिक पवित्र होते हैं।",
            english = "Hands that rise to help and serve others are far holier than hands merely joined in prayer."
        ),
        DailyThought(
            id = 8,
            hindi = "सकारात्मक सोच, मधुर वाणी और परस्पर भाईचारा जीवन की हर कठिन राह को सरल और सुखद बना देते हैं।",
            english = "Positive mindset, gentle words, and brotherhood make every difficult path smooth and pleasant."
        ),
        DailyThought(
            id = 9,
            hindi = "समय और संस्कारों का सही सदुपयोग ही व्यक्ति को समाज का आदर्श नागरिक और कुल का दीपक बनाता है।",
            english = "Right utilization of time and values makes a person an ideal citizen and a guiding light for family."
        ),
        DailyThought(
            id = 10,
            hindi = "परिवार एक ऐसा विशाल वृक्ष है जिसकी जड़ें संस्कार हैं, तना एकता है और फल सदैव सुख एवं शांति हैं।",
            english = "Family is a grand tree whose roots are values, trunk is unity, and fruits are lifelong peace."
        ),
        DailyThought(
            id = 11,
            hindi = "हर नया दिन ईश्वर का दिया हुआ एक नया अवसर है—नेक कार्य करने का और अपनों का मान-सम्मान बढ़ाने का।",
            english = "Every new day is a divine blessing—an opportunity to do noble deeds and elevate our people's dignity."
        ),
        DailyThought(
            id = 12,
            hindi = "त्याग, सेवा और पारदर्शिता से ही किसी भी संगठन या परिवार का मान-सम्मान पीढ़ी-दर-पीढ़ी बढ़ता है।",
            english = "Sacrifice, selfless service, and transparency elevate an organization's stature across generations."
        ),
        DailyThought(
            id = 13,
            hindi = "धैर्य और विनम्रता वह अनमोल आभूषण हैं जो हर व्यक्ति के व्यक्तित्व और चरित्र को सुशोभित करते हैं।",
            english = "Patience and humility are priceless ornaments that enrich one's personality and character."
        ),
        DailyThought(
            id = 14,
            hindi = "सच्चा नेतृत्व वह है जो सबको साथ लेकर चले, सबके सुख-दुख को समझे और पारदर्शी व्यवस्था बनाए रखे।",
            english = "True leadership walks with everyone, understands shared emotions, and maintains total transparency."
        )
    )

    fun getTodayThought(): DailyThought {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear + 2) % thoughts.size
        return thoughts[index]
    }

    fun shareThought(context: Context, thought: DailyThought, isEnglish: Boolean = false) {
        val text = buildString {
            append("✨ *युवा शक्ति ग्रुप - आज का सुविचार* ✨\n\n")
            append("❝ ${thought.hindi} ❞\n\n")
            if (isEnglish) {
                append("❝ ${thought.english} ❞\n\n")
            }
            append("🚩 भाटी परिवार • परस्पर एकता व सहयोग")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "सुविचार शेयर करें"))
    }
}
