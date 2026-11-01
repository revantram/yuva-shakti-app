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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberEntity
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenDark
import com.example.ui.theme.PaidGreenLight
import com.example.ui.theme.PendingRed
import com.example.ui.theme.PendingRedDark
import com.example.ui.theme.PendingRedLight

@Composable
fun MemberCard(
    member: MemberEntity,
    isAdmin: Boolean,
    isOnline: Boolean = false,
    isDark: Boolean = false,
    onCardClick: () -> Unit,
    onToggleStatus: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppReminder: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPaid = member.status == "Paid"
    val cardBg = if (isDark) (if (isPaid) Color(0xCC064E3B) else Color(0xCC450A0A)) else (if (isPaid) Color(0xD9F0FDF4) else Color(0xD9FFF1F2))
    val cardBorder = if (isDark) (if (isPaid) Color(0x6610B981) else Color(0x66F43F5E)) else (if (isPaid) Color(0xFFBBF7D0) else Color(0xFFFECDD3))
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .testTag("member_card_${member.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left avatar + details
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Initials with live presence dot
                    val avatarLetter = member.name.trim().take(1)
                    val avatarBg = if (isDark) (if (isPaid) Color(0xFF047857) else Color(0xFF7F1D1D)) else (if (isPaid) PaidGreenLight else PendingRedLight)
                    val avatarTextColor = if (isDark) (if (isPaid) Color(0xFFA7F3D0) else Color(0xFFFECDD3)) else (if (isPaid) PaidGreenDark else PendingRedDark)

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(avatarBg, CircleShape)
                                .border(1.5.dp, if (isPaid) Color(0xFF86EFAC) else Color(0xFFFDA4AF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = avatarLetter,
                                color = avatarTextColor,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(13.dp)
                                    .background(Color(0xFF16A34A), CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = member.name,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            if (member.notes.isNotBlank()) {
                                Surface(
                                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Text(
                                        text = member.notes,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Phone and payment line
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 3.dp)
                        ) {
                            val displayPhone = if (isAdmin || member.phone.length < 6) {
                                member.phone
                            } else {
                                member.phone.take(5) + "•••••"
                            }
                            val cleanCode = if (member.id == 1L || member.phone == "8696663615") "992942" else member.memberCode.ifBlank { (1000 + member.id).toString() }
                            Text(
                                text = displayPhone,
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                            Text(
                                text = " • 🔑 $cleanCode • ",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) SaffronGold else DeepNavy
                            )
                            if (isPaid) {
                                val modeText = if (member.paymentMode.isNotBlank()) "${member.paymentMode} द्वारा " else ""
                                Text(
                                    text = "${modeText}₹${member.monthlyAmount} प्राप्त",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF6EE7B7) else PaidGreenDark
                                )
                            } else {
                                Text(
                                    text = "₹${member.monthlyAmount} बकाया",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFCA5A5) else PendingRedDark
                                )
                            }
                        }

                        if (isPaid && member.paidDate.isNotBlank()) {
                            Text(
                                text = "तारीख: ${member.paidDate}",
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF047857),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Right Status / Admin Action
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    if (isAdmin) {
                        Surface(
                            color = if (isPaid) Color(0xFFFFE4E6) else PaidGreen,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .clickable { onToggleStatus() }
                                .border(
                                    1.dp,
                                    if (isPaid) Color(0xFFFECDD3) else PaidGreen,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Text(
                                text = if (isPaid) "बाकी करें" else "जमा करें",
                                color = if (isPaid) PendingRedDark else Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = if (isPaid) PaidGreenLight else PendingRedLight,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.border(
                                1.dp,
                                if (isPaid) Color(0xFFA7F3D0) else Color(0xFFFECDD3),
                                RoundedCornerShape(20.dp)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPaid) Icons.Default.Check else Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = if (isPaid) PaidGreenDark else PendingRedDark,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPaid) "जमा" else "बाकी",
                                    color = if (isPaid) PaidGreenDark else PendingRedDark,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Row (Call, WhatsApp Reminder, Edit/Delete)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call & WhatsApp reminders
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (member.phone.isNotBlank() && member.phone != "N/A") {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                .clickable { onCallClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call",
                                    tint = Color(0xFF334155),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "कॉल",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }

                    if (!isPaid && member.phone.isNotBlank()) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(10.dp))
                                .clickable { onWhatsAppReminder() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💬 ", fontSize = 12.sp)
                                Text(
                                    text = "व्हाट्सएप याद दिलाएं",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                    }
                }

                // Admin Edit & Delete buttons
                if (isAdmin) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Member",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Member",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddMemberSlotCard(
    isAdmin: Boolean,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onAddClick() }
            .testTag("btn_add_member_slot")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFEEF2FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "➕",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isAdmin) "+ नया सदस्य जोड़ें (असीमित संख्या)" else "+ नया सदस्य जोड़ें (एडमिन कोड आवश्यक)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "यहाँ क्लिक करके किसी भी नए सदस्य को ग्रुप बहीखाते में शामिल करें",
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
