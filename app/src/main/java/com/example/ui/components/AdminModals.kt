package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CycleHistoryEntity
import com.example.data.MemberEntity
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AdminPinDialog(
    onDismiss: () -> Unit,
    onVerify: (String) -> Boolean
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val dialogBg = if (isDark) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)

    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBg,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (isDark) Color(0xFF312E81) else Color(0xFFEEF2FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = if (isDark) SaffronGold else DeepNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "सुरक्षा कोड दर्ज करें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = textPrimary
                )
                Text(
                    text = "प्रशासनिक प्रबंधन सक्रिय करने हेतु गुप्त 4-अंकीय पिन दर्ज करें",
                    fontSize = 11.5.sp,
                    color = textSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 8) {
                            pin = it
                            errorMessage = null
                        }
                    },
                    placeholder = { Text("4-अंकों का गुप्त पिन", color = textSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
                        focusedBorderColor = if (isDark) SaffronGold else DeepNavy,
                        unfocusedBorderColor = if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1),
                        cursorColor = if (isDark) SaffronGold else DeepNavy
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = PendingRed,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pin.isBlank()) {
                        errorMessage = "कृपया पिन दर्ज करें"
                        return@Button
                    }
                    val valid = onVerify(pin)
                    if (!valid) {
                        errorMessage = "गलत पिन! सही पिन दर्ज करें।"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) SaffronGold else DeepNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_verify_pin")
            ) {
                Text(
                    text = "सत्यापित करें",
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF451A03) else Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें", color = textSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AddEditMemberDialog(
    memberToEdit: MemberEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, amount: Int, notes: String, instagramId: String, facebookId: String, whatsappNumber: String, villageCity: String) -> Unit
) {
    var name by remember { mutableStateOf(memberToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(memberToEdit?.phone ?: "") }
    var amountStr by remember { mutableStateOf((memberToEdit?.monthlyAmount ?: 0).let { if (it > 0) it.toString() else "" }) }
    var notes by remember { mutableStateOf(memberToEdit?.notes ?: "") }
    var instagramId by remember { mutableStateOf(memberToEdit?.instagramId ?: "") }
    var facebookId by remember { mutableStateOf(memberToEdit?.facebookId ?: "") }
    var whatsappNumber by remember { mutableStateOf(memberToEdit?.whatsappNumber ?: "") }
    var villageCity by remember { mutableStateOf(memberToEdit?.villageCity ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val isEditing = memberToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "सदस्य विवरण व प्रोफ़ाइल संपादित करें" else "नया सदस्य जोड़ें (असीमित सुविधा)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorText = null },
                        label = { Text("पूरा नाम *") },
                        placeholder = { Text("उदा. रेवंत राम जी भाटी") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_member_name")
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 15) phone = it },
                        label = { Text("मोबाइल नंबर (कॉल हेतु)") },
                        placeholder = { Text("10 अंकों का मोबाइल नंबर") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_member_phone")
                    )
                }

                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it.filter { char -> char.isDigit() } },
                        label = { Text("मासिक अंशदान राशि (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = villageCity,
                        onValueChange = { villageCity = it },
                        label = { Text("गाँव / शहर / निवास") },
                        placeholder = { Text("उदा. जोधपुर, बीकानेर, बाड़मेर, पाली") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "📱 सोशल मीडिया प्रोफ़ाइल लिंक (अन्य सदस्यों हेतु):",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = { whatsappNumber = it },
                        label = { Text("व्हाट्सएप नंबर (सीधी चैट हेतु)") },
                        placeholder = { Text("उदा. 9876543210") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = instagramId,
                        onValueChange = { instagramId = it },
                        label = { Text("इंस्टाग्राम यूज़रनेम (Instagram ID)") },
                        placeholder = { Text("उदा. revant_bhati_official") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = facebookId,
                        onValueChange = { facebookId = it },
                        label = { Text("फेसबुक प्रोफ़ाइल नाम / ID") },
                        placeholder = { Text("उदा. revant.bhati.1") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("पद / टिप्पणी (गोपनीय)") },
                        placeholder = { Text("उदा. वरिष्ठ मार्गदर्शक / कोषाध्यक्ष") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorText != null) {
                    item {
                        Text(
                            text = errorText ?: "",
                            color = PendingRed,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorText = "कृपया सदस्य का नाम दर्ज करें"
                        return@Button
                    }
                    val amt = amountStr.toIntOrNull() ?: 0
                    onSave(name, phone, amt, notes, instagramId, facebookId, whatsappNumber, villageCity)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_save_member")
            ) {
                Text(if (isEditing) "अपडेट करें" else "सदस्य जोड़ें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun PaymentModeSelectionDialog(
    member: MemberEntity,
    onDismiss: () -> Unit,
    onConfirm: (mode: String, date: String, upiRef: String) -> Unit
) {
    var selectedMode by remember { mutableStateOf(if (member.paymentMode.isNotBlank()) member.paymentMode.split(" ").first() else "UPI") }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var payDate by remember { mutableStateOf(if (member.paidDate.isNotBlank()) member.paidDate else today) }
    var upiRefOrLink by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${member.name} - भुगतान विवरण",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "भुगतान का माध्यम चुनें:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("UPI", "Cash", "Bank").forEach { mode ->
                        val isSelected = selectedMode == mode
                        Surface(
                            color = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) DeepNavy else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMode = mode }
                        ) {
                            Text(
                                text = when (mode) {
                                    "Cash" -> "नकद"
                                    "Bank" -> "बैंक"
                                    else -> "UPI"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DeepNavy else Color(0xFF334155),
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = payDate,
                    onValueChange = { payDate = it },
                    label = { Text("भुगतान तारीख (YYYY-MM-DD)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // UPI Link / Transaction Reference with Placeholder
                OutlinedTextField(
                    value = upiRefOrLink,
                    onValueChange = { upiRefOrLink = it },
                    label = {
                        Text(
                            when (selectedMode) {
                                "UPI" -> "UPI लिंक या UTR / Ref ID"
                                "Bank" -> "बैंक ट्रांसफर / IMPS / चेक नंबर"
                                else -> "नकद रसीद नंबर / पावती (वैकल्पिक)"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            when (selectedMode) {
                                "UPI" -> "उदा. upi://pay?pa=... या 12-अंक UTR नंबर"
                                "Bank" -> "उदा. NEFT / IMPS 123456789"
                                else -> "उदा. पावती क्र. 101"
                            }
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_transaction_ref")
                )

                if (selectedMode == "UPI") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable {
                                upiRefOrLink = "upi://pay?pa=revantrambhati@upi&am=${member.monthlyAmount}&tn=AmavasyaFund"
                            }
                        ) {
                            Text(
                                text = "⚡ ऑटो UPI लिंक भरें",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMode, payDate, upiRefOrLink.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = PaidGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_payment")
            ) {
                Text("जमा दर्ज करें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ResetCycleConfirmDialog(
    cycleName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = PendingRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "नया चक्र शुरू करें?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Text(
                text = "क्या आप निश्चित रूप से नए महीने का अमावस्या चक्र शुरू करना चाहते हैं?\n\nवर्तमान स्थिति ($cycleName) का रिकॉर्ड सुरक्षित इतिहास में सहेजा जाएगा और सभी सदस्यों की स्थिति वापस 'बाकी' हो जाएगी।",
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = Color(0xFF334155)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PendingRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("हाँ, चक्र रीसेट करें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun CycleHistoryDialog(
    historyList: List<CycleHistoryEntity>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "अमावस्या फंड इतिहास",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "अभी तक कोई पुराना चक्र सहेजा नहीं गया है।\nमाह पूरा होने पर 'नया चक्र रीसेट' करने पर रिकॉर्ड यहाँ जुड़ जाएगा।",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyList) { h ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = h.cycleName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "तारीख: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(h.dateRecorded))}",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = Color(0xFFE2E8F0)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "जमा: ${h.paidMembers}/${h.totalMembers} सदस्य",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PaidGreen
                                    )
                                    Text(
                                        text = "एकत्रित: ₹${h.totalCollected}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
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
                Text("ठीक है")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ChangePinDialog(
    onDismiss: () -> Unit,
    onChangePin: (String) -> Unit
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "नया एडमिन पिन सेट करें",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 8) newPin = it },
                    label = { Text("नया 4-अंकों का पिन") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 8) confirmPin = it },
                    label = { Text("पिन पुनः दर्ज करें") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = PendingRed,
                        fontSize = 11.5.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPin.length < 4) {
                        errorText = "पिन कम से कम 4 अंकों का होना चाहिए"
                        return@Button
                    }
                    if (newPin != confirmPin) {
                        errorText = "दोनों पिन मेल नहीं खाते"
                        return@Button
                    }
                    onChangePin(newPin)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("सहेजें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun EditCycleNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "अमावस्या चक्र का नाम",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("चक्र का नाम") },
                placeholder = { Text("उदा. कार्तिक अमावस्या 2026") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) onSave(text.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("सहेजें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AmavasyaDatePickerDialog(
    currentCountdown: com.example.util.CountdownInfo,
    onDismiss: () -> Unit,
    onSetCustomDate: (Long) -> Unit,
    onResetToAuto: () -> Unit
) {
    val autoDateMillis = remember { com.example.util.AmavasyaCalculator.calculateNextAmavasyaMillis() }
    val autoFormatted = remember { com.example.util.AmavasyaCalculator.formatHindiDate(autoDateMillis) }
    val autoTithi = remember { com.example.util.AmavasyaCalculator.getHindiTithiName(autoDateMillis) }

    val nextMonthMillis = remember {
        autoDateMillis + (29.530588853 * 24 * 3600 * 1000).toLong()
    }
    val nextMonthFormatted = remember { com.example.util.AmavasyaCalculator.formatHindiDate(nextMonthMillis) }
    val nextMonthTithi = remember { com.example.util.AmavasyaCalculator.getHindiTithiName(nextMonthMillis) }

    var customDateStr by remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(sdf.format(Date(currentCountdown.targetMillis)))
    }
    var parseError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "अमावस्या चक्र तिथि प्रबंधन",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "सुझावित आगामी अमावस्या तिथियां:",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )

                // Option 1: Automatic next
                Surface(
                    color = if (!currentCountdown.isCustomDate) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (!currentCountdown.isCustomDate) DeepNavy else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onResetToAuto() }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "⭐ आगामी: $autoTithi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                            Text(
                                text = autoFormatted,
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }
                        if (!currentCountdown.isCustomDate) {
                            Text(
                                text = "सक्रिय ✓",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaidGreen
                            )
                        }
                    }
                }

                // Option 2: Following next month
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSetCustomDate(nextMonthMillis) }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "अगला माह: $nextMonthTithi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = nextMonthFormatted,
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }
                        Text(
                            text = "चुनें",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "या अपनी सुविधानुसार तारीख दर्ज करें (YYYY-MM-DD):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )

                OutlinedTextField(
                    value = customDateStr,
                    onValueChange = { customDateStr = it; parseError = null },
                    placeholder = { Text("YYYY-MM-DD") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (parseError != null) {
                    Text(
                        text = parseError ?: "",
                        color = PendingRed,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val parsed = sdf.parse(customDateStr.trim())
                        if (parsed != null) {
                            val cal = Calendar.getInstance().apply {
                                time = parsed
                                set(Calendar.HOUR_OF_DAY, 9)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            }
                            onSetCustomDate(cal.timeInMillis)
                        } else {
                            parseError = "मान्य तारीख (YYYY-MM-DD) दर्ज करें"
                        }
                    } catch (e: Exception) {
                        parseError = "अमान्य प्रारूप। कृपया YYYY-MM-DD प्रारूप में तारीख लिखें।"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("तारीख सहेजें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun SetCycleTargetDialog(
    currentCycleName: String,
    currentTargetAmount: Int,
    currentConsensusNote: String,
    totalMembersCount: Int,
    defaultCalculatedTarget: Int,
    onDismiss: () -> Unit,
    onSavePlan: (targetAmount: Int, perMemberAmount: Int, applyToAll: Boolean, note: String, postToChat: Boolean) -> Unit
) {
    var targetStr by remember { mutableStateOf(if (currentTargetAmount > 0) currentTargetAmount.toString() else defaultCalculatedTarget.let { if (it > 0) it.toString() else "" }) }
    var perMemberStr by remember { mutableStateOf("") }
    var applyToAll by remember { mutableStateOf(false) }
    var noteStr by remember { mutableStateOf(currentConsensusNote) }
    var postToChat by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "मासिक लक्ष्य व सर्वसम्मति अंशदान",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "वर्तमान चक्र: $currentCycleName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepNavy
                    )
                }

                item {
                    Text(
                        text = "1. इस माह का कुल लक्ष्य (कुल अपेक्षित फंड):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    OutlinedTextField(
                        value = targetStr,
                        onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                        label = { Text("कुल लक्ष्य राशि (₹)") },
                        placeholder = { Text("उदा. 15000, 25000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (defaultCalculatedTarget > 0) {
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { targetStr = defaultCalculatedTarget.toString() }
                        ) {
                            Text(
                                text = "💡 सदस्यों के मौजूदा अंशदान जोड़ अनुसार सेट करें: ₹$defaultCalculatedTarget",
                                fontSize = 10.5.sp,
                                color = DeepNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2. प्रति सदस्य अंशदान राशि (सर्वसम्मति अनुसार):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    OutlinedTextField(
                        value = perMemberStr,
                        onValueChange = { perMemberStr = it.filter { c -> c.isDigit() } },
                        label = { Text("नया प्रति सदस्य अंशदान (₹) (वैकल्पिक)") },
                        placeholder = { Text("उदा. 500, 1000, 1500, 2000...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { applyToAll = !applyToAll }
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = applyToAll,
                            onCheckedChange = { applyToAll = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "यह अंशदान राशि सभी $totalMembersCount सदस्यों पर अभी लागू करें",
                            fontSize = 11.5.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "3. सर्वसम्मति संदेश / निर्णय नोट:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    OutlinedTextField(
                        value = noteStr,
                        onValueChange = { noteStr = it },
                        label = { Text("बैठक का निर्णय / घोषणा") },
                        placeholder = { Text("उदा. इस माह बैठक में तय हुआ कि प्रत्येक सदस्य ₹... जमा कराएगा।") },
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { postToChat = !postToChat }
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = postToChat,
                            onCheckedChange = { postToChat = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "यह निर्णय ग्रुप संवाद (Chat) में तुरंत प्रसारित करें",
                            fontSize = 11.5.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetAmt = targetStr.toIntOrNull() ?: 0
                    val perMemberAmt = perMemberStr.toIntOrNull() ?: 0
                    onSavePlan(targetAmt, perMemberAmt, applyToAll, noteStr.trim(), postToChat)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("निर्णय सहेजें व लागू करें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

