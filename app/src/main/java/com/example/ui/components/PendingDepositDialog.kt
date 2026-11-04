package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.AppDatabase
import com.example.data.MemberEntity
import com.example.data.PendingDepositDao
import com.example.data.PendingDepositEntity
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SaffronGold

/**
 * Dialog for entering pending November deposit entries for two specific members.
 * Admin can edit all fields: amount, paymentDate, notes, status.
 */
@Composable
fun PendingDepositDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val pendingDao: PendingDepositDao = db.pendingDepositDao()

    // Hard‑coded target members (as per user request)
    val targetMembers = listOf(
        MemberEntity(id = 0, name = "मनोहर भाटी", phone = "", monthlyAmount = 0, status = "Pending", paidDate = "", paymentMode = "", notes = "", sortOrder = 0, instagramId = "", facebookId = "", whatsappNumber = "", villageCity = "", memberCode = ""),
        MemberEntity(id = 0, name = "विक्की भाटी", phone = "", monthlyAmount = 0, status = "Pending", paidDate = "", paymentMode = "", notes = "", sortOrder = 0, instagramId = "", facebookId = "", whatsappNumber = "", villageCity = "", memberCode = "")
    )

    // State holders for each member's pending info
    data class PendingInfo(
        var amount: String = "",
        var paymentDate: String = "",
        var notes: String = "",
        var status: String = "Pending"
    )

    var membersInfo = remember { targetMembers.map { it.id to PendingInfo() }.toMutableMap() }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नोवेम्बर के लिये पेंडिंग जमा प्रविष्टि") },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                targetMembers.forEach { member ->
                    val info = membersInfo[member.id] ?: PendingInfo()
                    Text(text = "${member.name}", color = DeepNavy)
                    OutlinedTextField(
                        value = info.amount,
                        onValueChange = { info.amount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("राशि (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = info.paymentDate,
                        onValueChange = { info.paymentDate = it },
                        label = { Text("भुगतान तिथि (yyyy-MM-dd)") },
                        placeholder = { Text("उदा. 2026-11-30") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = info.notes,
                        onValueChange = { info.notes = it },
                        label = { Text("टिप्पणियाँ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = info.status,
                        onValueChange = { info.status = it },
                        label = { Text("स्थिति") },
                        placeholder = { Text("Pending / Paid / Rejected") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                errorMessage?.let { msg ->
                    Text(text = msg, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate and insert records
                    try {
                        val pendingList = targetMembers.map { member ->
                            val info = membersInfo[member.id] ?: PendingInfo()
                            PendingDepositEntity(
                                memberId = member.id,
                                memberName = member.name,
                                month = 11,
                                year = 2026,
                                amount = info.amount.toIntOrNull() ?: 0,
                                paymentDate = info.paymentDate,
                                notes = info.notes,
                                status = info.status
                            )
                        }
                        // Insert all pending records
                        pendingDao.insertAll(pendingList)
                        onDismiss()
                    } catch (e: Exception) {
                        errorMessage = "त्रुटि: ${e.localizedMessage}"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) SaffronGold else DeepNavy)
            ) {
                Text("सहेजें")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}

/**
 * Small helper composable that adds a button to open the PendingDepositDialog.
 * Place this in any admin screen where you want the feature.
 */
@Composable
fun PendingDepositButton() {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        PendingDepositDialog(onDismiss = { showDialog = false })
    }
    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
    ) {
        Text("नोवेम्बर पेंडिंग जमा जोड़ें")
    }
}
