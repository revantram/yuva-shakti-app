package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object AmavasyaCalculator {
    // Reference New Moon: Jan 11, 2024, 11:57 UTC
    private const val REF_NEW_MOON_MILLIS = 1704974220000L
    // Mean synodic month: 29.530588853 days in milliseconds
    private const val SYNODIC_MONTH_MILLIS = 2551442880.0

    private val HINDI_MONTHS = listOf(
        "पौष", "माघ", "फाल्गुन", "चैत्र", "वैशाख", "ज्येष्ठ",
        "आषाढ़", "श्रावण", "भाद्रपद", "आश्विन", "कार्तिक", "मार्गशीर्ष"
    )

    private val HINDI_DAYS = mapOf(
        Calendar.SUNDAY to "रविवार",
        Calendar.MONDAY to "सोमवार",
        Calendar.TUESDAY to "मंगलवार",
        Calendar.WEDNESDAY to "बुधवार",
        Calendar.THURSDAY to "गुरुवार",
        Calendar.FRIDAY to "शुक्रवार",
        Calendar.SATURDAY to "शनिवार"
    )

    private val HINDI_MONTH_NAMES = mapOf(
        Calendar.JANUARY to "जनवरी",
        Calendar.FEBRUARY to "फ़रवरी",
        Calendar.MARCH to "मार्च",
        Calendar.APRIL to "अप्रैल",
        Calendar.MAY to "मई",
        Calendar.JUNE to "जून",
        Calendar.JULY to "जुलाई",
        Calendar.AUGUST to "अगस्त",
        Calendar.SEPTEMBER to "सितंबर",
        Calendar.OCTOBER to "अक्टूबर",
        Calendar.NOVEMBER to "नवंबर",
        Calendar.DECEMBER to "दिसंबर"
    )

    // Accurate Hindu Panchang Amavasya Dates (Year, Month, Day)
    private val KNOWN_AMAVASYA_DATES = listOf(
        Triple(2026, Calendar.OCTOBER, 10), // आश्विन अमावस्या
        Triple(2026, Calendar.NOVEMBER, 8),  // कार्तिक अमावस्या (दीपावली - 8 नवंबर 2026)
        Triple(2026, Calendar.DECEMBER, 8),  // मार्गशीर्ष अमावस्या
        Triple(2027, Calendar.JANUARY, 7),
        Triple(2027, Calendar.FEBRUARY, 6),
        Triple(2027, Calendar.MARCH, 8),
        Triple(2027, Calendar.APRIL, 6),
        Triple(2027, Calendar.MAY, 6),
        Triple(2027, Calendar.JUNE, 4),
        Triple(2027, Calendar.JULY, 4),
        Triple(2027, Calendar.AUGUST, 2),
        Triple(2027, Calendar.AUGUST, 31),
        Triple(2027, Calendar.SEPTEMBER, 30),
        Triple(2027, Calendar.OCTOBER, 29),  // 2027 दीपावली
        Triple(2027, Calendar.NOVEMBER, 28),
        Triple(2027, Calendar.DECEMBER, 28)
    )

    /**
     * Calculates the next upcoming Amavasya date after currentTimeMillis.
     * Sets target time to 09:00 AM on that day (typical meeting/contribution time).
     */
    fun calculateNextAmavasyaMillis(fromMillis: Long = System.currentTimeMillis()): Long {
        for ((year, month, day) in KNOWN_AMAVASYA_DATES) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (cal.timeInMillis >= fromMillis - (18 * 3600 * 1000L)) {
                return cal.timeInMillis
            }
        }

        val diff = fromMillis - REF_NEW_MOON_MILLIS
        val cycles = Math.floor(diff / SYNODIC_MONTH_MILLIS)
        var nextCycleTime = REF_NEW_MOON_MILLIS + ((cycles + 1) * SYNODIC_MONTH_MILLIS).toLong()

        // Normalize to local calendar day at 09:00 AM
        val cal = Calendar.getInstance().apply {
            timeInMillis = nextCycleTime
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If normalized time already passed today by more than 18 hours, jump to next cycle
        if (cal.timeInMillis < fromMillis - (18 * 3600 * 1000L)) {
            val nextCal = Calendar.getInstance().apply {
                timeInMillis = (nextCycleTime + SYNODIC_MONTH_MILLIS).toLong()
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return nextCal.timeInMillis
        }

        return cal.timeInMillis
    }

    /**
     * Formats milliseconds into Hindi date string: e.g. "08 नवंबर 2026 (रविवार)"
     */
    fun formatHindiDate(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val monthStr = HINDI_MONTH_NAMES[cal.get(Calendar.MONTH)] ?: "माह"
        val year = cal.get(Calendar.YEAR)
        val dayOfWeek = HINDI_DAYS[cal.get(Calendar.DAY_OF_WEEK)] ?: ""

        val formattedDay = if (day < 10) "0$day" else "$day"
        return "$formattedDay $monthStr $year (${dayOfWeek})"
    }

    /**
     * Estimates Hindi lunar month name (e.g. "कार्तिक अमावस्या (दीपावली)", "मार्गशीर्ष अमावस्या")
     */
    fun getHindiTithiName(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        if (year == 2026 && month == Calendar.NOVEMBER && day == 8) {
            return "कार्तिक अमावस्या (दीपावली)"
        }

        val gregorianMonth = cal.get(Calendar.MONTH) // 0-based
        val hindiMonth = HINDI_MONTHS[gregorianMonth % HINDI_MONTHS.size]
        return "$hindiMonth अमावस्या"
    }

    /**
     * Computes countdown details between now and targetMillis.
     */
    fun computeCountdown(
        targetMillis: Long,
        currentMillis: Long = System.currentTimeMillis(),
        isCustom: Boolean = false
    ): CountdownInfo {
        val remainingMillis = targetMillis - currentMillis

        val calNow = Calendar.getInstance().apply { timeInMillis = currentMillis }
        val calTarget = Calendar.getInstance().apply { timeInMillis = targetMillis }

        val isSameDay = calNow.get(Calendar.YEAR) == calTarget.get(Calendar.YEAR) &&
                calNow.get(Calendar.DAY_OF_YEAR) == calTarget.get(Calendar.DAY_OF_YEAR)

        if (remainingMillis <= 0) {
            val elapsedMillis = -remainingMillis
            val elapsedDays = elapsedMillis / (1000 * 60 * 60 * 24)

            return CountdownInfo(
                targetMillis = targetMillis,
                formattedDate = formatHindiDate(targetMillis),
                hindiTithiName = getHindiTithiName(targetMillis),
                days = 0,
                hours = 0,
                minutes = 0,
                seconds = 0,
                isToday = isSameDay,
                isPast = true,
                isCustomDate = isCustom
            )
        }

        val totalSeconds = remainingMillis / 1000
        val days = totalSeconds / (3600 * 24)
        val hours = (totalSeconds % (3600 * 24)) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return CountdownInfo(
            targetMillis = targetMillis,
            formattedDate = formatHindiDate(targetMillis),
            hindiTithiName = getHindiTithiName(targetMillis),
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            isToday = isSameDay,
            isPast = false,
            isCustomDate = isCustom
        )
    }
}

data class CountdownInfo(
    val targetMillis: Long,
    val formattedDate: String,
    val hindiTithiName: String,
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val isToday: Boolean,
    val isPast: Boolean,
    val isCustomDate: Boolean
)
