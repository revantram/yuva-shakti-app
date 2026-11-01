package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold

@Composable
fun OnboardingLoginDialog(
    onLoginSuccess: (
        name: String,
        phone: String,
        address: String,
        photoUri: String,
        insta: String,
        isAdmin: Boolean
    ) -> Unit
) {
    // Mode choice: "member" or "admin"
    var selectedRole by remember { mutableStateOf("member") }
    var adminCodeInput by remember { mutableStateOf("") }
    var wrongAdminAttempts by remember { mutableIntStateOf(0) }

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var addressVillage by remember { mutableStateOf("") }
    var instagramId by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var alertNotice by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
            errorMessage = null
        }
    }

    val cardBg = Color.White
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val cardBorder = Color(0xFFE2E8F0)
    val iconTint = DeepNavy

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedBorderColor = DeepNavy,
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedLabelColor = DeepNavy,
        unfocusedLabelColor = textSecondary,
        focusedPlaceholderColor = Color(0xFF94A3B8),
        unfocusedPlaceholderColor = Color(0xFF94A3B8),
        cursorColor = DeepNavy
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DeepNavy
    ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Official Logo with Golden Ring
                Box(
                    modifier = Modifier
                        .size(80.dp)
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

                Spacer(modifier = Modifier.height(10.dp))

                // Official Branding Title
                Text(
                    text = "🚩 युवा शक्ति ग्रुप, भाटी परिवार कुचेरा",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "मासिक अमावस्या अंशदान एवं डिजिटल पारदर्शी बहीखाता",
                    fontSize = 12.sp,
                    color = Color(0xFFC7D2FE),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Main Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = BorderStroke(1.2.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        Text(
                            text = "प्रवेश व पंजीकरण (Login & Registration)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textPrimary
                        )

                        // 1. Role Selection: Admin Mode vs Member Mode
                        Column {
                            Text(
                                text = "आप किस रूप में ऐप शुरू करना चाहते हैं? *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Member Mode Card
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedRole = "member"
                                            errorMessage = null
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRole == "member") Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        if (selectedRole == "member") 1.8.dp else 1.dp,
                                        if (selectedRole == "member") Color(0xFF2563EB) else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "👤", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "सदस्य मोड",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (selectedRole == "member") Color(0xFF1E40AF) else Color(0xFF475569)
                                        )
                                        Text(
                                            text = "(पारदर्शी दर्शक)",
                                            fontSize = 9.5.sp,
                                            color = if (selectedRole == "member") Color(0xFF3B82F6) else Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                // Admin Mode Card
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (wrongAdminAttempts >= 3) {
                                                errorMessage = "⚠️ 3 बार गलत कोड दर्ज हुआ। सुरक्षा कारणों से एडमिन मोड ब्लॉक है, कृपया सदस्य मोड में जारी रखें।"
                                            } else {
                                                selectedRole = "admin"
                                                errorMessage = null
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRole == "admin") Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        if (selectedRole == "admin") 1.8.dp else 1.dp,
                                        if (selectedRole == "admin") SaffronGold else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "🛡️", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "एडमिन मोड",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (selectedRole == "admin") Color(0xFF92400E) else Color(0xFF475569)
                                        )
                                        Text(
                                            text = "(व्यवस्थापक)",
                                            fontSize = 9.5.sp,
                                            color = if (selectedRole == "admin") Color(0xFFB45309) else Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }

                        // If Admin Mode selected, prompt for Secret Admin Code
                        if (selectedRole == "admin") {
                            Surface(
                                color = Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "🔐 एडमिन सत्यापन कोड दर्ज करें (प्रयास: $wrongAdminAttempts/3)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF92400E)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = adminCodeInput,
                                        onValueChange = {
                                            if (it.length <= 6) {
                                                adminCodeInput = it
                                                errorMessage = null
                                            }
                                        },
                                        label = { Text("एडमिन कोड * (6 अंक)") },
                                        placeholder = { Text("992942") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SaffronGold)
                                        },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = inputColors
                                    )
                                }
                            }
                        }

                        // 2. Profile Photo Upload Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "प्रोफ़ाइल फ़ोटो जोड़ें (वैकल्पिक / बिना फोटो भी जारी रख सकते हैं):",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .border(2.5.dp, if (selectedPhotoUri.isNotBlank()) SaffronGold else Color(0xFFCBD5E1), CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { photoPickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedPhotoUri.isNotBlank()) {
                                    AsyncImage(
                                        model = selectedPhotoUri,
                                        contentDescription = "प्रोफ़ाइल फोटो",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.BottomEnd)
                                            .background(SaffronGold, CircleShape)
                                            .border(1.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "बदलें",
                                            tint = DeepNavy,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "फोटो अपलोड",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(26.dp)
                                        )
                                        Text(
                                            text = "फोटो चुनें 📸",
                                            fontSize = 9.sp,
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Full Name (Mandatory)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                errorMessage = null
                            },
                            label = { Text("पूरा नाम * (उदा. रेवंतराम भाटी)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = iconTint)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = inputColors
                        )

                        // 4. Mobile Number (Mandatory)
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                                    phoneNumber = it
                                    errorMessage = null
                                }
                            },
                            label = { Text("मोबाइल नंबर * (10 अंक)") },
                            placeholder = { Text("8696663615") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = iconTint)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = inputColors
                        )
                        Text(
                            text = "ℹ️ यह मोबाइल नंबर आपके बहीखाता खाते व अंशदान से लिंक रहेगा।",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(start = 4.dp, top = (-6).dp)
                        )

                        // 5. Village / City / Address (Mandatory)
                        OutlinedTextField(
                            value = addressVillage,
                            onValueChange = {
                                addressVillage = it
                                errorMessage = null
                            },
                            label = { Text("पता / गाँव या शहर * (उदा. कुचेरा)") },
                            placeholder = { Text("गाँव का नाम") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = iconTint)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = inputColors
                        )

                        // 6. Instagram ID (Optional)
                        OutlinedTextField(
                            value = instagramId,
                            onValueChange = { instagramId = it },
                            label = { Text("इंस्टाग्राम आईडी (वैकल्पिक)") },
                            placeholder = { Text("उदा. Revant__") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFE1306C))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = inputColors
                        )

                        // Notice / Error
                        if (alertNotice != null) {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = alertNotice ?: "",
                                    color = Color(0xFF1D4ED8),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = "⚠️ $errorMessage",
                                color = PendingRed,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val trimmedName = fullName.trim()
                                val trimmedPhone = phoneNumber.trim()
                                val trimmedAddress = addressVillage.trim()

                                if (trimmedName.isEmpty()) {
                                    errorMessage = "कृपया अपना पूरा नाम दर्ज करें।"
                                    return@Button
                                }
                                if (trimmedPhone.length < 10) {
                                    errorMessage = "कृपया 10-अंकों का वैध मोबाइल नंबर दर्ज करें।"
                                    return@Button
                                }
                                if (trimmedAddress.isEmpty()) {
                                    errorMessage = "कृपया अपना पता या गाँव दर्ज करें (उदा. कुचेरा)।"
                                    return@Button
                                }

                                if (selectedRole == "admin") {
                                    val code = adminCodeInput.trim()
                                    if (code != "992942") {
                                        val newWrong = wrongAdminAttempts + 1
                                        wrongAdminAttempts = newWrong
                                        if (newWrong >= 3) {
                                            selectedRole = "member"
                                            alertNotice = "⚠️ 3 बार गलत कोड दर्ज किया गया। सुरक्षा कारणों से आपको सदस्य मोड (Member Mode) में लॉगिन किया गया है।"
                                            errorMessage = null
                                            onLoginSuccess(
                                                trimmedName,
                                                trimmedPhone,
                                                trimmedAddress,
                                                selectedPhotoUri,
                                                instagramId.trim(),
                                                false
                                            )
                                        } else {
                                            errorMessage = "❌ गलत एडमिन कोड! प्रयास ($newWrong/3)। 3 बार गलत होने पर सदस्य मोड में लॉगिन हो जाएगा।"
                                        }
                                        return@Button
                                    } else {
                                        // Admin success
                                        onLoginSuccess(
                                            trimmedName,
                                            trimmedPhone,
                                            trimmedAddress,
                                            selectedPhotoUri,
                                            instagramId.trim(),
                                            true
                                        )
                                    }
                                } else {
                                    // Member mode success
                                    onLoginSuccess(
                                        trimmedName,
                                        trimmedPhone,
                                        trimmedAddress,
                                        selectedPhotoUri,
                                        instagramId.trim(),
                                        false
                                    )
                                }
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
                                text = "सत्यापित करें एवं ऐप शुरू करें ➔",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        }
                    }
                }
            }
        }
    }
