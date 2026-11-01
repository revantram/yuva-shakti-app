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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.SaffronLight

@Composable
fun AppHeader(
    isAdmin: Boolean,
    totalCollected: Int = 0,
    countdown: com.example.util.CountdownInfo? = null,
    countdownFlow: kotlinx.coroutines.flow.StateFlow<com.example.util.CountdownInfo>? = null,
    unreadNotificationCount: Int = 0,
    onlineMemberCount: Int = 1,
    onAuthClick: () -> Unit,
    onDateSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onUrgentBroadcastClick: () -> Unit = {},
    onOnlineMembersClick: () -> Unit = {},
    onSirenTgadaClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeCountdown = if (countdownFlow != null) {
        countdownFlow.collectAsStateWithLifecycle().value
    } else {
        countdown ?: com.example.util.CountdownInfo()
    }
    Surface(
        color = Color(0xF8FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5EA)),
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // ── ROW 1: Branding (Logo + Group Name + Subtitle + Live Chip) & Top Actions (Bell + Admin) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Logo with golden ring
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(2.dp, SaffronGold, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        val headerLogoPainter = runCatching {
                            androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_app_foreground_1793369456038)
                        }.getOrNull()
                        if (headerLogoPainter != null) {
                            androidx.compose.foundation.Image(
                                painter = headerLogoPainter,
                                contentDescription = "युवा शक्ति ग्रुप लोगो",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(text = "🚩", fontSize = 24.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(9.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🚩 युवा शक्ति ग्रुप",
                            color = Color(0xFF1C1C1E),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp,
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Text(
                                text = "भाटी परिवार कुचेरा",
                                color = SaffronGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            // Live online presence chip (compact pill)
                            Surface(
                                color = Color(0xFFE8F9ED),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E)),
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
                                            .background(Color(0xFF22C55E), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "$onlineMemberCount ऑनलाइन",
                                        color = Color(0xFF166534),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Right Utility Icons: Notification Bell & Admin Lock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Notification Bell with Badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                            .clickable { onNotificationsClick() }
                            .testTag("btn_notifications_bell"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Notifications",
                            tint = if (unreadNotificationCount > 0) SaffronGold else Color(0xFF334155),
                            modifier = Modifier.size(19.dp)
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

                    // Settings Quick Button in Top Row (direct access to Settings Dialog)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                            .clickable { onSettingsClick() }
                            .testTag("btn_header_top_settings"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── ROW 2: Primary Financial Action Cards (50% / 50% Balanced Grid) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Card 1: कुल जमा राशि (Deposit Box)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onPaymentClick() }
                        .testTag("btn_header_pay_circle")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 9.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFDCFCE7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💰", fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "कुल जमा फंड",
                                color = Color(0xFF166534),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                            Text(
                                text = "₹${totalCollected.formatAmount()}",
                                color = Color(0xFF047857),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "खाता ↗",
                            color = Color(0xFF15803D),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Card 2: PhonePe ऑनलाइन भुगतान (UPI Online Payment)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFAF5FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD8B4FE)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onPaymentClick() }
                        .testTag("btn_header_phonepe_pay")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 9.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF3E8FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📱", fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PhonePe पे",
                                color = Color(0xFF581C87),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                            Text(
                                text = "सीधा भुगतान",
                                color = Color(0xFF6B21A8),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "पे ↗",
                            color = Color(0xFF7E22CE),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amavasya Countdown Timer
            HeaderCountdownBanner(
                countdown = activeCountdown,
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
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp,
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
                        color = Color(0xFFB45309)
                    )
                    Text(
                        text = countdown.formattedDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                }

                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = countdown.hindiTithiName,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4338CA),
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
                    countdown.isToday -> Color(0xFF059669)
                    countdown.days in 1..3 -> Color(0xFFD97706)
                    else -> Color(0xFF64748B)
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
                        color = Color(0xFF2563EB),
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
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp,
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
                color = Color(0xFF0284C7),
                letterSpacing = 0.5.sp
            )
            Text(
                text = unit,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
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

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    val (bgColor, borderColor, icon, iconColor, title, description) = when (uiState.amavasyaBannerState) {
        "day_after" -> {
            BannerConfig(
                bg = if (isDark) Color(0xB3451A03) else Color(0xD9FEF3C7),
                border = if (isDark) SaffronGold else Color(0xFFFCD34D),
                icon = Icons.Default.Campaign,
                iconColor = if (isDark) Color(0xFFFDE047) else Color(0xFFB45309),
                title = "अमावस्या के दूसरे दिन की रिपोर्ट (प्रतिपदा)",
                desc = "अमावस्या चक्र संपन्न हो चुका है। नीचे दिए गए सदस्यों में से जिनका बकाया है, कृपया जल्द से जल्द अपना अंशदान पूरा करें।"
            )
        }
        "advance" -> {
            BannerConfig(
                bg = if (isDark) Color(0xB31E3A8A) else Color(0xD9EFF6FF),
                border = if (isDark) Color(0xFF3B82F6) else Color(0xFFBFDBFE),
                icon = Icons.Default.NotificationsActive,
                iconColor = if (isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8),
                title = "अग्रिम सूचना (अमावस्या में 3-4 दिन शेष)",
                desc = "सभी सदस्यों से सादर अनुरोध है कि तय सहमति अनुसार अपनी मासिक सहयोग राशि समय पर जमा कराने की तैयारी रखें।"
            )
        }
        else -> {
            BannerConfig(
                bg = if (isDark) Color(0xB31E1B4B) else Color(0xD9EEF2FF),
                border = if (isDark) Color(0xFF6366F1) else Color(0xFFC7D2FE),
                icon = Icons.Default.Campaign,
                iconColor = if (isDark) Color(0xFFA5B4FC) else Color(0xFF4338CA),
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
                        .background(if (isDark) Color(0x33FFFFFF) else Color.White.copy(alpha = 0.8f), CircleShape),
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
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B),
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
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
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
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val cardContainer = if (isDark) Color(0xCC1E293B) else Color(0xEEFFFFFF)
    val cardBorder = if (isDark) Color(0x33FFFFFF) else BorderSlate
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardContainer),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
            .testTag("fund_progress_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Consensus note banner if present
            if (uiState.consensusNote.isNotBlank()) {
                Surface(
                    color = if (isDark) Color(0xFF451A03) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) SaffronGold else Color(0xFFFCD34D)),
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
                                color = if (isDark) Color(0xFFFDE047) else Color(0xFF92400E)
                            )
                            Text(
                                text = uiState.consensusNote,
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFFEF08A) else Color(0xFF78350F)
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
                        color = textSecondary,
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
                            color = textSecondary,
                            letterSpacing = 0.5.sp
                        )
                        if (uiState.isAdmin) {
                            Text(
                                text = " ✎",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) SaffronGold else Color(0xFF4338CA)
                            )
                        }
                    }
                    Text(
                        text = if (uiState.targetAmount > 0) "₹${uiState.targetAmount.formatAmount()}" else "अनिर्धारित (सहमति अनुसार)",
                        fontSize = if (uiState.targetAmount > 0) 18.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
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
                    color = textSecondary
                )

                Surface(
                    color = if (isDark) Color(0xFF312E81) else Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${uiState.progressPercent}%",
                        color = if (isDark) Color(0xFFA5B4FC) else Color(0xFF4338CA),
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
                color = if (isDark) SaffronGold else Color(0xFF4F46E5),
                trackColor = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
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
                    textColor = textPrimary,
                    bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                    borderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    label = "जमा (Paid)",
                    value = uiState.paidMembers.toString(),
                    textColor = PaidGreen,
                    bgColor = if (isDark) Color(0xFF064E3B) else PaidGreenLight,
                    borderColor = if (isDark) Color(0xFF047857) else Color(0xFFA7F3D0),
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    label = "बाकी (Pending)",
                    value = uiState.pendingMembers.toString(),
                    textColor = PendingRed,
                    bgColor = if (isDark) Color(0xFF450A0A) else PendingRedLight,
                    borderColor = if (isDark) Color(0xFF991B1B) else Color(0xFFFECDD3),
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
    pendingRegistrationCount: Int = 0,
    onAddMember: () -> Unit,
    onSetCycleTarget: () -> Unit = {},
    onViewPaymentRequests: () -> Unit = {},
    onViewRegistrationRequests: () -> Unit = {},
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

            // Action Row 1: Cycle Target & Payment Requests
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
                            text = "लक्ष्य तय करें",
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

            // Action Row 2: Member Registration Verification & Add Member
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Member Registration Verification Requests with badge
                Surface(
                    color = if (pendingRegistrationCount > 0) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (pendingRegistrationCount > 0) PendingRed else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onViewRegistrationRequests() }
                        .testTag("btn_admin_view_registration_requests")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📝 ", fontSize = 12.sp)
                        Text(
                            text = "सदस्य सत्यापन" + if (pendingRegistrationCount > 0) " ($pendingRegistrationCount)" else "",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingRegistrationCount > 0) PendingRed else DeepNavy
                        )
                    }
                }

                // Add Member Button
                Surface(
                    color = Color(0xFF4338CA),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_add_member")
                        .clickable { onAddMember() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add member",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "नया सदस्य जोड़ें",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun Int.formatAmount(): String {
    return java.text.NumberFormat.getIntegerInstance(java.util.Locale("en", "IN")).format(this)
}
