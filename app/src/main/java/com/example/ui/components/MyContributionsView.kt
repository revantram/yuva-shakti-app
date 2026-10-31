package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DepositNotificationEntity
import com.example.data.MemberEntity
import com.example.data.PaymentRequestEntity
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold

@Composable
fun MyContributionsView(
    members: List<MemberEntity>,
    activeMemberId: Long,
    notifications: List<DepositNotificationEntity>,
    paymentRequests: List<PaymentRequestEntity>,
    onSelectMember: (Long) -> Unit,
    onQuickPayClick: () -> Unit,
    onShareReceipt: (DepositNotificationEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedMember = members.find { it.id == activeMemberId } ?: members.firstOrNull()
    var showMemberPicker by remember { mutableStateOf(false) }

    val memberPayments = remember(selectedMember, notifications) {
        if (selectedMember == null) emptyList()
        else notifications.filter { it.memberName == selectedMember.name || it.memberId == selectedMember.id }
    }

    val memberRequests = remember(selectedMember, paymentRequests) {
        if (selectedMember == null) emptyList()
        else paymentRequests.filter { it.memberId == selectedMember.id || it.memberName == selectedMember.name }
    }

    val isPaid = selectedMember?.status == "Paid"
    val monthlyAmt = selectedMember?.monthlyAmount ?: 0
    val totalPaidSoFar = remember(memberPayments) {
        memberPayments.sumOf { it.amount }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_contributions_view"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Member Profile & Switcher Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
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
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF2FF))
                                    .border(1.5.dp, DeepNavy, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedMember?.name?.take(1) ?: "म",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = selectedMember?.name ?: "सदस्य चुनें",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "📱 ${selectedMember?.phone ?: "-"}" + if (!selectedMember?.villageCity.isNullOrBlank()) " • 📍 ${selectedMember?.villageCity}" else "",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Switch Member Button
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { showMemberPicker = !showMemberPicker }
                                .testTag("btn_switch_member")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "बदलें",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                            }
                        }
                    }

                    if (showMemberPicker) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "परिवार का सदस्य चुनें:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        members.forEach { m ->
                            Surface(
                                color = if (m.id == selectedMember?.id) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        onSelectMember(m.id)
                                        showMemberPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = m.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (m.id == selectedMember?.id) FontWeight.Bold else FontWeight.Normal,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = if (m.status == "Paid") "🟢 जमा" else "🔴 बाकी",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (m.status == "Paid") Color(0xFF15803D) else Color(0xFFB91C1C)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Current Month Amavasya Dues Status
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isPaid) Color(0xFFF0FDF4) else Color(0xFFFFF1F2)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPaid) Color(0xFFBBF7D0) else Color(0xFFFECDD3)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = if (isPaid) Color(0xFF15803D) else Color(0xFFB91C1C),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isPaid) "इस माह का अंशदान: पूर्ण जमा ✅" else "इस माह का अंशदान: बकाया ⏳",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPaid) Color(0xFF14532D) else Color(0xFF991B1B)
                                )
                                Text(
                                    text = if (isPaid) "अमावस्या चक्र में आपका सहयोग दर्ज हो चुका है" else "कृपया समय पर अपना सहयोग जमा कराएं",
                                    fontSize = 11.sp,
                                    color = if (isPaid) Color(0xFF166534) else Color(0xFFB91C1C)
                                )
                            }
                        }

                        if (!isPaid) {
                            Button(
                                onClick = onQuickPayClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_my_quick_pay")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "पे करें",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "मासिक अंशदान राशि:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = if (monthlyAmt > 0) "₹$monthlyAmt" else "सहमति अनुसार",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "जमा की तारीख:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = if (isPaid && !selectedMember?.paidDate.isNullOrBlank()) selectedMember?.paidDate ?: "-" else "प्रतीक्षारत",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPaid) Color(0xFF15803D) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        // 3. Lifetime Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "कुल योगदान", fontSize = 11.sp, color = Color(0xFF4338CA), fontWeight = FontWeight.Medium)
                        Text(
                            text = "₹$totalPaidSoFar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF312E81),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "कुल पावतियां", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Medium)
                        Text(
                            text = "${memberPayments.size} रसीदें",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // 4. Payment History & Receipts
        item {
            Text(
                text = "🧾 आपकी अंशदान पावतियां व इतिहास:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (memberPayments.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🧾", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "कोई पावती नहीं मिली",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "जब आपका अंशदान जमा होगा, उसकी डिजिटल पावती यहाँ दिखेगी।",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        } else {
            items(memberPayments, key = { it.id }) { notif ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFDCFCE7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "अंशदान: ₹${notif.amount}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "तारीख: ${notif.paymentDate} • ${notif.paymentMode}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        IconButton(onClick = { onShareReceipt(notif) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
