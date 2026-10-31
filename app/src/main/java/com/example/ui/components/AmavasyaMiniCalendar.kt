package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SaffronGold
import com.example.util.AmavasyaCalculator
import com.example.util.FestivalHelper
import java.util.Calendar

@Composable
fun AmavasyaMiniCalendar(
    targetAmavasyaMillis: Long,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val borderClr = if (isDark) Color(0xFF334155) else BorderSlate
    val navIconTint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)

    val todayCal = remember { Calendar.getInstance() }
    val todayYear = todayCal.get(Calendar.YEAR)
    val todayMonth = todayCal.get(Calendar.MONTH)
    val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

    var displayYear by remember { mutableIntStateOf(todayYear) }
    var displayMonth by remember { mutableIntStateOf(todayMonth) }
    var selectedDay by remember { mutableStateOf<Int?>(todayDay) }

    // Target Amavasya Calendar
    val targetAmavasyaCal = remember(targetAmavasyaMillis) {
        Calendar.getInstance().apply { timeInMillis = targetAmavasyaMillis }
    }
    val amavasyaYear = targetAmavasyaCal.get(Calendar.YEAR)
    val amavasyaMonth = targetAmavasyaCal.get(Calendar.MONTH)
    val amavasyaDay = targetAmavasyaCal.get(Calendar.DAY_OF_MONTH)

    val monthNamesHindi = listOf(
        "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून",
        "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर"
    )

    val daysOfWeekHindi = listOf("रवि", "सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि")

    // Calculate grid for displayMonth & displayYear
    val cal = remember(displayYear, displayMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, displayYear)
            set(Calendar.MONTH, displayMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // Sunday = 1, Saturday = 7

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderClr, RoundedCornerShape(18.dp))
            .testTag("amavasya_mini_calendar")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Month Header with navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = if (isDark) SaffronGold else DeepNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${monthNamesHindi[displayMonth]} $displayYear",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Jump to today button
                    if (displayYear != todayYear || displayMonth != todayMonth) {
                        Surface(
                            color = if (isDark) Color(0xFF334155) else Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable {
                                    displayYear = todayYear
                                    displayMonth = todayMonth
                                    selectedDay = todayDay
                                }
                                .padding(end = 4.dp)
                        ) {
                            Text(
                                text = "आज",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) SaffronGold else DeepNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (displayMonth == 0) {
                                displayMonth = 11
                                displayYear -= 1
                            } else {
                                displayMonth -= 1
                            }
                            selectedDay = null
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = navIconTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (displayMonth == 11) {
                                displayMonth = 0
                                displayYear += 1
                            } else {
                                displayMonth += 1
                            }
                            selectedDay = null
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = navIconTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day of week labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeekHindi.forEachIndexed { index, dayName ->
                    Text(
                        text = dayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 0) Color(0xFFEF4444) else textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Days grid (up to 6 rows)
            val totalCells = ((firstDayOfWeek - 1) + daysInMonth)
            val numRows = (totalCells + 6) / 7

            for (row in 0 until numRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNum = cellIndex - (firstDayOfWeek - 1) + 1

                        if (dayNum in 1..daysInMonth) {
                            val isToday = (displayYear == todayYear && displayMonth == todayMonth && dayNum == todayDay)
                            val isAmavasya = (displayYear == amavasyaYear && displayMonth == amavasyaMonth && dayNum == amavasyaDay)
                            val isSelected = selectedDay == dayNum
                            val hasEvent = FestivalHelper.hasSpecialEvent(displayYear, displayMonth, dayNum, targetAmavasyaMillis)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .padding(2.dp)
                                    .clickable { selectedDay = dayNum },
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isAmavasya -> {
                                        // Auspicious glowing Amavasya indicator
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(Color(0xFF1E1B4B), CircleShape)
                                                .border(2.dp, SaffronGold, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$dayNum",
                                                    color = Color(0xFFFEF3C7),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                                Text(
                                                    text = "🌑",
                                                    fontSize = 7.sp
                                                )
                                            }
                                        }
                                    }
                                    isToday -> {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(Color(0xFF3B82F6), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$dayNum",
                                                    color = Color.White,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (hasEvent) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(SaffronGold, CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    isSelected -> {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(if (isDark) Color(0xFF334155) else Color(0xFFEEF2FF), CircleShape)
                                                .border(1.5.dp, if (isDark) SaffronGold else DeepNavy, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$dayNum",
                                                    color = if (isDark) Color(0xFFFEF08A) else DeepNavy,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (hasEvent) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(Color(0xFFEA580C), CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                color = if (col == 0) Color(0xFFEF4444) else textPrimary,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            )
                                            if (hasEvent) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(if (isDark) SaffronGold else Color(0xFFD97706), CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Festival / Tithi Details Card when a day is tapped
            selectedDay?.let { selDay ->
                val festival = FestivalHelper.getFestivalInfo(displayYear, displayMonth, selDay, targetAmavasyaMillis)
                val isAmavasyaSelected = (displayYear == amavasyaYear && displayMonth == amavasyaMonth && selDay == amavasyaDay)

                Surface(
                    color = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAmavasyaSelected) SaffronGold else if (isDark) Color(0xFF334155) else Color(0xFFFDE68A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = festival.icon,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Column {
                                    Text(
                                        text = festival.name,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFFDE047) else Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "${festival.dateString} • ${festival.tithi}",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFFB45309)
                                    )
                                }
                            }

                            Surface(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = festival.category,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFDE047) else Color(0xFF78350F),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = festival.description,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF451A03),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Monthly Amavasya Meeting & Tithi Status Banner
            Surface(
                color = if (isDark) Color(0xFF1E1B4B) else Color(0xFFFEF3C7),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) SaffronGold else Color(0xFFFCD34D)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌑 ", fontSize = 13.sp)
                        Column {
                            Text(
                                text = "इस माह की अमावस्या: $amavasyaDay ${monthNamesHindi[amavasyaMonth]}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFFDE047) else Color(0xFF78350F)
                            )
                            Text(
                                text = "मासिक अमावस्या बैठक एवं परस्पर सहयोग का दिन",
                                fontSize = 10.sp,
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF92400E)
                            )
                        }
                    }

                    Surface(
                        color = SaffronGold,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = AmavasyaCalculator.getHindiTithiName(targetAmavasyaMillis),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF451A03),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
