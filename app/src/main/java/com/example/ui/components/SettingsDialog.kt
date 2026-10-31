package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.File
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold
import com.example.util.ApkShareHelper

/**
 * iPhone-style toggle switch with smooth spring bounce and Apple #34C759 track color.
 */
@Composable
fun IosToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "ios_toggle_thumb"
    )

    Box(
        modifier = modifier
            .width(48.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(if (checked) Color(0xFF34C759) else Color(0xFF94A3B8))
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .size(24.dp)
                .shadow(3.dp, CircleShape)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
fun SettingsDialog(
    currentThemeMode: String,
    currentLanguage: String,
    isAdmin: Boolean,
    isAppLockEnabled: Boolean = false,
    isBiometricEnabled: Boolean = true,
    currentApkUrl: String? = null,
    latestVersionNote: String = "",
    whatsAppGroupLink: String = "",
    fundUpiId: String = "",
    onUpdateWhatsAppGroupLink: (String) -> Unit = {},
    onUpdateFundUpiId: (String) -> Unit = {},
    onBackupToGoogleDrive: () -> Unit = {},
    onBackupToJioCloud: () -> Unit = onBackupToGoogleDrive,
    onRestoreFromLocalBackup: () -> Unit = {},
    onDismiss: () -> Unit,
    onThemeSelect: (String) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onToggleAppLock: (Boolean) -> Unit = {},
    onToggleBiometric: (Boolean) -> Unit = {},
    onChangeAppLockPinClick: () -> Unit = {},
    onLockNowClick: () -> Unit = {},
    onChangePinClick: () -> Unit,
    onSendDailyThoughtNotification: () -> Unit,
    onOpenAppUpdateConsole: () -> Unit = {},
    onSwitchAdminRoleClick: () -> Unit = {},
    onUrgentBroadcastClick: () -> Unit = {},
    onSirenTgadaClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val isEnglish = currentLanguage == "en"

    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val dividerColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val cardBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    var showEditUpiInline by remember { mutableStateOf(false) }
    var editUpiText by remember(fundUpiId) { mutableStateOf(fundUpiId) }

    var showEditWaInline by remember { mutableStateOf(false) }
    var editWaText by remember(whatsAppGroupLink) { mutableStateOf(whatsAppGroupLink) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(24.dp)),
            color = if (isDark) Color(0xF21E293B) else Color(0xF8FFFFFF),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isDark) SaffronGold.copy(alpha = 0.5f) else Color(0x33000000)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = if (isDark) SaffronGold else DeepNavy,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEnglish) "App Settings" else "एप्लिकेशन सेटिंग्स",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = textSecondary)
                    }
                }

                HorizontalDivider(color = dividerColor)
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Section 0A: Discrete System Security & Access Level (No Darshan/Admin Mode words)
                    item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAdmin) (if (isDark) Color(0xFF1E1B4B) else Color(0xFFFEF3C7)) else cardBg
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.2.dp,
                                if (isAdmin) (if (isDark) SaffronGold else Color(0xFFF59E0B)) else cardBorder,
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            .size(34.dp)
                                            .background(
                                                if (isAdmin) SaffronGold else (if (isDark) Color(0xFF334155) else Color(0xFF64748B)),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isAdmin) Color(0xFF451A03) else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isAdmin) "🛡️ सिस्टम प्रबंधन नियंत्रण सक्रिय" else "👤 सामान्य सदस्य एक्सेस स्तर",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = if (isAdmin) (if (isDark) Color(0xFFFDE047) else Color(0xFF92400E)) else textPrimary
                                        )
                                        Text(
                                            text = if (isAdmin) "सभी संपादन व प्रशासनिक सुविधाएं सक्रिय हैं" else "पारदर्शी बहीखाता व सामान्य सुविधाएं सक्रिय हैं",
                                            fontSize = 10.5.sp,
                                            color = if (isAdmin) (if (isDark) Color(0xFFFEF08A) else Color(0xFFB45309)) else textSecondary
                                        )
                                    }
                                }

                                Surface(
                                    color = if (isAdmin) PendingRed else (if (isDark) SaffronGold else DeepNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        onDismiss()
                                        onSwitchAdminRoleClick()
                                    }
                                ) {
                                    Text(
                                        text = if (isAdmin) "एक्सेस बंद करें" else "सुरक्षा पिन दर्ज करें",
                                        color = if (isAdmin) Color.White else (if (isDark) Color(0xFF451A03) else Color.White),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 0B: Cloud Backup & Google Drive Integration (Zero Data Loss)
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFEFF6FF)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.2.dp, if (isDark) Color(0xFF1D4ED8) else Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(0xFF1A73E8), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cloud,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "📁 गूगल ड्राइव (Google Drive) एवं ऑनलाइन बैकअप",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E40AF)
                                    )
                                    Text(
                                        text = "Google Drive से जोड़कर 100% सुरक्षित डेटा",
                                        fontSize = 10.5.sp,
                                        color = if (isDark) Color(0xFFBFDBFE) else Color(0xFF3B82F6)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "बार-बार ऐप अपडेट करने या अनइंस्टॉल करने पर भी डेटा कभी नष्ट नहीं होगा। पूरा बहीखाता, सदस्य सूची, ऋण व संदेश अपनी गूगल ड्राइव (Google Drive) में सहेजें व कभी भी वापस पाएं।",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Backup to Google Drive button
                                Surface(
                                    color = Color(0xFF1A73E8),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onBackupToGoogleDrive() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp, horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudUpload,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Google Drive बैकअप",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                // Restore from local/cloud button
                                Surface(
                                    color = if (isDark) Color(0xFF1E293B) else Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A73E8)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onRestoreFromLocalBackup() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp, horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDownload,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "डेटा रीस्टोर करें",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF93C5FD) else Color(0xFF2563EB)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 0C: WhatsApp Group & UPI ID Settings
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // WhatsApp Group Link Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📲 आधिकारिक व्हाट्सएप ग्रुप लिंक",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = whatsAppGroupLink.ifBlank { "लिंक सेट नहीं है" },
                                        fontSize = 10.5.sp,
                                        color = textSecondary,
                                        maxLines = 1
                                    )
                                }
                                if (isAdmin) {
                                    IconButton(
                                        onClick = { showEditWaInline = !showEditWaInline },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit WA link",
                                            tint = if (isDark) SaffronGold else DeepNavy,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (showEditWaInline && isAdmin) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = editWaText,
                                    onValueChange = { editWaText = it },
                                    label = { Text("WhatsApp ग्रुप इनवाइट लिंक") },
                                    placeholder = { Text("https://chat.whatsapp.com/...") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = SaffronGold,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (editWaText.isNotBlank()) {
                                                onUpdateWhatsAppGroupLink(editWaText.trim())
                                                showEditWaInline = false
                                                Toast.makeText(context, "व्हाट्सएप ग्रुप लिंक अपडेट हुआ!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                ) {
                                    Text(
                                        text = "व्हाट्सएप लिंक सुरक्षित करें",
                                        color = Color(0xFF451A03),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = dividerColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Official UPI ID / PhonePe Number Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📱 ऑफिशियल UPI ID / फ़ोनपे नंबर",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = fundUpiId.ifBlank { "revantrambhati@upi" },
                                        fontSize = 10.5.sp,
                                        color = textSecondary
                                    )
                                }
                                if (isAdmin) {
                                    IconButton(
                                        onClick = { showEditUpiInline = !showEditUpiInline },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit UPI ID",
                                            tint = if (isDark) SaffronGold else DeepNavy,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (showEditUpiInline && isAdmin) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = editUpiText,
                                    onValueChange = { editUpiText = it },
                                    label = { Text("UPI ID या Google Pay / PhonePe नंबर") },
                                    placeholder = { Text("उदा. 9929xxxxxx@ybl या name@upi") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = SaffronGold,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (editUpiText.isNotBlank()) {
                                                onUpdateFundUpiId(editUpiText.trim())
                                                showEditUpiInline = false
                                                Toast.makeText(context, "UPI ID सुरक्षित हो गई!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                ) {
                                    Text(
                                        text = "UPI ID सुरक्षित करें",
                                        color = Color(0xFF451A03),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 0D: Alert Mode & Siren Mode Controls (Only visible to admin)
                if (isAdmin) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF450A0A) else Color(0xFFFFF1F2)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.2.dp, if (isDark) PendingRed else Color(0xFFFECDD3), RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(0xFFDC2626), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "📢 अलर्ट एवं सायरन मोड नियंत्रण",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = if (isDark) Color(0xFFFCA5A5) else Color(0xFF991B1B)
                                        )
                                        Text(
                                            text = "आपातकालीन सूचना व बकाया अंशदान सायरन",
                                            fontSize = 10.5.sp,
                                            color = if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Alert Broadcast Button
                                Surface(
                                    color = if (isDark) Color(0xFF1E293B) else Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF7F1D1D) else Color(0xFFFEE2E2)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isAdmin) {
                                                onDismiss()
                                                onUrgentBroadcastClick()
                                            } else {
                                                Toast.makeText(context, "केवल व्यवस्थापक ही अलर्ट भेज सकते हैं", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(text = "📢", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "आपातकालीन अलर्ट जारी करें",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFFCA5A5) else Color(0xFF991B1B)
                                                )
                                                Text(
                                                    text = "सभी सदस्यों को लाल स्क्रीन व कंपन के साथ अलर्ट",
                                                    fontSize = 9.5.sp,
                                                    color = textSecondary
                                                )
                                            }
                                        }
                                        Surface(
                                            color = if (isAdmin) Color(0xFFDC2626) else Color(0xFF94A3B8),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "अलर्ट भेजें ↗",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Siren Tgada Button
                                Surface(
                                    color = if (isDark) Color(0xFF1E293B) else Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF7F1D1D) else Color(0xFFFEE2E2)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isAdmin) {
                                                onSirenTgadaClick()
                                            } else {
                                                Toast.makeText(context, "केवल व्यवस्थापक ही सायरन बजा सकते हैं", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(text = "🚨", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "सायरन तगादा बजाएं (Police Siren)",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
                                                )
                                                Text(
                                                    text = "बकाया अंशदान वाले सदस्यों के फ़ोन पर सायरन बजेगा",
                                                    fontSize = 9.5.sp,
                                                    color = textSecondary
                                                )
                                            }
                                        }
                                        Surface(
                                            color = if (isAdmin) Color(0xFFB91C1C) else Color(0xFF94A3B8),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "सायरन बजाएं 🔔",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 1: App Updates
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF064E3B) else Color(0xFFF0FDF4)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.2.dp, if (isDark) Color(0xFF059669) else Color(0xFFBBF7D0), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(0xFF16A34A), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SystemUpdate,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isEnglish) "App Update Center" else "🚀 एप्लिकेशन अपडेट केंद्र",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF166534)
                                        )
                                        Text(
                                            text = "वर्तमान संस्करण: v2.5.0 (कुचेरा)",
                                            fontSize = 11.sp,
                                            color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF15803D)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (!currentApkUrl.isNullOrBlank()) {
                                Surface(
                                    color = Color(0xFF15803D),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentApkUrl))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "📥 नया अपडेट डाउनलोड करें",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (latestVersionNote.isNotBlank()) latestVersionNote else "एडमिन द्वारा नया संस्करण उपलब्ध कराया गया है",
                                                fontSize = 10.sp,
                                                color = Color(0xFFDCFCE7)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Check update button
                            Surface(
                                color = if (isDark) Color(0xFF1E293B) else Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!currentApkUrl.isNullOrBlank()) {
                                            Toast.makeText(context, "🌟 नया अपडेट उपलब्ध है! ऊपर दिए गए बटन से डाउनलोड करें।", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "✅ आप पहले से ही नवीनतम संस्करण (v2.5.0) का उपयोग कर रहे हैं।", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔄 नए अपडेट की जांच करें",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF166534)
                                    )
                                }
                            }

                            if (isAdmin) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = if (isDark) Color(0xFF1E1B4B) else DeepNavy,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismiss()
                                            onOpenAppUpdateConsole()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📢 नया अपडेट जारी करें (Admin Console)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            color = SaffronGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { HorizontalDivider(color = dividerColor) }

                // Section 2: Share App (Actual APK file & Invite text)
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = if (isDark) SaffronGold else DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "Share Application" else "एप्लिकेशन शेयर करें",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Share Actual APK File directly!
                        Surface(
                            color = if (isDark) Color(0xFF1E1B4B) else Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) SaffronGold else Color(0xFFC7D2FE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { ApkShareHelper.shareActualApk(context) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFF4338CA), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "📦 पूरी ऐप (APK फ़ाइल) सीधे भेजें",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = if (isDark) Color(0xFFFEF3C7) else DeepNavy
                                        )
                                        Text(
                                            text = "व्हाट्सएप या ब्लूटूथ पर सीधे APK भेजें ताकि तुरंत इंस्टॉल हो सके",
                                            fontSize = 10.sp,
                                            color = textSecondary
                                        )
                                    }
                                }
                                Text(text = "APK ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) SaffronGold else DeepNavy)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // WhatsApp text message invite
                        Surface(
                            color = cardBg,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { shareApplication(context) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "💬 व्हाट्सएप आमंत्रण संदेश शेयर करें",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary
                                )
                                Text(text = "शेयर ↗", fontSize = 11.sp, color = textSecondary)
                            }
                        }
                    }
                }

                item { HorizontalDivider(color = dividerColor) }

                // Section 3: App Lock & Security with iPhone-style Toggle
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = if (isDark) SaffronGold else DeepNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isEnglish) "App Lock (Security)" else "सुरक्षा व ऐप लॉक",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = if (isEnglish) "Lock with PIN / Biometrics" else "पिन व बायोमेट्रिक से सुरक्षित करें",
                                            fontSize = 10.5.sp,
                                            color = textSecondary
                                        )
                                    }
                                }

                                IosToggle(
                                    checked = isAppLockEnabled,
                                    onCheckedChange = { onToggleAppLock(it) }
                                )
                            }

                            if (isAppLockEnabled) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = dividerColor)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            tint = textPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isEnglish) "Fingerprint / Face Unlock" else "फिंगरप्रिंट व फेस लॉक",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textPrimary
                                            )
                                            Text(
                                                text = if (isEnglish) "Use device biometrics" else "डिवाइस के बायोमेट्रिक से तुरंत खोलें",
                                                fontSize = 10.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }

                                    IosToggle(
                                        checked = isBiometricEnabled,
                                        onCheckedChange = { onToggleBiometric(it) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = if (isDark) Color(0xFF1E293B) else Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onChangeAppLockPinClick() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Key,
                                                contentDescription = null,
                                                tint = textSecondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isEnglish) "Change PIN" else "पिन बदलें",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                        }
                                    }

                                    Surface(
                                        color = if (isDark) SaffronGold else DeepNavy,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onDismiss()
                                                onLockNowClick()
                                            }
                                        ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (isDark) Color(0xFF451A03) else SaffronGold,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isEnglish) "Lock Now" else "अभी लॉक करें",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color(0xFF451A03) else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { HorizontalDivider(color = dividerColor) }

                // Section 4: Theme Mode
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = if (isDark) SaffronGold else DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "Theme Mode" else "थीम मोड (Dark / Light)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "Morning Light, Night Dark (Auto)" else "🌅 सुबह लाइट व रात में डार्क (ऑटो)",
                            subtitle = if (isEnglish) "Light 6 AM - 7 PM, Dark 7 PM - 6 AM" else "सुबह 6 बजे से लाइट, शाम 7 बजे से डार्क मोड",
                            isSelected = currentThemeMode == "auto_time",
                            icon = Icons.Default.BrightnessAuto,
                            isDark = isDark,
                            onClick = { onThemeSelect("auto_time") }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "Light Mode (White)" else "⚪ लाइट मोड (सफेद)",
                            subtitle = if (isEnglish) "Always light background" else "हमेशा सफेद/लाइट बैकग्राउंड",
                            isSelected = currentThemeMode == "light",
                            icon = Icons.Default.LightMode,
                            isDark = isDark,
                            onClick = { onThemeSelect("light") }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "Dark Mode" else "⚫ डार्क मोड",
                            subtitle = if (isEnglish) "Comfortable dark theme" else "आँखों के लिए आरामदायक डार्क थीम",
                            isSelected = currentThemeMode == "dark",
                            icon = Icons.Default.DarkMode,
                            isDark = isDark,
                            onClick = { onThemeSelect("dark") }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "System Default" else "📱 सिस्टम डिफ़ॉल्ट",
                            subtitle = if (isEnglish) "Follow phone system settings" else "फ़ोन की सिस्टम सेटिंग अनुसार",
                            isSelected = currentThemeMode == "system",
                            icon = Icons.Default.PhoneAndroid,
                            isDark = isDark,
                            onClick = { onThemeSelect("system") }
                        )
                    }
                }

                item { HorizontalDivider(color = dividerColor) }

                // Section 5: Language Selection
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (isDark) SaffronGold else DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "Language / भाषा" else "भाषा चयन (Language)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LanguagePillItem(
                                label = "🇮🇳 हिन्दी (Hindi)",
                                isSelected = currentLanguage == "hi",
                                isDark = isDark,
                                modifier = Modifier.weight(1f),
                                onClick = { onLanguageSelect("hi") }
                            )

                            LanguagePillItem(
                                label = "🌐 English",
                                isSelected = currentLanguage == "en",
                                isDark = isDark,
                                modifier = Modifier.weight(1f),
                                onClick = { onLanguageSelect("en") }
                            )
                        }
                    }
                }

                item { HorizontalDivider(color = dividerColor) }

                // Section 6: Daily Thought Notification
                item {
                    Surface(
                        color = if (isDark) Color(0xFF451A03) else Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) SaffronGold else Color(0xFFFDE68A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSendDailyThoughtNotification() }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(SaffronGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color(0xFF451A03),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Daily Thought Notification" else "आज का सुविचार नोटिफिकेशन",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isDark) Color(0xFFFEF3C7) else Color(0xFF78350F)
                                    )
                                    Text(
                                        text = if (isEnglish) "Send daily inspirational thought to phone" else "फ़ोन पर आज का प्रेरक विचार प्राप्त करें",
                                        fontSize = 10.5.sp,
                                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
                                    )
                                }
                            }
                            Text(
                                text = "प्राप्त करें 🔔",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) SaffronGold else Color(0xFF78350F)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Close Button
            Surface(
                color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDismiss() }
            ) {
                Text(
                    text = if (isEnglish) "Close Settings" else "सेटिंग्स बंद करें",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}
}

@Composable
private fun ThemeOptionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        isSelected && isDark -> Color(0xFF1E1B4B)
        isSelected -> Color(0xFFEEF2FF)
        isDark -> Color(0xFF0F172A)
        else -> Color(0xFFF8FAFC)
    }
    val border = when {
        isSelected && isDark -> SaffronGold
        isSelected -> DeepNavy
        isDark -> Color(0xFF334155)
        else -> Color(0xFFE2E8F0)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, border),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) (if (isDark) SaffronGold else DeepNavy) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) (if (isDark) Color(0xFFFEF3C7) else DeepNavy) else (if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B))
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LanguagePillItem(
    label: String,
    isSelected: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = when {
        isSelected && isDark -> Color(0xFF1E1B4B)
        isSelected -> Color(0xFFEEF2FF)
        isDark -> Color(0xFF0F172A)
        else -> Color(0xFFF8FAFC)
    }
    val border = when {
        isSelected && isDark -> SaffronGold
        isSelected -> DeepNavy
        isDark -> Color(0xFF334155)
        else -> Color(0xFFCBD5E1)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, border),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) (if (isDark) Color(0xFFFEF3C7) else DeepNavy) else (if (isDark) Color(0xFFF8FAFC) else Color(0xFF334155))
            )
        }
    }
}

private fun shareApplication(context: Context) {
    val shareText = "🚩 *युवा शक्ति ग्रुप, भाटी परिवार कुचेरा* 🚩\n\n" +
            "सादर जय श्री कृष्णा / राम राम सा!\n\n" +
            "भाटी परिवार के मासिक अमावस्या फंड, पारदर्शी बहीखाता, कार्यक्रम फोटो गैलरी एवं दैनिक सुविचार से जुड़ने हेतु एप्लिकेशन डाउनलोड करें।\n\n" +
            "📥 *ऐप डाउनलोड लिंक:*\n" +
            "1. डायरेक्ट डाउनलोड:\nhttps://github.com/revantram/yuva-shakti-app/releases/download/latest/yuva-shakti.apk\n\n" +
            "2. रिलीज़ वेब पेज:\nhttps://github.com/revantram/yuva-shakti-app/releases/tag/latest\n\n" +
            "📲 डिजिटल बहीखाता • पारदर्शी प्रबंधन • पारिवारिक एकता\n" +
            "- युवा शक्ति ग्रुप, भाटी परिवार कुचेरा"

    try {
        val srcFile = File(context.applicationInfo.sourceDir)
        val cacheApk = File(context.cacheDir, "yuva-shakti.apk")
        if (srcFile.exists()) {
            srcFile.copyTo(cacheApk, overwrite = true)
            val apkUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheApk
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, apkUri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "युवा शक्ति ग्रुप ऐप APK शेयर करें"))
            return
        }
    } catch (e: Exception) {}

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "युवा शक्ति ग्रुप ऐप शेयर करें"))
}
