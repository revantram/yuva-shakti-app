package com.example.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.SaffronGold

@Composable
fun SettingsDialog(
    currentThemeMode: String, // "auto_time", "light", "dark", "system"
    currentLanguage: String,  // "hi", "en"
    isAdmin: Boolean,
    isAppLockEnabled: Boolean = false,
    isBiometricEnabled: Boolean = true,
    onDismiss: () -> Unit,
    onThemeSelect: (String) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onToggleAppLock: (Boolean) -> Unit = {},
    onToggleBiometric: (Boolean) -> Unit = {},
    onChangeAppLockPinClick: () -> Unit = {},
    onLockNowClick: () -> Unit = {},
    onChangePinClick: () -> Unit,
    onSendDailyThoughtNotification: () -> Unit
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
                // Section 1: Theme Mode
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

                // Section 2: Language Selection
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

                // Section 3: Share App
                item {
                    Surface(
                        color = Color(0xFFEEF2FF),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { shareApplication(context) }
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
                                        .background(Color(0xFF4338CA), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Share Application" else "एप्लिकेशन शेयर करें",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DeepNavy
                                    )
                                    Text(
                                        text = if (isEnglish) "Invite family members via WhatsApp" else "व्हाट्सएप पर परिवार के सदस्यों को शेयर करें",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                            Text(text = "शेयर ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                        }
                    }
                }

                // Section 4: Daily Thought Notification
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

                // Section: App Lock & Biometrics (Password / Fingerprint / Face Lock)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = DeepNavy,
                                        modifier = Modifier.size(18.dp)
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
                                            text = if (isEnglish) "Lock app with PIN / Fingerprint / Face" else "पासवर्ड, फिंगरप्रिंट या फेस लॉक से सुरक्षित करें",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Switch(
                                    checked = isAppLockEnabled,
                                    onCheckedChange = { onToggleAppLock(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = DeepNavy
                                    )
                                )
                            }

                            if (isAppLockEnabled) {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Biometric toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            tint = Color(0xFF1E293B),
                                            modifier = Modifier.size(16.dp)
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
                                                text = if (isEnglish) "Use device biometrics to unlock" else "डिवाइस के बायोमेट्रिक से तुरंत अनलॉक करें",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = isBiometricEnabled,
                                        onCheckedChange = { onToggleBiometric(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = PaidGreen
                                        )
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
                                            .clickable { onLockNowClick() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color.White,
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

                // Section 5: Admin Pin Change
                if (isAdmin) {
                    item { HorizontalDivider(color = Color(0xFFE2E8F0)) }
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChangePinClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        tint = DeepNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isEnglish) "Change Admin PIN (Current: 8696)" else "एडमिन पिन बदलें (वर्तमान: 8696)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Text(text = "बदलें →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                            }
                        }
                    }
                }

                // App version & branding
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "युवा शक्ति ग्रुप (भाटी परिवार)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "संस्करण 2.0 • डिजिटल मासिक बहीखाता",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isEnglish) "Done" else "सहेजें व बंद करें", fontWeight = FontWeight.Bold)
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
    val shareText = "🚩 *युवा शक्ति ग्रुप, भाटी परिवार* 🚩\n\n" +
            "सादर जय श्री कृष्णा / राम राम सा!\n\n" +
            "भाटी परिवार के मासिक अमावस्या फंड, पारदर्शी बहीखाता, कार्यक्रम फोटो गैलरी एवं दैनिक सुविचार से जुड़ने हेतु एप्लिकेशन डाउनलोड करें।\n\n" +
            "📲 डिजिटल बहीखाता • पारदर्शी प्रबंधन • पारिवारिक एकता\n" +
            "- युवा शक्ति ग्रुप"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "युवा शक्ति ग्रुप ऐप शेयर करें"))
}
