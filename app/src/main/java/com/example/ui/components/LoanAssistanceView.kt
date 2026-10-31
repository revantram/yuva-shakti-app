package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoanRequestEntity
import com.example.data.MemberEntity
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold

@Composable
fun LoanAssistanceView(
    loanRequests: List<LoanRequestEntity>,
    members: List<MemberEntity>,
    currentVoterId: Long,
    isAdmin: Boolean,
    onRequestNewLoanClick: () -> Unit,
    onVoteClick: (LoanRequestEntity, Boolean) -> Unit,
    onAdminEditClick: (LoanRequestEntity) -> Unit,
    onAdminStatusChange: (LoanRequestEntity, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterStatus by remember { mutableStateOf("all") }

    val filteredList = when (filterStatus) {
        "pending" -> loanRequests.filter { it.status == "Pending" }
        "approved" -> loanRequests.filter { it.status == "Approved" }
        "repaid" -> loanRequests.filter { it.status == "Repaid" }
        else -> loanRequests
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("loan_assistance_view"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(16.dp))
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
                            Text(text = "🤝 ", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "पारस्परिक सहायता व ऋण मंच",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF14532D)
                                )
                                Text(
                                    text = "पारिवारिक फंड से व्यक्तिगत कार्य हेतु सहायता एवं सामूहिक सहमति",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534)
                                )
                            }
                        }

                        Button(
                            onClick = onRequestNewLoanClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_request_loan")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "अनुरोध करें",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterStatus == "all",
                    onClick = { filterStatus = "all" },
                    label = { Text("सभी (${loanRequests.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF15803D),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filterStatus == "pending",
                    onClick = { filterStatus = "pending" },
                    label = {
                        Text(
                            "प्रतीक्षारत (${loanRequests.count { it.status == "Pending" }})",
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFB45309),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filterStatus == "approved",
                    onClick = { filterStatus = "approved" },
                    label = {
                        Text(
                            "स्वीकृत (${loanRequests.count { it.status == "Approved" }})",
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1E40AF),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🤝", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "अभी कोई सहायता अनुरोध नहीं है",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "यदि किसी सदस्य को व्यक्तिगत कार्य हेतु फंड की आवश्यकता है, तो ऊपर दिए बटन से अनुरोध भेज सकते हैं।",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { req ->
                LoanRequestCard(
                    request = req,
                    currentVoterId = currentVoterId,
                    isAdmin = isAdmin,
                    onVoteClick = { isUp -> onVoteClick(req, isUp) },
                    onAdminEditClick = { onAdminEditClick(req) },
                    onAdminStatusChange = { newStatus -> onAdminStatusChange(req, newStatus) }
                )
            }
        }
    }
}

@Composable
fun LoanRequestCard(
    request: LoanRequestEntity,
    currentVoterId: Long,
    isAdmin: Boolean,
    onVoteClick: (Boolean) -> Unit,
    onAdminEditClick: () -> Unit,
    onAdminStatusChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val voteMap = request.votedMemberIds.split(",")
        .filter { it.isNotBlank() && it.contains(":") }
        .associate {
            val parts = it.split(":")
            parts[0].toLongOrNull() to parts[1]
        }
    val myVote = voteMap[currentVoterId] // "up" or "down" or null

    val (statusLabel, statusColor, statusBg) = when (request.status) {
        "Approved" -> Triple("मंजूर / स्वीकृत", Color(0xFF15803D), Color(0xFFDCFCE7))
        "Rejected" -> Triple("अस्वीकृत", Color(0xFFB91C1C), Color(0xFFFEE2E2))
        "Repaid" -> Triple("वापस प्राप्त (पूर्ण)", Color(0xFF1E40AF), Color(0xFFDBEAFE))
        else -> Triple("प्रतीक्षारत (निर्णयाधीन)", Color(0xFFB45309), Color(0xFFFEF3C7))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .testTag("loan_card_${request.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Member Name + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.memberName.take(1),
                            color = Color(0xFF3730A3),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = request.memberName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "दिनांक: ${request.requestDate}" + if (request.memberPhone.isNotBlank()) " • 📞 ${request.memberPhone}" else "",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount requested highlight
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ज़रूरत राशि:",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "₹${java.text.NumberFormat.getIntegerInstance(java.util.Locale("en", "IN")).format(request.amountRequested)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Purpose
            Surface(
                color = Color(0xFFFFFBEB),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "📝 कार्य / कारण:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = request.purpose,
                        fontSize = 12.sp,
                        color = Color(0xFF78350F),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Return promise in days
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⏱️ ", fontSize = 12.sp)
                    Text(
                        text = "वापसी का वादा: ",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "मैं यह राशि ${request.returnPromiseDays} दिनों में लौटा दूंगा।",
                        fontSize = 11.5.sp,
                        color = Color(0xFF1D4ED8)
                    )
                }
            }

            if (request.adminNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🛡️ एडमिन टिप्पणी: ${request.adminNote}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Voting Section (Thumb Up & Thumb Down)
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "पारिवारिक सहमति:",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = when (myVote) {
                                "up" -> "आपका मत: 👍 सहमत"
                                "down" -> "आपका मत: 👎 असहमत"
                                else -> "अभी तक मत नहीं दिया"
                            },
                            fontSize = 9.5.sp,
                            color = if (myVote != null) Color(0xFF0F766E) else Color(0xFF64748B)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Thumbs UP button
                        Surface(
                            color = if (myVote == "up") Color(0xFFDCFCE7) else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (myVote == "up") Color(0xFF16A34A) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .clickable { onVoteClick(true) }
                                .testTag("btn_thumb_up_${request.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Support",
                                    tint = if (myVote == "up") Color(0xFF15803D) else Color(0xFF475569),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${request.upVotes} समर्थन",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (myVote == "up") Color(0xFF15803D) else Color(0xFF334155)
                                )
                            }
                        }

                        // Thumbs DOWN button
                        Surface(
                            color = if (myVote == "down") Color(0xFFFEE2E2) else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (myVote == "down") Color(0xFFDC2626) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .clickable { onVoteClick(false) }
                                .testTag("btn_thumb_down_${request.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDown,
                                    contentDescription = "Oppose",
                                    tint = if (myVote == "down") Color(0xFFB91C1C) else Color(0xFF475569),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${request.downVotes} असहमत",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (myVote == "down") Color(0xFFB91C1C) else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }

            // Admin Control Panel for this request
            if (isAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛡️ एडमिन अधिकार:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC2410C)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Edit button
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDBA74)),
                                modifier = Modifier.clickable { onAdminEditClick() }
                            ) {
                                Text(
                                    text = "संपादित करें ✎",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            if (request.status == "Pending") {
                                Surface(
                                    color = Color(0xFF15803D),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.clickable { onAdminStatusChange("Approved") }
                                ) {
                                    Text(
                                        text = "स्वीकृत ✓",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            } else if (request.status == "Approved") {
                                Surface(
                                    color = Color(0xFF1E40AF),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.clickable { onAdminStatusChange("Repaid") }
                                ) {
                                    Text(
                                        text = "वापस प्राप्त ✓",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
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

@Composable
fun CreateLoanRequestDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onSubmit: (memberId: Long, name: String, phone: String, amount: Int, purpose: String, days: Int) -> Unit
) {
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var customName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var returnDaysText by remember { mutableStateOf("30") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🤝 ", fontSize = 20.sp)
                Text(
                    text = "नया सहायता / ऋण अनुरोध",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "परिवार कोष से सहायता हेतु विवरण भरें। यह समस्त सदस्यों को सूचना व सहमति हेतु भेजा जाएगा।",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = if (customName.isNotBlank()) customName else (selectedMember?.name ?: ""),
                    onValueChange = { customName = it },
                    label = { Text("सदस्य का नाम *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone.ifBlank { selectedMember?.phone ?: "" },
                    onValueChange = { phone = it },
                    label = { Text("मोबाइल नंबर") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                    label = { Text("ज़रूरत राशि (₹) *") },
                    placeholder = { Text("उदा. 5000, 10000, 20000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("कार्य / कारण (विवरण) *") },
                    placeholder = { Text("मुझे इस कार्य हेतु पैसों की आवश्यकता है...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = returnDaysText,
                    onValueChange = { returnDaysText = it.filter { char -> char.isDigit() } },
                    label = { Text("वापसी का वादा (दिनों में) *") },
                    placeholder = { Text("उदा. 30, 60, 90 दिन") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (customName.isNotBlank()) customName else (selectedMember?.name ?: "")
                    val amt = amountText.toIntOrNull() ?: 0
                    val days = returnDaysText.toIntOrNull() ?: 30
                    if (finalName.isBlank()) {
                        errorMessage = "कृपया सदस्य का नाम दर्ज करें"
                        return@Button
                    }
                    if (amt <= 0) {
                        errorMessage = "कृपया वैध राशि दर्ज करें"
                        return@Button
                    }
                    if (purpose.isBlank()) {
                        errorMessage = "कृपया कार्य या कारण का विवरण लिखें"
                        return@Button
                    }
                    val memId = selectedMember?.id ?: 0L
                    onSubmit(memId, finalName, phone, amt, purpose, days)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
            ) {
                Text("अनुरोध सबमिट करें")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}

@Composable
fun EditLoanRequestDialog(
    request: LoanRequestEntity,
    onDismiss: () -> Unit,
    onSave: (updated: LoanRequestEntity) -> Unit
) {
    var memberName by remember { mutableStateOf(request.memberName) }
    var phone by remember { mutableStateOf(request.memberPhone) }
    var amountText by remember { mutableStateOf(request.amountRequested.toString()) }
    var purpose by remember { mutableStateOf(request.purpose) }
    var returnDaysText by remember { mutableStateOf(request.returnPromiseDays.toString()) }
    var adminNote by remember { mutableStateOf(request.adminNote) }
    var status by remember { mutableStateOf(request.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🛡️ एडमिन संपादन: सहायता अनुरोध",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    label = { Text("सदस्य का नाम") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("मोबाइल") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("राशि (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("कार्य / कारण") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = returnDaysText,
                    onValueChange = { returnDaysText = it.filter { c -> c.isDigit() } },
                    label = { Text("वापसी दिन") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = adminNote,
                    onValueChange = { adminNote = it },
                    label = { Text("एडमिन निर्णय / टिप्पणी") },
                    placeholder = { Text("उदा. पंचायत बैठक में स्वीकृत...") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Status selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Pending", "Approved", "Rejected", "Repaid").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = {
                                Text(
                                    when (s) {
                                        "Approved" -> "मंजूर"
                                        "Rejected" -> "अस्वीकृत"
                                        "Repaid" -> "पूर्ण"
                                        else -> "प्रतीक्षारत"
                                    },
                                    fontSize = 10.sp
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toIntOrNull() ?: request.amountRequested
                    val days = returnDaysText.toIntOrNull() ?: request.returnPromiseDays
                    val updated = request.copy(
                        memberName = memberName,
                        memberPhone = phone,
                        amountRequested = amt,
                        purpose = purpose,
                        returnPromiseDays = days,
                        adminNote = adminNote,
                        status = status
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
            ) {
                Text("सुरक्षित करें")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}
