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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GroupMessageEntity
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenLight
import com.example.ui.theme.PendingRed
import com.example.ui.theme.PendingRedLight
import com.example.ui.theme.SaffronGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GroupChatView(
    messages: List<GroupMessageEntity>,
    isAdmin: Boolean,
    onSendMessage: (senderName: String, message: String) -> Unit,
    onAddWhatsAppMessageClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSlate, RoundedCornerShape(18.dp))
            .testTag("group_chat_view")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Group Chat Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFDCFCE7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💬", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ग्रुप संवाद व संदेश मंच",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "व्हाट्सएप व ग्रुप के सभी संदेश सीधे यहाँ देखें",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Admin WhatsApp paste button
                    if (isAdmin) {
                        Surface(
                            color = Color(0xFF059669),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { onAddWhatsAppMessageClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📲 WA संदेश जोड़ें",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // WhatsApp Group open button
                    Surface(
                        color = Color(0xFF25D366),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable {
                            openWhatsAppGroup(context)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ग्रुप चैट ↗",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Messages Container
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    when {
                        msg.isUrgentAlert -> UrgentAlertBubble(message = msg)
                        msg.isWhatsAppMessage -> WhatsAppMessageBubble(message = msg)
                        msg.isSystemAlert -> SystemAlertBubble(message = msg)
                        else -> UserMessageBubble(message = msg, isAdmin = isAdmin)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Message Composer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("ग्रुप में संदेश लिखें...", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = DeepNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_group_message")
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            val name = if (isAdmin) "रेवंत राम जी (व्यवस्थापक)" else "भाटी परिवार सदस्य"
                            onSendMessage(name, inputMessage.trim())
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DeepNavy, CircleShape)
                        .testTag("btn_send_group_message")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UrgentAlertBubble(message: GroupMessageEntity) {
    Surface(
        color = Color(0xFFFFF1F2),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PendingRed),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.NotificationImportant,
                contentDescription = null,
                tint = PendingRed,
                modifier = Modifier.size(20.dp).padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = PendingRed,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "🚨 आवश्यक सूचना",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(message.timestamp)),
                        fontSize = 9.sp,
                        color = Color(0xFF991B1B)
                    )
                }

                Text(
                    text = message.message,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF881337),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun WhatsAppMessageBubble(message: GroupMessageEntity) {
    Surface(
        color = Color(0xFFF0FDF4),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(9.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.senderName,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                    Surface(
                        color = Color(0xFF25D366),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(start = 5.dp)
                    ) {
                        Text(
                            text = "📲 WhatsApp",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp)),
                    fontSize = 9.5.sp,
                    color = Color(0xFF059669)
                )
            }

            Text(
                text = message.message,
                fontSize = 11.5.sp,
                color = Color(0xFF14532D),
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SystemAlertBubble(message: GroupMessageEntity) {
    Surface(
        color = Color(0xFFFEF3C7),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(16.dp).padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.message,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF78350F),
                    lineHeight = 15.sp
                )
                Text(
                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(message.timestamp)),
                    fontSize = 9.sp,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun UserMessageBubble(
    message: GroupMessageEntity,
    isAdmin: Boolean
) {
    val isAdminMsg = message.senderRole.contains("व्यवस्थापक") || message.senderRole.contains("एडमिन")
    val bubbleBg = if (isAdminMsg) Color(0xFFEEF2FF) else Color.White
    val bubbleBorder = if (isAdminMsg) Color(0xFFC7D2FE) else Color(0xFFE2E8F0)

    Surface(
        color = bubbleBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, bubbleBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(9.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.senderName,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAdminMsg) DeepNavy else Color(0xFF0F172A)
                    )
                    if (isAdminMsg) {
                        Surface(
                            color = SaffronGold,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(start = 5.dp)
                        ) {
                            Text(
                                text = "व्यवस्थापक",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF451A03),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp)),
                    fontSize = 9.5.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Text(
                text = message.message,
                fontSize = 11.5.sp,
                color = Color(0xFF1E293B),
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

private fun openWhatsAppGroup(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://chat.whatsapp.com/invite")
        setPackage("com.whatsapp")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com"))
        try {
            context.startActivity(fallback)
        } catch (e2: Exception) {
            Toast.makeText(context, "व्हाट्सएप उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
        }
    }
}
