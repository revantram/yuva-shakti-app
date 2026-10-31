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

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFDE68A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_thought_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFDF5), Color(0xFFFEF3C7))
                    )
                )
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFFDE68A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✨", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isEnglish) "Today's Inspiration" else "आज का दैनिक सुविचार",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = if (isEnglish) "Daily Motivation for Family" else "प्रेरणा व सकारात्मक विचार • भाटी परिवार",
                            fontSize = 10.5.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Send notification button
                    Surface(
                        color = Color(0xFFFEF9C3),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE047)),
                        modifier = Modifier.clickable { onNotifyClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Notify",
                                tint = Color(0xFF854D0E),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "अलर्ट",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF854D0E)
                            )
                        }
                    }

                    // Share button
                    Surface(
                        color = Color(0xFF059669),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable {
                            DailyThoughtHelper.shareThought(context, thought, isEnglish)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "शेयर",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thought Text
            Text(
                text = "❝ ${thought.hindi} ❞",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF451A03),
                lineHeight = 19.sp,
                textAlign = TextAlign.Start
            )

            if (isEnglish) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "❝ ${thought.english} ❞",
                    fontSize = 11.5.sp,
                    color = Color(0xFF78350F),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
