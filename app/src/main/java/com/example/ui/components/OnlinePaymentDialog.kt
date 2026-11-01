package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.MemberEntity
import com.example.data.PaymentRequestEntity
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenDark
import com.example.ui.theme.PaidGreenLight
import com.example.ui.theme.PendingRed
import com.example.ui.theme.PendingRedDark
import com.example.ui.theme.SaffronGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun launchUpiIntent(
    context: Context,
    packageName: String?,
    upiId: String,
    payeeName: String = "युवा शक्ति परिवार कुचेरा",
    amount: String = ""
) {
    try {
        val uriBuilder = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("cu", "INR")
        if (amount.isNotBlank()) {
            uriBuilder.appendQueryParameter("am", amount)
        }
        val uri = uriBuilder.build()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (!packageName.isNullOrBlank()) {
            intent.setPackage(packageName)
        }
        val pm = context.packageManager
        if (intent.resolveActivity(pm) != null) {
            context.startActivity(intent)
        } else {
            val genericIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(Intent.createChooser(genericIntent, "UPI ऐप चुनें"))
        }
    } catch (e: Exception) {
        Toast.makeText(context, "UPI ऐप खोलने में त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlinePaymentDialog(
    customQrCodeUri: String,
    isAdmin: Boolean,
    members: List<MemberEntity>,
    pendingRequestsCount: Int,
    upiId: String = "7850983600@ibl",
    onUpdateUpiId: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onUploadQrCode: (String) -> Unit,
    onRequestVerificationClick: () -> Unit,
    onViewPendingRequestsClick: () -> Unit,
    onSubmitDirectDeposit: (member: MemberEntity, mode: String, utr: String) -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val dialogBg = if (isDark) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val safeUpi = remember(upiId) {
        val s = upiId.trim()
        if (s.isBlank() || s.contains("850mbhati") || s.contains("77850983600") || s.contains("777850983600") || s.count { it == '@' } != 1 || !s.contains("@")) {
            "7850983600@ibl"
        } else {
            s
        }
    }
    var currentUpiId by remember(safeUpi) { mutableStateOf(safeUpi) }
    var showEditUpiDialog by remember { mutableStateOf(false) }
    var newUpiInput by remember { mutableStateOf(upiId) }

    var showDirectSubmit by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf(members.firstOrNull { it.status == "Pending" } ?: members.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var utrNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val qrPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadQrCode(uri.toString())
            Toast.makeText(context, "QR कोड फोटो अपडेट हो गई!", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBg,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "QR Code",
                        tint = if (isDark) SaffronGold else DeepNavy,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "स्कैन करें और भुगतान करें",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textPrimary
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = textSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // QR Code Container Box
                Surface(
                    color = Color(0xFF1E1B4B),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🚩 युवा शक्ति ग्रुप, भाटी परिवार",
                            color = Color(0xFFFEF3C7),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "मासिक अमावस्या फंड • परस्पर सहयोग",
                            color = Color(0xFFC7D2FE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actual QR Code Display: Custom Image or Vector QR
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .size(210.dp)
                                .padding(4.dp)
                        ) {
                            if (customQrCodeUri.isNotBlank()) {
                                AsyncImage(
                                    model = customQrCodeUri,
                                    contentDescription = "Group Payment QR Code",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_phonepe_qr_official),
                                    contentDescription = "Official PhonePe QR Code (7850983600@ibl)",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "📷 PhonePe, GPay, Paytm या किसी भी UPI ऐप से स्कैन करें",
                            color = Color(0xFFE0E7FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        // Admin QR Upload Button
                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFF312E81),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable {
                                    qrPhotoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Upload,
                                        contentDescription = "Upload QR",
                                        tint = SaffronGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (customQrCodeUri.isNotBlank()) "QR कोड बदलें" else "एडमिन: अपना QR कोड फोटो अपलोड करें",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // UPI ID & PhonePe/GPay number card with Copy & Edit
                Surface(
                    color = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else BorderSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📱 ऑफिशियल UPI ID / फ़ोनपे नंबर",
                                    fontSize = 11.sp,
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = currentUpiId,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) SaffronGold else DeepNavy
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (isDark) Color(0xFF334155) else Color(0xFFEEF2FF),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", currentUpiId))
                                        Toast.makeText(context, "UPI ID कॉपी हो गई: $currentUpiId", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy UPI ID",
                                            tint = if (isDark) SaffronGold else DeepNavy,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "कॉपी",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else DeepNavy
                                        )
                                    }
                                }

                                if (isAdmin) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = if (showEditUpiDialog) PendingRed else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SaffronGold),
                                        modifier = Modifier.clickable { showEditUpiDialog = !showEditUpiDialog }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit UPI",
                                                tint = if (showEditUpiDialog) Color.White else Color(0xFF92400E),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = if (showEditUpiDialog) "रद्द ✖" else "बदलें ✎",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (showEditUpiDialog) Color.White else Color(0xFF92400E)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (showEditUpiDialog && isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newUpiInput,
                                onValueChange = { newUpiInput = it },
                                label = { Text("नया UPI ID या Google Pay / PhonePe नंबर") },
                                placeholder = { Text("उदा. 9929xxxxxx@ybl या username@upi") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    if (newUpiInput.isNotBlank()) {
                                        currentUpiId = newUpiInput.trim()
                                        onUpdateUpiId(currentUpiId)
                                        showEditUpiDialog = false
                                        Toast.makeText(context, "UPI ID सफलतापूर्वक अपडेट हुई!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("सुरक्षित करें", color = Color(0xFF451A03), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 1-Tap Direct UPI Payment Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // PhonePe Button
                    Button(
                        onClick = {
                            launchUpiIntent(
                                context = context,
                                packageName = "com.phonepe.app",
                                upiId = currentUpiId,
                                payeeName = "Yuva Shakti Parivar"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(text = "💜 PhonePe", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                            Text(text = "सीधा भुगतान", fontSize = 10.sp, color = Color(0xFFE9D5FF))
                        }
                    }

                    // Google Pay Button
                    Button(
                        onClick = {
                            launchUpiIntent(
                                context = context,
                                packageName = "com.google.android.apps.nbu.paisa.user",
                                upiId = currentUpiId,
                                payeeName = "Yuva Shakti Parivar"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(text = "💙 Google Pay", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                            Text(text = "सीधा भुगतान", fontSize = 10.sp, color = Color(0xFFBFDBFE))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Other UPI Apps Button
                OutlinedButton(
                    onClick = {
                        launchUpiIntent(
                            context = context,
                            packageName = null,
                            upiId = currentUpiId,
                            payeeName = "Yuva Shakti Parivar"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚡ अन्य UPI ऐप से भुगतान (Paytm, BHIM)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFF93C5FD) else DeepNavy
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Action 1: "मैंने भुगतान कर दिया है - जमा अनुरोध व स्क्रीनशॉट भेजें"
                Button(
                    onClick = {
                        onRequestVerificationClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PaidGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_request_deposit_verification")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "मैंने भुगतान कर दिया है - स्क्रीनशॉट भेजें",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }

                // If admin, show Pending Verification Requests button
                if (isAdmin && pendingRequestsCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewPendingRequestsClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PendingActions,
                                    contentDescription = null,
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "लंबित भुगतान अनुरोध ($pendingRequestsCount)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                            }
                            Text(
                                text = "देखें व स्वीकृत करें →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle for direct entry
                Text(
                    text = if (showDirectSubmit) "▲ सीधा UTR/पावती फॉर्म छुपाएं" else "▼ क्या आप सीधे UTR नंबर से जमा करना चाहते हैं?",
                    fontSize = 11.5.sp,
                    color = DeepNavy,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { showDirectSubmit = !showDirectSubmit }
                        .padding(vertical = 4.dp)
                )

                AnimatedVisibility(visible = showDirectSubmit) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expandedDropdown,
                            onExpandedChange = { expandedDropdown = !expandedDropdown },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedMember?.let { "${it.name} (अंशदान: ₹${it.monthlyAmount})" } ?: "सदस्य चुनें",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("सदस्य चुनें") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false }
                            ) {
                                members.forEach { m ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = m.name, fontSize = 12.5.sp)
                                                Text(
                                                    text = if (m.status == "Paid") "जमा ✓" else "बाकी",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (m.status == "Paid") PaidGreen else PendingRed
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedMember = m
                                            expandedDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = utrNumber,
                            onValueChange = { utrNumber = it; errorMessage = null },
                            label = { Text("12-अंकों का UTR / Transaction ID") },
                            placeholder = { Text("उदा. 430129485721") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = PendingRed,
                                fontSize = 11.5.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val member = selectedMember
                                if (member == null) {
                                    errorMessage = "कृपया सदस्य चुनें"
                                    return@Button
                                }
                                if (utrNumber.isBlank()) {
                                    errorMessage = "कृपया UTR या Ref ID दर्ज करें"
                                    return@Button
                                }
                                onSubmitDirectDeposit(member, "QR कोड", utrNumber.trim())
                                Toast.makeText(context, "${member.name} का जमा दर्ज हुआ!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("सीधा जमा दर्ज करें", fontWeight = FontWeight.Bold)
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
                Text("बंद करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentVerificationDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onSubmitRequest: (member: MemberEntity, amount: Int, screenshotUri: String, utrOrRef: String, note: String) -> Unit
) {
    val context = LocalContext.current
    var selectedMember by remember { mutableStateOf(members.firstOrNull { it.status == "Pending" } ?: members.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var amountStr by remember { mutableStateOf((selectedMember?.monthlyAmount ?: 0).let { if (it > 0) it.toString() else "" }) }
    var screenshotUri by remember { mutableStateOf<String?>(null) }
    var utrOrRef by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("मैंने भुगतान कर दिया है, कृपया मेरा भुगतान जमा दर्ज करें।") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val screenshotPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            screenshotUri = uri.toString()
            errorMessage = null
        }
    }

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
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PaidGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "भुगतान सत्यापन अनुरोध",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
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
                        text = "यदि आपने ऑनलाइन भुगतान कर दिया है, तो नीचे विवरण व स्क्रीनशॉट संलग्न करके अनुरोध भेजें।",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Member Selection
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedMember?.let { "${it.name} (${if (it.status == "Paid") "जमा ✓" else "बाकी"})" } ?: "सदस्य चुनें",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("भुगतान करने वाले सदस्य का नाम *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = m.name, fontSize = 13.sp)
                                            Text(
                                                text = if (m.status == "Paid") "जमा ✓" else "बाकी",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (m.status == "Paid") PaidGreen else PendingRed
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedMember = m
                                        if (amountStr.isBlank() && m.monthlyAmount > 0) {
                                            amountStr = m.monthlyAmount.toString()
                                        }
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Amount
                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                        label = { Text("जमा की गई राशि (₹) *") },
                        placeholder = { Text("उदा. 500, 1000, 1500, 2000...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Screenshot Proof Picker (Mandatory / Recommended)
                item {
                    Surface(
                        color = if (screenshotUri != null) Color(0xFFF0FDF4) else Color(0xFFEEF2FF),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (screenshotUri != null) Color(0xFF86EFAC) else DeepNavy
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                screenshotPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (screenshotUri != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = PaidGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "स्क्रीनशॉट प्रमाण जुड़ा हुआ है",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = PaidGreenDark
                                        )
                                    }
                                    Text(
                                        text = "बदलें 🔄",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                AsyncImage(
                                    model = screenshotUri,
                                    contentDescription = "Payment Screenshot",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Add screenshot",
                                    tint = DeepNavy,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "📸 पेमेंट स्क्रीनशॉट प्रमाण जोड़ें *",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                                Text(
                                    text = "PhonePe/GPay/Paytm भुगतान रसीद की फोटो चुनें",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // UTR or Ref ID
                item {
                    OutlinedTextField(
                        value = utrOrRef,
                        onValueChange = { utrOrRef = it },
                        label = { Text("UTR / Ref ID (वैकल्पिक)") },
                        placeholder = { Text("उदा. 430129485721") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Note
                item {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("संदेश / टिप्पणी") },
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage ?: "",
                            color = PendingRed,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val member = selectedMember
                    if (member == null) {
                        errorMessage = "कृपया सदस्य चुनें"
                        return@Button
                    }
                    val amt = amountStr.toIntOrNull() ?: 0
                    if (amt <= 0) {
                        errorMessage = "कृपया मान्य राशि दर्ज करें"
                        return@Button
                    }
                    onSubmitRequest(member, amt, screenshotUri ?: "", utrOrRef.trim(), note.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaidGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("अनुरोध भेजें", fontWeight = FontWeight.Bold)
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
fun PendingPaymentRequestsDialog(
    requests: List<PaymentRequestEntity>,
    onDismiss: () -> Unit,
    onViewScreenshot: (String) -> Unit,
    onApprove: (PaymentRequestEntity) -> Unit,
    onReject: (PaymentRequestEntity) -> Unit
) {
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
                        imageVector = Icons.Default.PendingActions,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "लंबित भुगतान सत्यापन अनुरोध (${requests.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            if (requests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = PaidGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "कोई लंबित अनुरोध नहीं है।\nसभी सदस्यों के भुगतान रिकॉर्ड अपडेटेड हैं।",
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(requests) { req ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = req.memberName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "राशि: ₹${req.amount} • दिनांक: ${req.requestDate}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PaidGreenDark,
                                            modifier = Modifier.padding(top = 1.dp)
                                        )
                                    }

                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "सत्यापन बाकी",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (req.utrOrRef.isNotBlank()) {
                                    Text(
                                        text = "UTR / Ref: ${req.utrOrRef}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                if (req.note.isNotBlank()) {
                                    Text(
                                        text = "संदेश: \"${req.note}\"",
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Screenshot Preview Row
                                if (req.screenshotUri.isNotBlank()) {
                                    Surface(
                                        color = Color(0xFFEEF2FF),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onViewScreenshot(req.screenshotUri) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                AsyncImage(
                                                    model = req.screenshotUri,
                                                    contentDescription = "Proof",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "📸 स्क्रीनशॉट प्रमाण संलग्न",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = DeepNavy
                                                )
                                            }
                                            Text(
                                                text = "देखें 🔍",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepNavy
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Approve / Reject Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onApprove(req) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PaidGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "स्वीकार करें ✓",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { onReject(req) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PendingRed),
                                        modifier = Modifier.weight(0.7f)
                                    ) {
                                        Text(
                                            text = "अस्वीकार",
                                            fontSize = 11.sp
                                        )
                                    }
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
                Text("बंद करें")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ScreenshotViewerDialog(
    screenshotUri: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "पेमेंट स्क्रीनशॉट प्रमाण",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = screenshotUri,
                    contentDescription = "Full Screenshot",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("वापस जाएँ", color = DeepNavy, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UpiQrVisualPattern(
    upiId: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cols = 21
        val cellSize = w / cols

        drawRect(color = Color.White, size = size)

        fun drawFinder(cx: Float, cy: Float) {
            val finderSize = cellSize * 7
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(cx, cy),
                size = Size(finderSize, finderSize),
                cornerRadius = CornerRadius(cellSize * 1.2f, cellSize * 1.2f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cx + cellSize, cy + cellSize),
                size = Size(finderSize - (cellSize * 2), finderSize - (cellSize * 2)),
                cornerRadius = CornerRadius(cellSize * 0.8f, cellSize * 0.8f)
            )
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(cx + (cellSize * 2), cy + (cellSize * 2)),
                size = Size(finderSize - (cellSize * 4), finderSize - (cellSize * 4)),
                cornerRadius = CornerRadius(cellSize * 0.5f, cellSize * 0.5f)
            )
        }

        drawFinder(0f, 0f)
        drawFinder(w - (cellSize * 7), 0f)
        drawFinder(0f, h - (cellSize * 7))

        val seed = upiId.hashCode()
        val random = java.util.Random(seed.toLong())

        for (r in 0 until cols) {
            for (c in 0 until cols) {
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= cols - 8
                val inBottomLeft = r >= cols - 8 && c < 8
                if (!inTopLeft && !inTopRight && !inBottomLeft) {
                    if (random.nextBoolean()) {
                        drawRect(
                            color = Color(0xFF0F172A),
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 0.92f, cellSize * 0.92f)
                        )
                    }
                }
            }
        }

        // Center emblem
        val emblemSize = cellSize * 4.5f
        val emblemX = (w - emblemSize) / 2
        val emblemY = (h - emblemSize) / 2
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(emblemX, emblemY),
            size = Size(emblemSize, emblemSize),
            cornerRadius = CornerRadius(cellSize, cellSize)
        )
        drawRoundRect(
            color = Color(0xFFDC2626),
            topLeft = Offset(emblemX + (cellSize * 0.6f), emblemY + (cellSize * 0.6f)),
            size = Size(emblemSize - (cellSize * 1.2f), emblemSize - (cellSize * 1.2f)),
            cornerRadius = CornerRadius(cellSize * 0.6f, cellSize * 0.6f)
        )
    }
}
