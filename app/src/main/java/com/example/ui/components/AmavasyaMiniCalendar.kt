package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.SaffronGold
import com.example.util.AmavasyaCalculator
import java.util.Calendar

@Composable
fun AmavasyaMiniCalendar(
    targetAmavasyaMillis: Long,
    modifier: Modifier = Modifier
) {
    val todayCal = remember { Calendar.getInstance() }
    val todayYear = todayCal.get(Calendar.YEAR)
    val todayMonth = todayCal.get(Calendar.MONTH)
    val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

    var displayYear by remember { mutableIntStateOf(todayYear) }
    var displayMonth by remember { mutableIntStateOf(todayMonth) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSlate, RoundedCornerShape(18.dp))
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
                        tint = DeepNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${monthNamesHindi[displayMonth]} $displayYear",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Jump to today if not in today's month
                    if (displayYear != todayYear || displayMonth != todayMonth) {
                        Surface(
                            color = Color(0xFFEEF2FF),
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
                                color = DeepNavy,
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
                            tint = Color(0xFF475569),
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
                            tint = Color(0xFF475569),
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
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 0) Color(0xFFDC2626) else Color(0xFF64748B),
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

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .padding(2.dp)
                                    .clickable { selectedDay = dayNum },
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isAmavasya -> {
                                        // Auspicious glowing Amavasya indicator
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
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
                                                .size(30.dp)
                                                .background(Color(0xFF3B82F6), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                color = Color.White,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    isSelected -> {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(Color(0xFFEEF2FF), CircleShape)
                                                .border(1.5.dp, DeepNavy, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                color = DeepNavy,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = "$dayNum",
                                            color = if (col == 0) Color(0xFFDC2626) else Color(0xFF1E293B),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Normal,
                                            textAlign = TextAlign.Center
                                        )
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

            // Calendar Legend & Amavasya Quick Reminder Banner
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
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
                                color = Color(0xFF78350F)
                            )
                            Text(
                                text = "मासिक अमावस्या बैठक एवं परस्पर सहयोग का दिन",
                                fontSize = 10.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Surface(
                        color = SaffronGold,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${AmavasyaCalculator.getHindiTithiName(targetAmavasyaMillis)}",
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
