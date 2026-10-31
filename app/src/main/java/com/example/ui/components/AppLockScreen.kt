package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SaffronGold

@Composable
fun AppLockScreen(
    isBiometricAvailable: Boolean,
    onPinEntered: (String) -> Boolean,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleDigit(d: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + d
            enteredPin = newPin
            errorMessage = null
            if (newPin.length == 4) {
                val success = onPinEntered(newPin)
                if (!success) {
                    errorMessage = "गलत पिन! कृपया सही 4-अंकीय पिन दर्ज करें।"
                    enteredPin = ""
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("app_lock_screen"),
        color = DeepNavy
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
                            ),
                            CircleShape
                        )
                        .border(2.dp, SaffronGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = SaffronGold,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "युवा शक्ति ग्रुप",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "सुरक्षित पारिवारिक बहीखाता",
                    color = Color(0xFFC7D2FE),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ऐप सुरक्षित है, अनलॉक करने के लिए पिन दर्ज करें",
                    color = Color(0xFFE2E8F0),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // PIN dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) SaffronGold else Color(0xFF334155)
                                )
                                .border(
                                    1.dp,
                                    if (isFilled) Color.White else Color(0xFF64748B),
                                    CircleShape
                                )
                        )
                    }
                }

                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            // Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Keypad Rows
                val keyRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9")
                )

                for (row in keyRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (digit in row) {
                            KeypadButton(
                                text = digit,
                                onClick = { handleDigit(digit) }
                            )
                        }
                    }
                }

                // Bottom row: Biometric / 0 / Backspace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Biometric button on bottom-left
                    if (isBiometricAvailable) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(1.dp, SaffronGold.copy(alpha = 0.5f), CircleShape)
                                .clickable { onBiometricClick() }
                                .testTag("btn_biometric_unlock"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Unlock",
                                tint = SaffronGold,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(70.dp))
                    }

                    KeypadButton(
                        text = "0",
                        onClick = { handleDigit("0") }
                    )

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .clickable { handleBackspace() }
                            .testTag("btn_keypad_backspace"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // Biometric Option Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                if (isBiometricAvailable) {
                    OutlinedButton(
                        onClick = { onBiometricClick() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .testTag("btn_biometric_bottom")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "फिंगरप्रिंट / फेस लॉक से अनलॉक करें",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "डिफ़ॉल्ट सुरक्षा कोड: 8696",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), CircleShape)
            .clickable { onClick() }
            .testTag("btn_keypad_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
