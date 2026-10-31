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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.SaffronGold

@Composable
fun AppUpdateAndCodeConsoleDialog(
    currentApkUrl: String,
    currentVersionNote: String,
    onDismiss: () -> Unit,
    onBroadcastUpdate: (version: String, notes: String, apkUrl: String) -> Unit,
    onExecuteCodePatch: (code: String, onResult: (String) -> Unit) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: App Update, 1: Code Fix Console

    // Update tab state
    var versionText by remember { mutableStateOf("v2.1") }
    var releaseNotes by remember { mutableStateOf(currentVersionNote.ifBlank { "नया कोड व सुधार लागू किए गए हैं, कृपया अपडेट करें।" }) }
    var apkUrl by remember { mutableStateOf(currentApkUrl) }

    // Code fix tab state
    var customCode by remember { mutableStateOf("") }
    var consoleOutput by remember { mutableStateOf("✅ सिस्टम सामान्य है। एडमिन द्वारा कोई भी पैच कोड निष्पादित किया जा सकता है।") }

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
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ऐप अपडेट व कोड सुधार कंसोल",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "व्यवस्थापक डेवलपर टूल्स • वर्शन 2.1",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = DeepNavy
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("🚀 नया अपडेट जारी करें", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("🛠️ कोड सुधार व मरम्मत", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedTab == 0) {
                        // App Update Tab
                        item {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "ℹ️ सभी सदस्यों को ऐप अपडेट भेजने की सुविधा",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E40AF)
                                    )
                                    Text(
                                        text = "यहाँ से आप नया वर्शन व सुधार विवरण दर्ज करके सभी सदस्यों को एक क्लिक में अपडेट नोटिफिकेशन और डाउनलोड लिंक प्रेषित कर सकते हैं।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = versionText,
                                onValueChange = { versionText = it },
                                label = { Text("नया वर्शन क्रमांक (Version)") },
                                placeholder = { Text("उदा. v2.2, v2.3") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = releaseNotes,
                                onValueChange = { releaseNotes = it },
                                label = { Text("सुधार व नई सुविधाओं का विवरण (Release Notes)") },
                                placeholder = { Text("क्या नया जोड़ा गया या कौन सी त्रुटि ठीक की गई...") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = apkUrl,
                                onValueChange = { apkUrl = it },
                                label = { Text("APK डाउनलोड / अपडेट लिंक (Drive/Web URL)") },
                                placeholder = { Text("https://...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    onBroadcastUpdate(
                                        versionText.ifBlank { "v2.1" },
                                        releaseNotes.ifBlank { "नया अपडेट उपलब्ध है।" },
                                        apkUrl.ifBlank { "https://github.com/yuva-shakti-group" }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_broadcast_app_update")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "समस्त सदस्यों को अपडेट भेजें 📢",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Code Patch & Maintenance Tab
                        item {
                            Surface(
                                color = Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "🛠️ डायरेक्ट कोड सुधार व सिस्टम मरम्मत",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "एडमिन के रूप में आप किसी भी त्रुटि को सुधारने हेतु नीचे दिए गए त्वरित पैच चला सकते हैं या नया कोड/नियम लिख सकते हैं।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF78350F),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }

                        // Preset Quick Repairs
                        item {
                            Text(
                                text = "त्वरित सिस्टम रिपेयर टूल्स:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onExecuteCodePatch("repair_db") { result ->
                                                consoleOutput = result
                                            }
                                        }
                                ) {
                                    Text(
                                        text = "🛠️ डेटाबेस रिपेयर",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                                    )
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onExecuteCodePatch("resync_balance") { result ->
                                                consoleOutput = result
                                            }
                                        }
                                ) {
                                    Text(
                                        text = "👥 बैलेंस री-सिंक",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onExecuteCodePatch("recalc_amavasya") { result ->
                                                consoleOutput = result
                                            }
                                        }
                                ) {
                                    Text(
                                        text = "🔄 अमावस्या पंचांग",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                                    )
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onExecuteCodePatch("clear_logs") { result ->
                                                consoleOutput = result
                                            }
                                        }
                                ) {
                                    Text(
                                        text = "🧹 कैशे साफ़ करें",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                                    )
                                }
                            }
                        }

                        // Custom Code Input
                        item {
                            OutlinedTextField(
                                value = customCode,
                                onValueChange = { customCode = it },
                                label = { Text("कस्टम कोड / स्क्रिप्ट पैच दर्ज करें") },
                                placeholder = { Text("उदा. SET_CONSENSUS_POLICY = 1, FIX_MEMBER_RECORD...") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    if (customCode.isNotBlank()) {
                                        onExecuteCodePatch(customCode) { result ->
                                            consoleOutput = result
                                            customCode = ""
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_execute_code_patch")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "कोड निष्पादित करें / पैच लागू करें ▶️",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Console Output Terminal
                        item {
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "🖥️ टर्मिनल आउटपुट:",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8)
                                    )
                                    Text(
                                        text = consoleOutput,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFE2E8F0),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("बंद करें")
            }
        }
    )
}
