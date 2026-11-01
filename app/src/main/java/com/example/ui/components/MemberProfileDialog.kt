package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberEntity
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenDark
import com.example.ui.theme.PaidGreenLight
import com.example.ui.theme.PendingRed
import com.example.ui.theme.PendingRedDark
import com.example.ui.theme.PendingRedLight
import com.example.ui.theme.SaffronGold

@Composable
fun MemberProfileDialog(
    member: MemberEntity,
    isAdmin: Boolean,
    profilePhotoUri: String = "",
    onDismiss: () -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val isPaid = member.status == "Paid"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "सदस्य प्रोफ़ाइल",
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Avatar
                if (profilePhotoUri.isNotBlank()) {
                    coil.compose.AsyncImage(
                        model = profilePhotoUri,
                        contentDescription = "प्रोफ़ाइल फोटो",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .androidx.compose.ui.draw.clip(CircleShape)
                            .border(2.dp, if (isPaid) PaidGreen else SaffronGold, CircleShape)
                    )
                } else {
                    val avatarLetter = member.name.trim().take(1)
                    val avatarBg = if (isPaid) PaidGreenLight else Color(0xFFEEF2FF)
                    val avatarTextColor = if (isPaid) PaidGreenDark else DeepNavy

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(avatarBg, CircleShape)
                            .border(2.dp, if (isPaid) PaidGreen else Color(0xFF818CF8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarLetter,
                            color = avatarTextColor,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = member.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )

                if (member.notes.isNotBlank()) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = member.notes,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                if (member.villageCity.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = member.villageCity,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Status Card
                Surface(
                    color = if (isPaid) PaidGreenLight else PendingRedLight,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPaid) Color(0xFFA7F3D0) else Color(0xFFFECDD3)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "मासिक अंशदान स्थिति:",
                                fontSize = 11.sp,
                                color = if (isPaid) PaidGreenDark else PendingRedDark
                            )
                            Text(
                                text = if (isPaid) "ऑनलाइन सत्यापित व जमा ✅" else "अंशदान बाकी ⏳",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPaid) PaidGreenDark else PendingRedDark
                            )
                        }

                        Text(
                            text = "₹${member.monthlyAmount}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPaid) PaidGreenDark else PendingRedDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Social Media Accounts Section
                Text(
                    text = "सामाजिक संपर्क व सोशल मीडिया आईडी",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Social Pills
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp
                    val waNumber = member.whatsappNumber.ifBlank { member.phone }
                    SocialActionRow(
                        iconEmoji = "💬",
                        platformName = "WhatsApp",
                        idValue = if (waNumber.isNotBlank()) waNumber else "उपलब्ध नहीं",
                        buttonText = "चैट करें",
                        actionColor = Color(0xFF25D366),
                        onAction = {
                            if (waNumber.isNotBlank()) {
                                openWhatsAppChat(context, waNumber)
                            }
                        }
                    )

                    // Instagram
                    SocialActionRow(
                        iconEmoji = "📸",
                        platformName = "Instagram",
                        idValue = if (member.instagramId.isNotBlank()) "@${member.instagramId}" else "आईडी जोड़ी नहीं गई",
                        buttonText = if (member.instagramId.isNotBlank()) "प्रोफ़ाइल" else "",
                        actionColor = Color(0xFFE1306C),
                        onAction = {
                            if (member.instagramId.isNotBlank()) {
                                openUrl(context, "https://instagram.com/${member.instagramId.trim('@')}")
                            }
                        }
                    )

                    // Facebook
                    SocialActionRow(
                        iconEmoji = "🌐",
                        platformName = "Facebook",
                        idValue = if (member.facebookId.isNotBlank()) member.facebookId else "आईडी जोड़ी नहीं गई",
                        buttonText = if (member.facebookId.isNotBlank()) "फेसबुक देखें" else "",
                        actionColor = Color(0xFF1877F2),
                        onAction = {
                            if (member.facebookId.isNotBlank()) {
                                val fbUrl = if (member.facebookId.startsWith("http")) member.facebookId else "https://facebook.com/${member.facebookId}"
                                openUrl(context, fbUrl)
                            }
                        }
                    )

                    // Phone Dial
                    if (member.phone.isNotBlank() && member.phone != "N/A") {
                        SocialActionRow(
                            iconEmoji = "📞",
                            platformName = "मोबाइल नंबर",
                            idValue = if (isAdmin) member.phone else maskPhone(member.phone),
                            buttonText = "कॉल करें",
                            actionColor = Color(0xFF334155),
                            onAction = {
                                openDialer(context, member.phone)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isAdmin) {
                Button(
                    onClick = {
                        onDismiss()
                        onEditClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("प्रोफ़ाइल संपादित करें", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ठीक है", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (isAdmin) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("बंद करें")
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun SocialActionRow(
    iconEmoji: String,
    platformName: String,
    idValue: String,
    buttonText: String,
    actionColor: Color,
    onAction: () -> Unit
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = iconEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = platformName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = idValue,
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (buttonText.isNotBlank()) {
                Surface(
                    color = actionColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .clickable { onAction() }
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = actionColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun maskPhone(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return if (digits.length >= 10) {
        "${digits.take(4)} ••••• ${digits.takeLast(2)}"
    } else {
        phone
    }
}

private fun openWhatsAppChat(context: Context, phone: String) {
    val clean = phone.replace(Regex("[^0-9]"), "")
    val formatted = if (clean.length == 10) "91$clean" else clean
    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formatted")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: Exception) {
        Toast.makeText(context, "व्हाट्सएप उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        Toast.makeText(context, "लिंक खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
    }
}

private fun openDialer(context: Context, phone: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
    } catch (e: Exception) {
        Toast.makeText(context, "कॉल शुरू नहीं हो सकी", Toast.LENGTH_SHORT).show()
    }
}
