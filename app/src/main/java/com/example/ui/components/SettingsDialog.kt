package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.PaidGreen
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
            .background(if (checked) Color(0xFF34C759) else Color(0xFFCBD5E1))
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
    onDismiss: () -> Unit,
    onThemeSelect: (String) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onToggleAppLock: (Boolean) -> Unit = {},
    onToggleBiometric: (Boolean) -> Unit = {},
    onChangeAppLockPinClick: () -> Unit = {},
    onLockNowClick: () -> Unit = {},
    onChangePinClick: () -> Unit,
    onSendDailyThoughtNotification: () -> Unit,
    onOpenAppUpdateConsole: () -> Unit = {}
) {
    val context = LocalContext.current
    val isEnglish = currentLanguage == "en"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "App Settings" else "एप्लिकेशन सेटिंग्स",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: App Updates (ऐप अपडेट)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.2.dp, Color(0xFFBBF7D0), RoundedCornerShape(14.dp))
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
                                            color = Color(0xFF166534)
                                        )
                                        Text(
                                            text = "वर्तमान संस्करण: v2.4.0 (कुचेरा)",
                                            fontSize = 11.sp,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // If update link is available from admin
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
                                                Toast.makeText(context, "लिंक खोलने में त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
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
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!currentApkUrl.isNullOrBlank()) {
                                            Toast.makeText(context, "🌟 नया अपडेट उपलब्ध है! ऊपर दिए गए हरे बटन से डाउनलोड करें।", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "✅ आप पहले से ही नवीनतम संस्करण (v2.4.0) का उपयोग कर रहे हैं।", Toast.LENGTH_SHORT).show()
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
                                        color = Color(0xFF166534)
                                    )
                                }
                            }

                            // Admin broadcast button
                            if (isAdmin) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = DeepNavy,
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

                item { HorizontalDivider(color = Color(0xFFE2E8F0)) }

                // Section 2: Share App (Actual APK file & Invite text)
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "Share Application" else "एप्लिकेशन शेयर करें",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Primary Option: Share Actual APK File directly!
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    ApkShareHelper.shareActualApk(context)
                                }
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
                                            color = DeepNavy
                                        )
                                        Text(
                                            text = "व्हाट्सएप या ब्लूटूथ पर सीधे APK भेजें ताकि सामने वाला तुरंत इंस्टॉल कर सके",
                                            fontSize = 10.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                                Text(text = "APK ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Secondary Option: WhatsApp text message invite
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                    color = Color(0xFF334155)
                                )
                                Text(text = "शेयर ↗", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }

                item { HorizontalDivider(color = Color(0xFFE2E8F0)) }

                // Section 3: App Lock & Security with iPhone-style Toggle
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
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
                                        tint = DeepNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isEnglish) "App Lock (Security)" else "सुरक्षा व ऐप लॉक",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = if (isEnglish) "Lock with PIN / Biometrics" else "पिन व बायोमेट्रिक से सुरक्षित करें",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                // iPhone-style Switch
                                IosToggle(
                                    checked = isAppLockEnabled,
                                    onCheckedChange = { onToggleAppLock(it) }
                                )
                            }

                            if (isAppLockEnabled) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                                Spacer(modifier = Modifier.height(12.dp))

                                // Biometric toggle with iPhone Switch
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
                                            tint = Color(0xFF1E293B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isEnglish) "Fingerprint / Face Unlock" else "फिंगरप्रिंट व फेस लॉक",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = if (isEnglish) "Use device biometrics" else "डिवाइस के बायोमेट्रिक से तुरंत खोलें",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
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
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
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
                                                tint = Color(0xFF334155),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isEnglish) "Change PIN" else "पिन बदलें",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF334155)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = DeepNavy,
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
                                                tint = SaffronGold,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isEnglish) "Lock Now" else "अभी लॉक करें",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { HorizontalDivider(color = Color(0xFFE2E8F0)) }

                // Section 4: Theme Mode
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "Theme Mode" else "थीम मोड (Dark / Light)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "Morning Light, Night Dark (Auto)" else "🌅 सुबह लाइट व रात में डार्क (ऑटो)",
                            subtitle = if (isEnglish) "Light 6 AM - 7 PM, Dark 7 PM - 6 AM" else "सुबह 6 बजे से लाइट, शाम 7 बजे से डार्क मोड",
                            isSelected = currentThemeMode == "auto_time",
                            icon = Icons.Default.BrightnessAuto,
                            onClick = { onThemeSelect("auto_time") }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "Light Mode (White)" else "⚪ लाइट मोड (सफेद)",
                            subtitle = if (isEnglish) "Always light background" else "हमेशा सफेद/लाइट बैकग्राउंड",
                            isSelected = currentThemeMode == "light",
                            icon = Icons.Default.LightMode,
                            onClick = { onThemeSelect("light") }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "Dark Mode" else "⚫ डार्क मोड",
                            subtitle = if (isEnglish) "Comfortable dark theme" else "आँखों के लिए आरामदायक डार्क थीम",
                            isSelected = currentThemeMode == "dark",
                            icon = Icons.Default.DarkMode,
                            onClick = { onThemeSelect("dark") }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ThemeOptionItem(
                            title = if (isEnglish) "System Default" else "📱 सिस्टम डिफ़ॉल्ट",
                            subtitle = if (isEnglish) "Follow phone system settings" else "फ़ोन की सिस्टम सेटिंग अनुसार",
                            isSelected = currentThemeMode == "system",
                            icon = Icons.Default.PhoneAndroid,
                            onClick = { onThemeSelect("system") }
                        )
                    }
                }

                item { HorizontalDivider(color = Color(0xFFE2E8F0)) }

                // Section 5: Language Selection
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "Language / भाषा" else "भाषा चयन (Language)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B)
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
                                modifier = Modifier.weight(1f),
                                onClick = { onLanguageSelect("hi") }
                            )

                            LanguagePillItem(
                                label = "🌐 English",
                                isSelected = currentLanguage == "en",
                                modifier = Modifier.weight(1f),
                                onClick = { onLanguageSelect("en") }
                            )
                        }
                    }
                }

                item { HorizontalDivider(color = Color(0xFFE2E8F0)) }

                // Section 6: Daily Thought Notification
                item {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
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
                                        color = Color(0xFF78350F)
                                    )
                                    Text(
                                        text = if (isEnglish) "Send daily inspirational thought to phone" else "फ़ोन पर आज का प्रेरक विचार प्राप्त करें",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                            Text(text = "प्राप्त करें 🔔", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isEnglish) "Close" else "बंद करें",
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun ThemeOptionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) DeepNavy else Color(0xFFE2E8F0)
        ),
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
                tint = if (isSelected) DeepNavy else Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) DeepNavy else Color(0xFF1E293B)
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) DeepNavy else Color(0xFFCBD5E1)
        ),
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
                color = if (isSelected) DeepNavy else Color(0xFF334155)
            )
        }
    }
}

private fun shareApplication(context: Context) {
    val shareText = "🚩 *युवा शक्ति ग्रुप, भाटी परिवार कुचेरा* 🚩\n\n" +
            "सादर जय श्री कृष्णा / राम राम सा!\n\n" +
            "भाटी परिवार के मासिक अमावस्या फंड, पारदर्शी बहीखाता, कार्यक्रम फोटो गैलरी एवं दैनिक सुविचार से जुड़ने हेतु एप्लिकेशन डाउनलोड करें।\n\n" +
            "📲 डिजिटल बहीखाता • पारदर्शी प्रबंधन • पारिवारिक एकता\n" +
            "- युवा शक्ति ग्रुप, भाटी परिवार कुचेरा"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "युवा शक्ति ग्रुप ऐप शेयर करें"))
}
