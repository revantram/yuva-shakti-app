package com.example.ui.components

import android.content.Context
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SaffronGold
import com.example.util.DailyThought
import com.example.util.DailyThoughtHelper

@Composable
fun DailyThoughtCard(
    thought: DailyThought,
    isEnglish: Boolean = false,
    onNotifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    val cardBgGradient = if (isDark) {
        listOf(Color(0xCC1E1B4B), Color(0xCC0F172A))
    } else {
        listOf(Color(0xD9FFFDF5), Color(0xD9FEF3C7))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xCC1E1B4B) else Color(0xD9FFFBEB)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SaffronGold),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_thought_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = cardBgGradient))
                .padding(16.dp)
        ) {
            // Header Row: Sunrise Icon & Morning Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(if (isDark) Color(0xFF312E81) else Color(0xFFFDE68A), CircleShape)
                            .border(1.5.dp, SaffronGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌅", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isEnglish) "Today's Inspiration" else "🌅 आज का सुविचार • सुप्रभात",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFDE047) else Color(0xFF78350F)
                        )
                        Text(
                            text = if (isEnglish) "Daily Motivation for Family" else "प्रेरणा, एकता व संस्कार • भाटी परिवार",
                            fontSize = 11.5.sp,
                            color = if (isDark) Color(0xFFFEF08A) else Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Header Badge
                Surface(
                    color = if (isDark) Color(0xFF334155) else Color(0xFFFEF08A),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) SaffronGold else Color(0xFFF59E0B))
                ) {
                    Text(
                        text = "दैनिक विचार",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFFDE047) else Color(0xFF78350F),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Thought Text - Large, Prominent and Clear
            Surface(
                color = if (isDark) Color(0x660F172A) else Color.White.copy(alpha = 0.85f),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "❝ ${thought.hindi} ❞",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B),
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start
                    )

                    if (isEnglish) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "❝ ${thought.english} ❞",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— युवा शक्ति ग्रुप भाटी परिवार कुचेरा",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronGold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: WhatsApp Share & Notification Alert
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp Share Button
                Surface(
                    color = Color(0xFF16A34A),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            DailyThoughtHelper.shareThought(context, thought, isEnglish)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "व्हाट्सएप शेयर करें",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Alert / Notification Button
                Surface(
                    color = DeepNavy,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNotifyClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Notify",
                            tint = SaffronGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "सूचना भेजें",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
