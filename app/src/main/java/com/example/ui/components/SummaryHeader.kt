package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainUiState
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenLight
import com.example.ui.theme.PendingRed
import com.example.ui.theme.PendingRedLight
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronLight

@Composable
fun AppHeader(
    isAdmin: Boolean,
    countdown: com.example.util.CountdownInfo,
    unreadNotificationCount: Int = 0,
    onlineMemberCount: Int = 1,
    onAuthClick: () -> Unit,
    onDateSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onUrgentBroadcastClick: () -> Unit = {},
    onOnlineMembersClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = DeepNavy,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🚩 ",
                            fontSize = 18.sp
                        )
                        Text(
                            text = "युवा शक्ति ग्रुप",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "भाटी परिवार • मासिक अमावस्या फंड",
                            color = Color(0xFFC7D2FE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        // Online presence chip
                        Surface(
                            color = Color(0xFF064E3B),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .clickable { onOnlineMembersClick() }
                                .testTag("chip_online_count")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF34D399), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$onlineMemberCount ऑनलाइन",
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Direct QR Code Payment Button
                    Surface(
                        color = Color(0xFF047857),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable { onPaymentClick() }
                            .testTag("btn_header_pay")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📷 QR पे",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Admin Urgent Notice Broadcast Button
                    if (isAdmin) {
                        Surface(
                            color = Color(0xFFDC2626),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { onUrgentBroadcastClick() }
                                .testTag("btn_header_urgent_broadcast")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🚨 अलर्ट",
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Settings Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF312E81), CircleShape)
                            .clickable { onSettingsClick() }
                            .testTag("btn_header_settings"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Notification Bell with Badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF312E81), CircleShape)
                            .clickable { onNotificationsClick() }
                            .testTag("btn_notifications_bell"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Notifications",
                            tint = if (unreadNotificationCount > 0) SaffronGold else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(14.dp)
                                    .background(PendingRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Role Badge
                    Surface(
                        color = if (isAdmin) SaffronGold else Color(0xFF312E81),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .testTag("role_badge")
                            .clickable { onAuthClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isAdmin) {
                                Text(
                                    text = "एडमिन",
                                    color = Color(0xFF451A03),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black
                                )
                            } else {
                                Text(
                                    text = "दर्शक",
                                    color = Color(0xFFE0E7FF),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amavasya Countdown Timer
            HeaderCountdownBanner(
                countdown = countdown,
                isAdmin = isAdmin,
                onDateSettingsClick = onDateSettingsClick
            )
        }
    }
}

@Composable
fun HeaderCountdownBanner(
    countdown: com.example.util.CountdownInfo,
    isAdmin: Boolean,
    onDateSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("header_countdown_banner")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(text = "🌑 ", fontSize = 13.sp)
                    Text(
                        text = "अगली अमावस्या: ",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDE68A)
                    )
                    Text(
                        text = countdown.formattedDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Surface(
                    color = Color(0xFF312E81),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = countdown.hindiTithiName,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live 4-box Countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CountdownTimeBlock(
                    value = countdown.days.toString().padStart(2, '0'),
                    unit = "दिन",
                    modifier = Modifier.weight(1f)
                )
                CountdownTimeBlock(
                    value = countdown.hours.toString().padStart(2, '0'),
                    unit = "घंटे",
                    modifier = Modifier.weight(1f)
                )
                CountdownTimeBlock(
                    value = countdown.minutes.toString().padStart(2, '0'),
                    unit = "मिनट",
                    modifier = Modifier.weight(1f)
                )
                CountdownTimeBlock(
                    value = countdown.seconds.toString().padStart(2, '0'),
                    unit = "सेकंड",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusText = when {
                    countdown.isToday -> "🎉 आज अमावस्या बैठक व अंशदान का दिन है!"
                    countdown.days in 1..3 -> "🔔 अग्रिम सूचना: केवल ${countdown.days} दिन शेष!"
                    countdown.isPast -> "अमावस्या संपन्न • प्रतिपदा (बकाया अंशदान पूरा करें)"
                    else -> "अंशदान चक्र में ${countdown.days} दिन शेष"
                }

                val statusColor = when {
                    countdown.isToday -> Color(0xFF34D399)
                    countdown.days in 1..3 -> Color(0xFFFBBF24)
                    else -> Color(0xFF94A3B8)
                }

                Text(
                    text = statusText,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )

                if (isAdmin) {
                    Text(
                        text = "तिथि बदलें ✎",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDE68A),
                        modifier = Modifier
                            .clickable { onDateSettingsClick() }
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CountdownTimeBlock(
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF38BDF8),
                letterSpacing = 0.5.sp
            )
            Text(
                text = unit,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun AmavasyaBannerCard(
    uiState: MainUiState,
    onBannerStateCycle: (String) -> Unit,
    onEditCycleName: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val (bgColor, borderColor, icon, iconColor, title, description) = when (uiState.amavasyaBannerState) {
        "day_after" -> {
            BannerConfig(
                bg = Color(0xFFFEF3C7),
                border = Color(0xFFFCD34D),
                icon = Icons.Default.Campaign,
                iconColor = Color(0xFFB45309),
                title = "अमावस्या के दूसरे दिन की रिपोर्ट (प्रतिपदा)",
                desc = "अमावस्या चक्र संपन्न हो चुका है। नीचे दिए गए सदस्यों में से जिनका बकाया है, कृपया जल्द से जल्द अपना अंशदान पूरा करें।"
            )
        }
        "advance" -> {
            BannerConfig(
                bg = Color(0xFFEFF6FF),
                border = Color(0xFFBFDBFE),
                icon = Icons.Default.NotificationsActive,
                iconColor = Color(0xFF1D4ED8),
                title = "अग्रिम सूचना (अमावस्या में 3-4 दिन शेष)",
                desc = "सभी सदस्यों से सादर अनुरोध है कि तय सहमति अनुसार अपनी मासिक सहयोग राशि समय पर जमा कराने की तैयारी रखें।"
            )
        }
        else -> {
            BannerConfig(
                bg = Color(0xFFEEF2FF),
                border = Color(0xFFC7D2FE),
                icon = Icons.Default.Campaign,
                iconColor = Color(0xFF4338CA),
                title = "मासिक अमावस्या बैठक चक्र",
                desc = "पारदर्शी बहीखाता: केवल अधिकृत व्यवस्थापक द्वारा वेरिफाइड प्रविष्टियाँ ही यहाँ दिखाई देती हैं।"
            )
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .testTag("amavasya_banner")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Notification",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            color = Color(0xFF1E293B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (uiState.isAdmin) {
                            IconButton(
                                onClick = {
                                    val nextState = when (uiState.amavasyaBannerState) {
                                        "day_after" -> "advance"
                                        "advance" -> "regular"
                                        else -> "day_after"
                                    }
                                    onBannerStateCycle(nextState)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Switch alert state",
                                    tint = iconColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = description,
                        color = Color(0xFF334155),
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📅 ${uiState.amavasyaCycleName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = iconColor
                        )

                        if (uiState.isAdmin) {
                            Text(
                                text = "नाम बदलें ✎",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = iconColor,
                                modifier = Modifier
                                    .clickable { onEditCycleName() }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class BannerConfig(
    val bg: Color,
    val border: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val title: String,
    val desc: String
)

@Composable
fun FundProgressCard(
    uiState: MainUiState,
    onAddExpenseClick: () -> Unit = {},
    onSetTargetClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSlate, RoundedCornerShape(20.dp))
            .testTag("fund_progress_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Consensus note banner if present
            if (uiState.consensusNote.isNotBlank()) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📢 ", fontSize = 13.sp)
                        Column {
                            Text(
                                text = "सर्वसम्मति निर्णय:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = uiState.consensusNote,
                                fontSize = 11.5.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "कुल एकत्रित राशि",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "₹${uiState.totalCollected.formatAmount()}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = PaidGreen,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { onSetTargetClick() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "कुल लक्ष्य",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        if (uiState.isAdmin) {
                            Text(
                                text = " ✎",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4338CA)
                            )
                        }
                    }
                    Text(
                        text = if (uiState.targetAmount > 0) "₹${uiState.targetAmount.formatAmount()}" else "अनिर्धारित (सहमति अनुसार)",
                        fontSize = if (uiState.targetAmount > 0) 18.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.targetAmount > 0) "मासिक फंड संकलन प्रगति (लक्ष्य: ₹${uiState.targetAmount})" else "मासिक फंड संकलन प्रगति",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )

                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${uiState.progressPercent}%",
                        color = Color(0xFF4338CA),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            val animatedProgress by animateFloatAsState(
                targetValue = if (uiState.targetAmount > 0) (uiState.totalCollected.toFloat() / uiState.targetAmount).coerceIn(0f, 1f) else 0f,
                label = "progress"
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Color(0xFF4F46E5),
                trackColor = Color(0xFFF1F5F9)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Stat Counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatPill(
                    label = "कुल सदस्य",
                    value = uiState.totalMembers.toString(),
                    textColor = Color(0xFF0F172A),
                    bgColor = Color(0xFFF8FAFC),
                    borderColor = Color(0xFFE2E8F0),
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    label = "जमा (Paid)",
                    value = uiState.paidMembers.toString(),
                    textColor = PaidGreen,
                    bgColor = PaidGreenLight,
                    borderColor = Color(0xFFA7F3D0),
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    label = "बाकी (Pending)",
                    value = uiState.pendingMembers.toString(),
                    textColor = PendingRed,
                    bgColor = PendingRedLight,
                    borderColor = Color(0xFFFECDD3),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    textColor: Color,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, borderColor, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.85f)
            )
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = textColor,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun WhatsAppShareCard(
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF047857)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("whatsapp_share_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF059669), Color(0xFF0F766E))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "व्हाट्सएप रिपोर्ट शेयर करें",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Paid/Pending लिस्ट एक क्लिक में ग्रुप में भेजें",
                    color = Color(0xFFD1FAE5),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .testTag("btn_share_whatsapp")
                    .clickable { onShareClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💬 ",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "शेयर करें",
                        color = Color(0xFF065F46),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AdminControlBar(
    pendingPaymentRequestCount: Int = 0,
    onAddMember: () -> Unit,
    onSetCycleTarget: () -> Unit = {},
    onViewPaymentRequests: () -> Unit = {},
    onUrgentBroadcast: () -> Unit = {},
    onResetCycle: () -> Unit,
    onViewHistory: () -> Unit,
    onChangePin: () -> Unit,
    onOpenAppUpdateConsole: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
            .testTag("admin_control_panel")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🛡️ ", fontSize = 13.sp)
                    Text(
                        text = "एडमिन कंट्रोल पैनल",
                        color = Color(0xFF78350F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🚀 अपडेट/कोड",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8),
                        modifier = Modifier
                            .clickable { onOpenAppUpdateConsole() }
                            .padding(2.dp)
                    )
                    Text(
                        text = "इतिहास",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier
                            .clickable { onViewHistory() }
                            .padding(2.dp)
                    )
                    Text(
                        text = "पिन बदलें",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier
                            .clickable { onChangePin() }
                            .padding(2.dp)
                    )
                    Text(
                        text = "रीसेट",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626),
                        modifier = Modifier
                            .clickable { onResetCycle() }
                            .padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: Cycle Target & Payment Requests
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Set Cycle Target & Consensus Amount
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSetCycleTarget() }
                        .testTag("btn_admin_set_target")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎯 ", fontSize = 12.sp)
                        Text(
                            text = "लक्ष्य व अंशदान तय करें",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                // Payment Verification Requests with badge
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onViewPaymentRequests() }
                        .testTag("btn_admin_view_payment_requests")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📩 ", fontSize = 12.sp)
                        Text(
                            text = "पेमेंट सत्यापन" + if (pendingPaymentRequestCount > 0) " ($pendingPaymentRequestCount)" else "",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingPaymentRequestCount > 0) Color(0xFFB45309) else Color(0xFF1D4ED8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Member Button
            Surface(
                color = Color(0xFF4338CA),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_add_member")
                    .clickable { onAddMember() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 9.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add member",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "नया सदस्य जोड़ें",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun Int.formatAmount(): String {
    return java.text.NumberFormat.getIntegerInstance(java.util.Locale("en", "IN")).format(this)
}
