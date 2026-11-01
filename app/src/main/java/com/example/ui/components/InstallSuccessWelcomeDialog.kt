package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SaffronGold

@Composable
fun InstallSuccessWelcomeDialog(
    fullName: String,
    phoneNumber: String,
    address: String,
    photoUri: String,
    isAdmin: Boolean,
    onEnterApp: () -> Unit
) {
    Dialog(
        onDismissRequest = onEnterApp,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFFFDE68A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Festive Header Icon with Golden Halo
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(Color(0xFFFEF3C7), CircleShape)
                        .border(2.5.dp, SaffronGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎉", fontSize = 34.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "हार्दिक बधाई एवं शुभकामनाएं!",
                    fontSize = 18.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "🚩 युवा शक्ति ग्रुप (भाटी परिवार कुचेरा)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepNavy,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Congratulatory Welcome Message
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "आपने युवा शक्ति ग्रुप ऐप को सफलतापूर्वक इंस्टॉल एवं सेटअप कर लिया है!\n\nमाँ भवानी व कुलदेवता के पावन आशीर्वाद से यह डिजिटल बहीखाता मंच हमारे भाटी परिवार की एकता, पारदर्शिता और मासिक अमावस्या अंशदान को निरंतर सशक्त रखेगा।",
                        fontSize = 12.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 17.5.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Avatar / Uploaded Photo
                        if (photoUri.isNotBlank()) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "प्रोफ़ाइल फोटो",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, SaffronGold, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFFE0E7FF), CircleShape)
                                    .border(2.dp, Color(0xFF818CF8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fullName.take(1).ifBlank { "स" },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fullName.ifBlank { "सम्मानित सदस्य" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = Color(0xFF0F172A)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = phoneNumber,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                            if (address.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = address,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        // Mode Chip
                        Surface(
                            color = if (isAdmin) Color(0xFFFEF3C7) else Color(0xFFE0F2FE),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isAdmin) Color(0xFFF59E0B) else Color(0xFF38BDF8))
                        ) {
                            Text(
                                text = if (isAdmin) "🛡️ एडमिन" else "👤 सदस्य",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAdmin) Color(0xFFB45309) else Color(0xFF0369A1),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Enter App Action Button
                Button(
                    onClick = onEnterApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚩 ऐप में प्रवेश करें (Enter App)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy
                    )
                }
            }
        }
    }
}
