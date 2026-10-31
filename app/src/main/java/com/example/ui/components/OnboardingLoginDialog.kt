package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SaffronGold

@Composable
fun OnboardingLoginDialog(
    onLoginSuccess: (name: String, phone: String, pin: String, insta: String, matrimonialStatus: String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var securityPin by remember { mutableStateOf("") }
    var instagramId by remember { mutableStateOf("") }
    var matrimonialStatus by remember { mutableStateOf("अविवाहित (बायोडाटा उपलब्ध)") }
    var showBioOptions by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val bioOptions = listOf(
        "अविवाहित (बायोडाटा उपलब्ध)",
        "विवाहित",
        "अविवाहित (युवा सदस्य)"
    )

    Dialog(
        onDismissRequest = { /* Mandatory, cannot dismiss */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DeepNavy
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Big Official Logo with Golden Ring
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(3.dp, SaffronGold, CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_foreground_1793369456038),
                        contentDescription = "युवा शक्ति ग्रुप लोगो",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Official Branding Title
                Text(
                    text = "🚩 युवा शक्ति ग्रुप भाटी परिवार कुचेरा",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "मासिक अमावस्या अंशदान, विवाह रिकॉर्ड एवं बहीखाता",
                    fontSize = 12.sp,
                    color = Color(0xFFC7D2FE),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Login & Registration Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "सदस्य प्रवेश व सत्यापन",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "कृपया अपना सही नाम और मोबाइल नंबर दर्ज करें ताकि आपको ग्रुप के सदस्यों की सूची में जोड़ा जा सके।",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )

                        // 1. Full Name (Mandatory)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                errorMessage = null
                            },
                            label = { Text("पूरा नाम * (जैसे: रेवंतराम भाटी)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = DeepNavy)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepNavy,
                                focusedLabelColor = DeepNavy
                            )
                        )

                        // 2. Mobile Number (Mandatory)
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                                    phoneNumber = it
                                    errorMessage = null
                                }
                            },
                            label = { Text("मोबाइल नंबर * (10 अंक)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = DeepNavy)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepNavy,
                                focusedLabelColor = DeepNavy
                            )
                        )

                        // 3. Security PIN (Mandatory)
                        OutlinedTextField(
                            value = securityPin,
                            onValueChange = {
                                if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                                    securityPin = it
                                    errorMessage = null
                                }
                            },
                            label = { Text("सुरक्षा कोड (4-अंकीय PIN) *") },
                            placeholder = { Text("अपना 4-अंकीय पिन बनाएं") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = DeepNavy)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepNavy,
                                focusedLabelColor = DeepNavy
                            )
                        )

                        // 4. Instagram ID (Optional)
                        OutlinedTextField(
                            value = instagramId,
                            onValueChange = { instagramId = it },
                            label = { Text("इंस्टाग्राम आईडी (वैकल्पिक)") },
                            placeholder = { Text("@username (यदि हो)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFE1306C))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepNavy,
                                focusedLabelColor = DeepNavy
                            )
                        )

                        // 5. Matrimonial Record Dropdown (विवाह रिकॉर्ड)
                        Column {
                            Text(
                                text = "विवाह रिकॉर्ड / वैवाहिक स्थिति:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showBioOptions = !showBioOptions }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = matrimonialStatus, fontSize = 13.sp, color = Color(0xFF0F172A))
                                    Text(text = if (showBioOptions) "▲" else "▼", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            if (showBioOptions) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                ) {
                                    bioOptions.forEach { opt ->
                                        Text(
                                            text = opt,
                                            fontSize = 12.5.sp,
                                            color = if (opt == matrimonialStatus) DeepNavy else Color(0xFF334155),
                                            fontWeight = if (opt == matrimonialStatus) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    matrimonialStatus = opt
                                                    showBioOptions = false
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = "⚠️ $errorMessage",
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val trimmedName = fullName.trim()
                                val trimmedPhone = phoneNumber.trim()
                                val trimmedPin = securityPin.trim()

                                if (trimmedName.isEmpty()) {
                                    errorMessage = "कृपया अपना पूरा नाम दर्ज करें।"
                                    return@Button
                                }
                                if (trimmedPhone.length < 10) {
                                    errorMessage = "कृपया 10-अंकों का वैध मोबाइल नंबर दर्ज करें।"
                                    return@Button
                                }
                                if (trimmedPin.length < 4) {
                                    errorMessage = "कृपया 4-अंकीय सुरक्षा पिन दर्ज करें।"
                                    return@Button
                                }

                                onLoginSuccess(
                                    trimmedName,
                                    trimmedPhone,
                                    trimmedPin,
                                    instagramId.trim(),
                                    matrimonialStatus
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronGold)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeepNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "सत्यापित करें एवं ऐप शुरू करें",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        }
                    }
                }
            }
        }
    }
}
