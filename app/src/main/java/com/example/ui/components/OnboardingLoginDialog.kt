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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold
import kotlinx.coroutines.launch

@Composable
fun OnboardingLoginDialog(
    onLoginWithCode: (code: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onRegisterWithCode: (
        name: String,
        phone: String,
        address: String,
        memberCode: String,
        photoUri: String,
        insta: String,
        onResult: (Boolean, String) -> Unit
    ) -> Unit,
    onCheckCodeTaken: suspend (String) -> Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login with Code, 1: Register New Member

    // Login tab state
    var memberCodeInput by remember { mutableStateOf("") }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }

    // Registration tab state
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var addressVillage by remember { mutableStateOf("कुचेरा") }
    var desiredCode by remember { mutableStateOf("") }
    var isCodeTaken by remember { mutableStateOf(false) }
    var isCheckingCode by remember { mutableStateOf(false) }
    var instagramId by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf("") }
    var regErrorMessage by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
            regErrorMessage = null
        }
    }

    // Check code uniqueness whenever desiredCode changes
    LaunchedEffect(desiredCode) {
        val trimmed = desiredCode.trim()
        if (trimmed.length >= 4) {
            isCheckingCode = true
            isCodeTaken = onCheckCodeTaken(trimmed)
            isCheckingCode = false
        } else {
            isCodeTaken = false
            isCheckingCode = false
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
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Official Logo with Golden Ring
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .border(3.dp, SaffronGold, CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                val logoPainter = runCatching {
                    painterResource(id = R.drawable.ic_app_foreground_1793369456038)
                }.getOrNull()
                if (logoPainter != null) {
                    Image(
                        painter = logoPainter,
                        contentDescription = "युवा शक्ति ग्रुप लोगो",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(text = "🚩", fontSize = 34.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Branding Header
            Text(
                text = "🚩 युवा शक्ति ग्रुप, भाटी परिवार कुचेरा",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "सदस्य पहचान एवं डिजिटल पारदर्शी बहीखाता",
                fontSize = 11.5.sp,
                color = Color(0xFFC7D2FE),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Auth Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = BorderStroke(1.2.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Top Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = DeepNavy
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                loginErrorMessage = null
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (selectedTab == 0) DeepNavy else textSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "🔑 कोड से लॉगिन",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                regErrorMessage = null
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (selectedTab == 1) DeepNavy else textSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "📝 नया पंजीकरण",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }
                        )
                    }

                    // TAB 0: Direct Login via Member Code or Admin Code
                    if (selectedTab == 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "🔐 व्यक्तिगत सदस्य / एडमिन कोड द्वारा प्रवेश",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF1E40AF)
                                    )
                                    Text(
                                        text = "प्रत्येक सदस्य का एक विशिष्ट कोड है जिससे उनकी ऑनलाइन पहचान होती है। कृपया अपना व्यक्तिगत सदस्य कोड दर्ज करें।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.padding(top = 3.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = memberCodeInput,
                                onValueChange = {
                                    if (it.length <= 8) {
                                        memberCodeInput = it.trim()
                                        loginErrorMessage = null
                                    }
                                },
                                label = { Text("अपना व्यक्तिगत सदस्य कोड दर्ज करें *") },
                                placeholder = { Text("उदा. 1001 या आपका कोड") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SaffronGold)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_member_code"),
                                colors = inputColors
                            )

                            if (loginErrorMessage != null) {
                                Text(
                                    text = loginErrorMessage ?: "",
                                    color = PendingRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    val code = memberCodeInput.trim()
                                    if (code.isBlank()) {
                                        loginErrorMessage = "कृपया अपना सदस्य कोड दर्ज करें।"
                                        return@Button
                                    }
                                    isLoggingIn = true
                                    onLoginWithCode(code) { success, msg ->
                                        isLoggingIn = false
                                        if (!success) {
                                            loginErrorMessage = msg
                                        }
                                    }
                                },
                                enabled = !isLoggingIn,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_submit_code_login"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold)
                            ) {
                                if (isLoggingIn) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DeepNavy, strokeWidth = 2.dp)
                                } else {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeepNavy)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "कोड सत्यापित करें व प्रवेश करें ➔",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy
                                    )
                                }
                            }

                            // Switch to registration tip
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTab = 1
                                        regErrorMessage = null
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "💡 ", fontSize = 13.sp)
                                    Text(
                                        text = "यदि आप पहली बार जुड़े हैं तो यहाँ क्लिक करके अपना नया सदस्य कोड बनाएं व पंजीकरण करें।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    } else {
                        // TAB 1: New Member Registration with Unique Code
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "📝 नया सदस्य खाता बनाएं",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = textPrimary
                            )

                            // Profile Photo Picker
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
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
                                                .size(22.dp)
                                                .align(Alignment.BottomEnd)
                                                .background(SaffronGold, CircleShape)
                                                .border(1.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "बदलें",
                                                tint = DeepNavy,
                                                modifier = Modifier.size(12.dp)
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
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = "फोटो चुनें",
                                                fontSize = 9.sp,
                                                color = Color(0xFF64748B),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Full Name
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    regErrorMessage = null
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

                            // Phone Number
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = {
                                    if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                                        phoneNumber = it
                                        regErrorMessage = null
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

                            // Village / City
                            OutlinedTextField(
                                value = addressVillage,
                                onValueChange = {
                                    addressVillage = it
                                    regErrorMessage = null
                                },
                                label = { Text("गाँव / शहर का नाम * (उदा. कुचेरा)") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = iconTint)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = inputColors
                            )

                            // UNIQUE MEMBER CODE INPUT SECTION
                            Surface(
                                color = if (isCodeTaken) Color(0xFFFEF2F2) else if (desiredCode.length >= 4) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isCodeTaken) PendingRed else if (desiredCode.length >= 4) PaidGreen else Color(0xFFFDE68A)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🔑 अपना विशिष्ट सदस्य कोड चुनें * (4-6 अंक)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCodeTaken) PendingRed else DeepNavy
                                        )
                                        // Auto-generate code button
                                        Surface(
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.clickable {
                                                coroutineScope.launch {
                                                    var candidate = (1001..9999).random().toString()
                                                    while (onCheckCodeTaken(candidate) || candidate == "992942") {
                                                        candidate = (1001..9999).random().toString()
                                                    }
                                                    desiredCode = candidate
                                                    regErrorMessage = null
                                                }
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(11.dp), tint = DeepNavy)
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(text = "ऑटो कोड", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = desiredCode,
                                        onValueChange = {
                                            if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                                desiredCode = it
                                                regErrorMessage = null
                                            }
                                        },
                                        placeholder = { Text("उदा. 1025 या 5588") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = DeepNavy)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = inputColors
                                    )

                                    if (isCheckingCode) {
                                        Text(
                                            text = "⏳ कोड उपलब्धता जांची जा रही है...",
                                            fontSize = 11.sp,
                                            color = Color(0xFF2563EB),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    } else if (isCodeTaken) {
                                        Text(
                                            text = "⚠️ यह कोड '$desiredCode' पहले से किसी सदस्य द्वारा उपयोग में है! कृपया दूसरा कोड चुनें।",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PendingRed,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    } else if (desiredCode.length >= 4) {
                                        Text(
                                            text = "✅ यह कोड उपलब्ध है! यह आपका व्यक्तिगत लॉगिन कोड रहेगा।",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Instagram ID
                            OutlinedTextField(
                                value = instagramId,
                                onValueChange = { instagramId = it },
                                label = { Text("इंस्टाग्राम आईडी (वैकल्पिक)") },
                                placeholder = { Text("उदा. bhati_kuchera") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFE1306C))
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = inputColors
                            )

                            if (regErrorMessage != null) {
                                Text(
                                    text = "⚠️ $regErrorMessage",
                                    color = PendingRed,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Register Button
                            Button(
                                onClick = {
                                    val trimmedName = fullName.trim()
                                    val trimmedPhone = phoneNumber.trim()
                                    val trimmedAddress = addressVillage.trim()
                                    val trimmedCode = desiredCode.trim()

                                    if (trimmedName.isEmpty()) {
                                        regErrorMessage = "कृपया अपना पूरा नाम दर्ज करें।"
                                        return@Button
                                    }
                                    if (trimmedPhone.length < 10) {
                                        regErrorMessage = "कृपया 10-अंकों का वैध मोबाइल नंबर दर्ज करें।"
                                        return@Button
                                    }
                                    if (trimmedAddress.isEmpty()) {
                                        regErrorMessage = "कृपया गाँव / शहर का नाम दर्ज करें।"
                                        return@Button
                                    }
                                    if (trimmedCode.length < 4) {
                                        regErrorMessage = "कृपया कम से कम 4-अंकों का विशिष्ट सदस्य कोड चुनें।"
                                        return@Button
                                    }
                                    if (isCodeTaken) {
                                        regErrorMessage = "यह कोड पहले से उपयोग में है, कृपया दूसरा कोड चुनें।"
                                        return@Button
                                    }

                                    isRegistering = true
                                    onRegisterWithCode(
                                        trimmedName,
                                        trimmedPhone,
                                        trimmedAddress,
                                        trimmedCode,
                                        selectedPhotoUri,
                                        instagramId.trim()
                                    ) { success, msg ->
                                        isRegistering = false
                                        if (!success) {
                                            regErrorMessage = msg
                                        }
                                    }
                                },
                                enabled = !isRegistering && !isCodeTaken && desiredCode.length >= 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_submit_registration"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold)
                            ) {
                                if (isRegistering) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DeepNavy, strokeWidth = 2.dp)
                                } else {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeepNavy)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "पंजीकरण करें एवं ऐप शुरू करें ➔",
                                        fontSize = 14.sp,
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
    }
}
