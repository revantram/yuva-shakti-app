package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ExpenseEntity
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenDark
import com.example.ui.theme.PendingRed
import com.example.ui.theme.PendingRedDark
import com.example.ui.theme.SaffronGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AccountingExpenseView(
    totalIncome: Int,
    expenses: List<ExpenseEntity>,
    isAdmin: Boolean,
    onAddExpense: () -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalExpense = expenses.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense
    var viewSlipExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Balance Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSlate, RoundedCornerShape(18.dp))
                .testTag("accounting_summary_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = DeepNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "समुदाय फंड बहीखाता (Accounting)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    if (isAdmin) {
                        Surface(
                            color = Color(0xFF4338CA),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { onAddExpense() }
                                .testTag("btn_open_add_expense")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "खर्च जोड़ें",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Financial Totals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FinancialBox(
                        label = "कुल जमा फंड",
                        amount = "₹$totalIncome",
                        amountColor = PaidGreenDark,
                        bgColor = Color(0xFFF0FDF4),
                        borderColor = Color(0xFFBBF7D0),
                        modifier = Modifier.weight(1f)
                    )

                    FinancialBox(
                        label = "कुल खर्च",
                        amount = "₹$totalExpense",
                        amountColor = PendingRedDark,
                        bgColor = Color(0xFFFFF1F2),
                        borderColor = Color(0xFFFECDD3),
                        modifier = Modifier.weight(1f)
                    )

                    FinancialBox(
                        label = "शुद्ध शेष राशि",
                        amount = "₹$netBalance",
                        amountColor = DeepNavy,
                        bgColor = Color(0xFFEEF2FF),
                        borderColor = Color(0xFFC7D2FE),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Header for expense item list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "खर्च मद का नाम (Item)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
            Text(
                text = "खर्च राशि (₹)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
        }

        // Expense Items List
        if (expenses.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📝", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "अभी तक कोई खर्च दर्ज नहीं है",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "एडमिन द्वारा जोड़ा गया खर्च और बिल की रसीद यहाँ दिखाई देगी।",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                expenses.forEach { exp ->
                    ExpenseRowItem(
                        expense = exp,
                        isAdmin = isAdmin,
                        onViewSlip = { viewSlipExpense = exp },
                        onDelete = { onDeleteExpense(exp) }
                    )
                }
            }
        }
    }

    // Slip Photo Viewer Dialog
    viewSlipExpense?.let { exp ->
        SlipViewerDialog(
            expense = exp,
            onDismiss = { viewSlipExpense = null }
        )
    }
}

@Composable
private fun FinancialBox(
    label: String,
    amount: String,
    amountColor: Color,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )
            Text(
                text = amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = amountColor,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun ExpenseRowItem(
    expense: ExpenseEntity,
    isAdmin: Boolean,
    onViewSlip: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
            .testTag("expense_item_${expense.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Item Name & Info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFF1F2), CircleShape)
                        .border(1.dp, Color(0xFFFECDD3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛒", fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = expense.itemName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = expense.category,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }

                        Text(
                            text = " • ${expense.date}",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (expense.receiptSlipUri.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onViewSlip() }
                                .padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Slip attached",
                                tint = DeepNavy,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "बिल / रसीद देखें 📷",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        }
                    }
                }
            }

            // Right side: Amount & Delete (if admin)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "- ₹${expense.amount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = PendingRedDark
                )

                if (isAdmin) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete expense",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (itemName: String, amount: Int, category: String, date: String, slipUri: String) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("प्रसाद/भोजन") }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var dateStr by remember { mutableStateOf(today) }
    var slipUri by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            slipUri = uri.toString()
        }
    }

    val categories = listOf("प्रसाद/भोजन", "व्यवस्था", "सामग्री", "धार्मिक", "अन्य")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "नया खर्च व बिल दर्ज करें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it; errorMsg = null },
                    label = { Text("खर्च मद / वस्तु का नाम *") },
                    placeholder = { Text("उदा. अमावस्या भोग व प्रसाद") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { ch -> ch.isDigit() }; errorMsg = null },
                    label = { Text("खर्च राशि (₹) *") },
                    placeholder = { Text("उदा. 1200") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Chips
                Column {
                    Text(
                        text = "खर्च श्रेणी:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            CategoryPill(
                                label = cat,
                                isSelected = category == cat,
                                onClick = { category = cat },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.drop(3).forEach { cat ->
                            CategoryPill(
                                label = cat,
                                isSelected = category == cat,
                                onClick = { category = cat },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("तारीख (YYYY-MM-DD)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Photo Slip attachment button
                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach Slip",
                            tint = DeepNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (slipUri.isNotBlank()) "स्लिप चुनी गई ✓ (बदलें)" else "बिल या रसीद की फोटो अपलोड करें 📷",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    }
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = PendingRed,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (itemName.isBlank()) {
                        errorMsg = "वस्तु का नाम दर्ज करें"
                        return@Button
                    }
                    val amt = amountStr.toIntOrNull()
                    if (amt == null || amt <= 0) {
                        errorMsg = "मान्य खर्च राशि दर्ज करें"
                        return@Button
                    }
                    onSave(itemName, amt, category, dateStr, slipUri)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("खर्च सहेजें", fontWeight = FontWeight.Bold)
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
private fun CategoryPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) DeepNavy else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF475569),
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SlipViewerDialog(
    expense: ExpenseEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "बिल / रसीद: ${expense.itemName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
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
                Text(
                    text = "राशि: ₹${expense.amount} • तारीख: ${expense.date}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (expense.receiptSlipUri.isNotBlank()) {
                    AsyncImage(
                        model = expense.receiptSlipUri,
                        contentDescription = "Bill slip",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, BorderSlate, RoundedCornerShape(10.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "इस खर्च के साथ कोई फोटो संलग्न नहीं है।",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
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
