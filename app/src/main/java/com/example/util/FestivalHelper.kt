package com.example.util

import java.util.Calendar

data class FestivalInfo(
    val dateString: String,
    val name: String,
    val category: String, // "मुख्य त्योहार", "अमावस्या / पूर्णिमा", "एकादशी व्रत", "राष्ट्रीय पर्व", "पारिवारिक दिवस"
    val tithi: String,
    val description: String,
    val icon: String
)

object FestivalHelper {

    /**
     * Returns detailed festival / tithi information for any day of any month/year.
     */
    fun getFestivalInfo(year: Int, month: Int, day: Int, targetAmavasyaMillis: Long): FestivalInfo {
        val dateKey = "%02d-%02d".format(month + 1, day)
        val fullDateKey = "%04d-%02d-%02d".format(year, month + 1, day)

        // Check if this date matches the target Amavasya
        val targetAmavasyaCal = Calendar.getInstance().apply { timeInMillis = targetAmavasyaMillis }
        val isAmavasyaDay = (targetAmavasyaCal.get(Calendar.YEAR) == year &&
                targetAmavasyaCal.get(Calendar.MONTH) == month &&
                targetAmavasyaCal.get(Calendar.DAY_OF_MONTH) == day)

        if (isAmavasyaDay) {
            val tithiName = AmavasyaCalculator.getHindiTithiName(targetAmavasyaMillis)
            return FestivalInfo(
                dateString = "$day ${getMonthName(month)} $year",
                name = "🌑 $tithiName (मासिक अमावस्या)",
                category = "अमावस्या व समूह बैठक",
                tithi = "$tithiName (कृष्ण पक्ष)",
                description = "🚩 युवा शक्ति ग्रुप, भाटी परिवार कुचेरा का मासिक अंशदान चक्र पूर्ण होने एवं पारिवारिक बैठक का पावन दिवस। परस्पर सहयोग व एकता का संकल्प।",
                icon = "🌑"
            )
        }

        // Year-specific known festivals (e.g. 2026/2027)
        yearSpecificFestivals[fullDateKey]?.let { return it.copy(dateString = "$day ${getMonthName(month)} $year") }

        // General fixed and recurring festivals
        fixedFestivals[dateKey]?.let { return it.copy(dateString = "$day ${getMonthName(month)} $year") }

        // Recurring tithi estimations (Ekadashi, Purnima, Pradosh)
        val dayOfWeek = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }.get(Calendar.DAY_OF_WEEK)

        val dayNamesHindi = listOf("", "रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार")

        return FestivalInfo(
            dateString = "$day ${getMonthName(month)} $year (${dayNamesHindi.getOrElse(dayOfWeek) { "" }})",
            name = "शुभ दिवस (${dayNamesHindi.getOrElse(dayOfWeek) { "" }})",
            category = "दैनिक पंचांग",
            tithi = "पारिवारिक सुविचार व समृद्धि दिवस",
            description = "आज का दिन भाटी परिवार की एकता, धर्म और सत्कर्म के लिए समर्पित है। सभी कार्यों में सफलता एवं स्वास्थ्य की मंगलकामनाएं।",
            icon = "✨"
        )
    }

    /**
     * Returns true if the given day has a notable festival or event (used for dot badge in calendar)
     */
    fun hasSpecialEvent(year: Int, month: Int, day: Int, targetAmavasyaMillis: Long): Boolean {
        val targetAmavasyaCal = Calendar.getInstance().apply { timeInMillis = targetAmavasyaMillis }
        if (targetAmavasyaCal.get(Calendar.YEAR) == year &&
            targetAmavasyaCal.get(Calendar.MONTH) == month &&
            targetAmavasyaCal.get(Calendar.DAY_OF_MONTH) == day) {
            return true
        }

        val fullDateKey = "%04d-%02d-%02d".format(year, month + 1, day)
        val dateKey = "%02d-%02d".format(month + 1, day)

        return yearSpecificFestivals.containsKey(fullDateKey) || fixedFestivals.containsKey(dateKey)
    }

    private fun getMonthName(month: Int): String {
        val names = listOf(
            "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून",
            "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर"
        )
        return names.getOrElse(month) { "" }
    }

    // 2026/2027 Major Hindu Calendar Dates
    private val yearSpecificFestivals = mapOf(
        // 2026 October
        "2026-10-17" to FestivalInfo("", "नवरात्रि आरंभ (घटस्थापना)", "मुख्य त्योहार", "आश्विन शुक्ल प्रतिपदा", "माता शैलपुत्री पूजन व शारदीय नवरात्रि के पावन पर्व का शुभारंभ।", "🔱"),
        "2026-10-24" to FestivalInfo("", "महाअष्टमी / दुर्गा अष्टमी", "मुख्य त्योहार", "आश्विन शुक्ल अष्टमी", "मां महागौरी पूजन, कन्या पूजन व हवन का पावन पर्व।", "🌺"),
        "2026-10-25" to FestivalInfo("", "महानवमी (दुर्गा नवमी)", "मुख्य त्योहार", "आश्विन शुक्ल नवमी", "मां सिद्धिदात्री पूजन व नवरात्रि व्रत पारण।", "🌸"),
        "2026-10-26" to FestivalInfo("", "विजयादशमी / दशहरा", "मुख्य त्योहार", "आश्विन शुक्ल दशमी", "अधर्म पर धर्म और बुराई पर अच्छाई की विजय का प्रतीक - रावण दहन व शस्त्र पूजन।", "🏹"),
        "2026-10-29" to FestivalInfo("", "करवा चौथ व्रत", "मुख्य त्योहार", "कार्तिक कृष्ण चतुर्थी", "अखंड सौभाग्य, सुहाग की दीर्घायु एवं मंगलकामना हेतु निर्जला करवा चौथ व्रत।", "🌙"),
        "2026-10-31" to FestivalInfo("", "अहोई अष्टमी व्रत", "व्रत एवं उपवास", "कार्तिक कृष्ण अष्टमी", "संतान की दीर्घायु, उत्तम स्वास्थ्य व समृद्धि के लिए माताओं का विशेष व्रत।", "⭐"),

        // 2026 November
        "2026-11-06" to FestivalInfo("", "रमा एकादशी व्रत", "एकादशी व्रत", "कार्तिक कृष्ण एकादशी", "भगवान श्री हरि विष्णु जी का पावन एकादशी व्रत। समस्त पापों का नाश करने वाला।", "🪷"),
        "2026-11-07" to FestivalInfo("", "प्रदोष व्रत (शनि प्रदोष)", "व्रत एवं उपवास", "कार्तिक कृष्ण त्रयोदशी", "भगवान शिव एवं माता पार्वती की कृपा प्राप्ति हेतु प्रदोष काल में विशेष पूजन।", "🕉️"),
        "2026-11-08" to FestivalInfo("", "धनतेरस / धन्वंतरि जयंती", "मुख्य त्योहार", "कार्तिक कृष्ण त्रयोदशी", "आरोग्य के देवता भगवान धन्वंतरि एवं कुबेर देव का पूजन। सोना-चांदी व बर्तन क्रय का महामुहूर्त।", "🪙"),
        "2026-11-09" to FestivalInfo("", "नरक चतुर्दशी (रूप चौदस)", "मुख्य त्योहार", "कार्तिक कृष्ण चतुर्दशी", "यमराज निमित्त दीपदान एवं छोटी दिवाली का पावन उत्सव। सौंदर्य व आरोग्य की प्राप्ति।", "🪔"),
        "2026-11-10" to FestivalInfo("", "🪔 दीपावली (महालक्ष्मी पूजन)", "महापर्व", "कार्तिक अमावस्या", "दीपों का महापर्व दीपावली! भगवान श्री राम के 14 वर्ष वनवास उपरांत अयोध्या आगमन पर दीप प्रज्ज्वलन व माता महालक्ष्मी-गणेश जी का भव्य पूजन।", "🪔"),
        "2026-11-11" to FestivalInfo("", "गोवर्धन पूजा / अन्नकूट", "मुख्य त्योहार", "कार्तिक शुक्ल प्रतिपदा", "भगवान श्री कृष्ण द्वारा गोवर्धन पर्वत उठाने की पावन स्मृति में गोवर्धन पूजा व अन्नकूट महोत्सव।", "🌾"),
        "2026-11-12" to FestivalInfo("", "भाई दूज (यम द्वितीया)", "मुख्य त्योहार", "कार्तिक शुक्ल द्वितीया", "भाई-बहन के अगाध स्नेह का पावन पर्व। बहन द्वारा भाई को तिलक लगाकर दीर्घायु की कामना।", "👫"),
        "2026-11-15" to FestivalInfo("", "डाला छठ पूजा (सूर्य षष्ठी)", "महापर्व", "कार्तिक शुक्ल षष्ठी", "भगवान सूर्य देव एवं छठी मैया की उपासना का महापर्व। अस्ताचलगामी एवं उदीयमान सूर्य को अर्घ्य।", "☀️"),
        "2026-11-17" to FestivalInfo("", "गोपाष्टमी उत्सव", "मुख्य त्योहार", "कार्तिक शुक्ल अष्टमी", "गौ माता की पूजा व संरक्षण का पावन उत्सव। भगवान श्री कृष्ण द्वारा गायों को चराने का आरंभ।", "🐄"),
        "2026-11-18" to FestivalInfo("", "आंवला नवमी (अक्षय नवमी)", "धार्मिक पर्व", "कार्तिक शुक्ल नवमी", "आंवले के वृक्ष का पूजन व उसके नीचे भोजन करने से अक्षय पुण्य की प्राप्ति।", "🌳"),
        "2026-11-20" to FestivalInfo("", "देवउठनी एकादशी (प्रबोधिनी)", "एकादशी व्रत", "कार्तिक शुक्ल एकादशी", "चार मास के योगनिद्रा उपरांत श्री हरि का जागरण। समस्त शुभ एवं मांगलिक कार्यों का शुभारंभ व तुलसी विवाह।", "🌸"),
        "2026-11-21" to FestivalInfo("", "तुलसी विवाह उत्सव", "धार्मिक पर्व", "कार्तिक शुक्ल द्वादशी", "माता तुलसी एवं भगवान शालिग्राम का अलौकिक विवाह उत्सव।", "🌿"),
        "2026-11-24" to FestivalInfo("", "कार्तिक पूर्णिमा (देव दीपावली)", "महापर्व", "कार्तिक पूर्णिमा", "त्रिपुरारी शिव द्वारा त्रिपुरासुर संहार पर देवताओं द्वारा देव दिवाली महोत्सव व पवित्र नदियों में महास्नान।", "🌕"),

        // 2026 December
        "2026-12-05" to FestivalInfo("", "उत्पन्ना एकादशी", "एकादशी व्रत", "मार्गशीर्ष कृष्ण एकादशी", "एकादशी माता का प्राकट्य दिवस। श्री हरि विष्णु की विशेष कृपा।", "🪷"),
        "2026-12-09" to FestivalInfo("", "मार्गशीर्ष अमावस्या", "अमावस्या", "मार्गशीर्ष कृष्ण अमावस्या", "पितृ तर्पण व दान-पुण्य हेतु पावन अमावस्या।", "🌑"),
        "2026-12-20" to FestivalInfo("", "मोक्षदा एकादशी / गीता जयंती", "मुख्य पर्व", "मार्गशीर्ष शुक्ल एकादशी", "कुरुक्षेत्र में भगवान श्री कृष्ण द्वारा अर्जुन को श्रीमद्भगवद्गीता उपदेश का पावन दिवस।", "📖"),
        "2026-12-24" to FestivalInfo("", "मार्गशीर्ष पूर्णिमा / दत्तात्रेय जयंती", "पूर्णिमा", "मार्गशीर्ष पूर्णिमा", "भगवान दत्तात्रेय जी का प्राकट्य दिवस व सत्यनारायण कथा।", "🌕"),

        // 2027 Key Dates
        "2027-01-14" to FestivalInfo("", "मकर संक्रांति (पोंगल)", "महापर्व", "सूर्य उत्तरायण", "सूर्य देव का मकर राशि में प्रवेश। तिल-गुड़ दान, पतंगबाजी व महास्नान का पुण्य पर्व।", "🪁"),
        "2027-01-26" to FestivalInfo("", "गणतंत्र दिवस", "राष्ट्रीय पर्व", "26 जनवरी", "भारत का 78वां गणतंत्र दिवस। संविधान व देश की एकता का राष्ट्रीय उत्सव।", "🇮🇳"),
        "2027-02-11" to FestivalInfo("", "बसंत पंचमी (सरस्वती पूजा)", "मुख्य त्योहार", "माघ शुक्ल पंचमी", "विद्या, ज्ञान, संगीत और कला की देवी मां सरस्वती की वंदना व बसंत ऋतु का आगमन।", "🌼"),
        "2027-03-06" to FestivalInfo("", "महाशिवरात्रि महापर्व", "महापर्व", "फाल्गुन कृष्ण चतुर्दशी", "भगवान भोलेनाथ और माता पार्वती का महाकल्याणकारी विवाह दिवस। चार प्रहर की महापूजा व रुद्राभिषेक।", "🔱"),
        "2027-03-22" to FestivalInfo("", "होलिका दहन", "मुख्य त्योहार", "फाल्गुन पूर्णिमा", "भक्त प्रह्लाद की रक्षा व असत्य पर सत्य की विजय का प्रतीक होलिका दहन।", "🔥"),
        "2027-03-23" to FestivalInfo("", "धुलंडी (रंगों की होली)", "महापर्व", "चैत्र कृष्ण प्रतिपदा", "उमंग, भाईचारे और रंगों का महापर्व होली। भाटी परिवार में आपसी प्रेम और सद्भाव।", "🎨")
    )

    // Regular Annual National and Fixed Festivals
    private val fixedFestivals = mapOf(
        "01-01" to FestivalInfo("", "नव वर्ष (New Year)", "उत्सव", "1 जनवरी", "ईस्वी नव वर्ष का शुभारंभ। सुख, समृद्धि और पारिवारिक उन्नति की मंगलकामनाएं।", "🎉"),
        "01-14" to FestivalInfo("", "मकर संक्रांति", "महापर्व", "सूर्य उत्तरायण", "सूर्य देव का मकर राशि में प्रवेश। दान-पुण्य, खिचड़ी व तिल-गुड़ का पर्व।", "🪁"),
        "01-26" to FestivalInfo("", "गणतंत्र दिवस", "राष्ट्रीय पर्व", "26 जनवरी", "भारत का गणतंत्र दिवस। तिरंगे की शान और अमर शहीदों को नमन।", "🇮🇳"),
        "08-15" to FestivalInfo("", "स्वतंत्रता दिवस", "राष्ट्रीय पर्व", "15 अगस्त", "भारत का स्वतंत्रता दिवस। राष्ट्र की अखंडता व स्वाधीनता का राष्ट्रीय गौरव पर्व।", "🇮🇳"),
        "10-02" to FestivalInfo("", "गांधी जयंती व शास्त्री जयंती", "राष्ट्रीय पर्व", "2 अक्टूबर", "राष्ट्रपिता महात्मा गांधी व सादगी की मिसाल लाल बहादुर शास्त्री जी की पावन जयंती।", "🕊️")
    )

    /**
     * Checks if today is any festival or religious event.
     */
    fun getTodayFestival(targetAmavasyaMillis: Long): FestivalInfo? {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return if (hasSpecialEvent(year, month, day, targetAmavasyaMillis)) {
            getFestivalInfo(year, month, day, targetAmavasyaMillis)
        } else {
            null
        }
    }

    /**
     * True if festival is of major cultural importance (Diwali, Holi, Navratri, etc.)
     */
    fun isMajorFestival(info: FestivalInfo): Boolean {
        return info.category.contains("महापर्व") ||
               info.category.contains("मुख्य त्योहार") ||
               info.name.contains("दीपावली") ||
               info.name.contains("होली") ||
               info.name.contains("दशहरा") ||
               info.name.contains("नवरात्रि") ||
               info.name.contains("शिवरात्रि") ||
               info.name.contains("छठ") ||
               info.name.contains("धनतेरस") ||
               info.name.contains("अमावस्या")
    }

    /**
     * Heartfelt morning festival greeting wishes message.
     */
    fun getFestivalGreeting(info: FestivalInfo): String {
        return when {
            info.name.contains("दीपावली") ->
                "🚩 युवा शक्ति ग्रुप, भाटी परिवार कुचेरा की ओर से दीपों के महापर्व दीपावली की हार्दिक शुभकामनाएं! माता महालक्ष्मी व भगवान श्री गणेश जी की कृपा से आपका जीवन सुख, शांति, समृद्धि और उत्तम स्वास्थ्य से परिपूर्ण रहे।"
            info.name.contains("होली") ->
                "🎨 रंगों के पावन महापर्व होली की हार्दिक शुभकामनाएं! भाटी परिवार के सभी सदस्यों के जीवन में आपसी प्रेम, सौहार्द और खुशियों के अनंत रंग बरसें।"
            info.name.contains("दशहरा") || info.name.contains("विजयादशमी") ->
                "🏹 विजयादशमी (दशहरा) महापर्व की हार्दिक बधाई! बुराई पर अच्छाई और असत्य पर सत्य की विजय का यह पावन पर्व आप सभी के जीवन में नई ऊर्जा और सफलता लाए।"
            info.name.contains("नवरात्रि") ->
                "🔱 शारदीय नवरात्रि के पावन पर्व की मंगलकामनाएं! मां दुर्गा की असीम कृपा पूरे भाटी परिवार पर बनी रहे।"
            info.name.contains("धनतेरस") ->
                "🪙 पावन धनतेरस पर्व की बहुत-बहुत बधाई! भगवान धन्वंतरि व कुबेर देव आपको आरोग्य, धन और वैभव प्रदान करें।"
            info.name.contains("शिवरात्रि") ->
                "🔱 महाशिवरात्रि महापर्व की हार्दिक शुभकामनाएं! देवाधिदेव महादेव व माता पार्वती की कृपा से समस्त कष्टों का निवारण हो।"
            info.name.contains("अमावस्या") ->
                "🌑 मासिक अमावस्या बैठक व अंशदान का पावन दिवस। भाटी परिवार की एकता और प्रगति हेतु परस्पर सहयोग का संकल्प।"
            else ->
                "✨ आज ${info.name} का पावन पर्व है! युवा शक्ति ग्रुप, भाटी परिवार कुचेरा की ओर से मंगलमय शुभकामनाएं। आपका दिन शुभ और सुखमय हो।"
        }
    }
}
