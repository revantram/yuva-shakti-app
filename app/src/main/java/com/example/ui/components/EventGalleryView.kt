package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.EventGalleryEntity
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenDark
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventGalleryView(
    events: List<EventGalleryEntity>,
    isAdmin: Boolean,
    onAddEventClick: () -> Unit,
    onDeleteEvent: (EventGalleryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPhotoToView by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("event_gallery_view")
    ) {
        // Top Banner / Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📸 ", fontSize = 16.sp)
                            Text(
                                text = "कार्यक्रम व उत्सव फोटो गैलरी",
                                color = Color(0xFFFEF3C7),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "युवा शक्ति ग्रुप • पारिवारिक यादगार तस्वीरें व डाउनलोड लिंक",
                            color = Color(0xFFC7D2FE),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (isAdmin) {
                        Surface(
                            color = SaffronGold,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .clickable { onAddEventClick() }
                                .testTag("btn_admin_add_event")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF451A03),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "नया जोड़ें",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF451A03)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Event List
        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "अभी तक कोई कार्यक्रम फोटो नहीं जोड़ी गई है",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isAdmin) "ऊपर 'नया जोड़ें' बटन से कार्यक्रम की फोटो व Google Drive डाउनलोड लिंक जोड़ें।" else "एडमिन द्वारा पारिवारिक कार्यक्रमों की फोटो व डाउनलोड लिंक जोड़ने पर यहाँ दिखाई देंगी।",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onAddEventClick,
                            colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("कार्यक्रम फोटो व लिंक जोड़ें", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventCardItem(
                        event = event,
                        isAdmin = isAdmin,
                        onPhotoClick = { uri -> selectedPhotoToView = uri },
                        onOpenDownloadLink = { link -> openExternalLink(context, link) },
                        onDeleteClick = { onDeleteEvent(event) }
                    )
                }
            }
        }
    }

    selectedPhotoToView?.let { uri ->
        EventPhotoViewerDialog(
            photoUri = uri,
            onDismiss = { selectedPhotoToView = null }
        )
    }
}

@Composable
fun EventCardItem(
    event: EventGalleryEntity,
    isAdmin: Boolean,
    onPhotoClick: (String) -> Unit,
    onOpenDownloadLink: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val photoList = remember(event.photosJson) {
        if (event.photosJson.isBlank()) emptyList()
        else event.photosJson.split("|").filter { it.isNotBlank() }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title & Date Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.eventDate,
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (isAdmin) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Event",
                            tint = PendingRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // Photo Preview Horizontal Row
            if (photoList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(photoList) { uri ->
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .clickable { onPhotoClick(uri) }
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Event Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Direct HD Download Link Button (Drive / Photos)
            if (event.driveDownloadLink.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDownloadLink(event.driveDownloadLink) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF4338CA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "सभी HD फ़ोटो डाउनलोड करें ⬇️",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                                Text(
                                    text = "Google Drive / Photos लिंक से मूल फ़ोटो प्राप्त करें",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF475569),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open Link",
                            tint = DeepNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, eventDate: String, description: String, driveDownloadLink: String, photosJson: String) -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var title by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf(today) }
    var description by remember { mutableStateOf("") }
    var driveLink by remember { mutableStateOf("") }
    val photos = remember { mutableStateListOf<String>() }
    var errorText by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            photos.add(uri.toString())
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
                Text(
                    text = "नया कार्यक्रम व फ़ोटो/Drive लिंक जोड़ें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
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
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; errorText = null },
                        label = { Text("कार्यक्रम का नाम / शीर्षक *") },
                        placeholder = { Text("उदा. दीपावली स्नेह मिलन 2026") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = { eventDate = it },
                        label = { Text("कार्यक्रम की तारीख (YYYY-MM-DD)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = driveLink,
                        onValueChange = { driveLink = it },
                        label = { Text("Google Drive / Photos डाउनलोड लिंक") },
                        placeholder = { Text("https://drive.google.com/drive/folders/...") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "💡 सभी सदस्य इस लिंक पर क्लिक करके सीधे अपने फोन में सभी फोटो डाउनलोड कर सकेंगे।",
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("संक्षिप्त विवरण (वैकल्पिक)") },
                        placeholder = { Text("उदा. भाटी परिवार के समस्त सदस्यों की उपस्थिति रही...") },
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Photo upload preview section
                item {
                    Text(
                        text = "कार्यक्रम की झलकियाँ / फोटो जोड़ें (${photos.size} फोटो)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
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
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "फ़ोन से फोटो चुनें ➕",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        }
                    }

                    if (photos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(photos) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { photos.remove(uri) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(20.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (errorText != null) {
                    item {
                        Text(
                            text = errorText ?: "",
                            color = PendingRed,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorText = "कृपया कार्यक्रम का नाम दर्ज करें"
                        return@Button
                    }
                    val photosJson = photos.joinToString("|")
                    onSave(title.trim(), eventDate.trim(), description.trim(), driveLink.trim(), photosJson)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("सहेजें व प्रकाशित करें", fontWeight = FontWeight.Bold)
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
fun UrgentBroadcastDialog(
    onDismiss: () -> Unit,
    onBroadcast: (title: String, message: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🚨 ", fontSize = 18.sp)
                Text(
                    text = "आवश्यक / आपातकालीन सूचना प्रसारण",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFB91C1C)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "यह संदेश सभी सदस्यों को तुरंत उनके फोन पर अलर्ट/नोटिफिकेशन और ग्रुप संवाद में शीर्ष पर भेजा जाएगा।",
                    fontSize = 11.5.sp,
                    color = Color(0xFF475569)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorText = null },
                    label = { Text("सूचना का विषय *") },
                    placeholder = { Text("उदा. कल शाम आवश्यक पारिवारिक बैठक") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it; errorText = null },
                    label = { Text("विस्तृत सूचना / आदेश *") },
                    placeholder = { Text("उदा. सभी सदस्यों का उपस्थित होना अनिवार्य है...") },
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = PendingRed,
                        fontSize = 11.5.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || message.isBlank()) {
                        errorText = "कृपया विषय एवं संदेश दोनों दर्ज करें"
                        return@Button
                    }
                    onBroadcast(title.trim(), message.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = PendingRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("सभी को अलर्ट भेजें 🚨", fontWeight = FontWeight.Bold)
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
fun AddWhatsAppMessageDialog(
    onDismiss: () -> Unit,
    onSave: (sender: String, message: String) -> Unit
) {
    var sender by remember { mutableStateOf("व्हाट्सएप ग्रुप") }
    var message by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📲 ", fontSize = 16.sp)
                Text(
                    text = "व्हाट्सएप संदेश साझा करें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF065F46)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "व्हाट्सएप ग्रुप से कोई भी महत्वपूर्ण संदेश यहाँ जोड़ें ताकि सदस्य ऐप में ही पढ़ सकें।",
                    fontSize = 11.5.sp,
                    color = Color(0xFF475569)
                )

                OutlinedTextField(
                    value = sender,
                    onValueChange = { sender = it },
                    label = { Text("भेजने वाले का नाम (व्हाट्सएप)") },
                    placeholder = { Text("उदा. रेवंत राम जी भाटी") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it; errorText = null },
                    label = { Text("व्हाट्सएप संदेश यहाँ पेस्ट करें *") },
                    placeholder = { Text("व्हाट्सएप संदेश यहाँ लिखें या पेस्ट करें...") },
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = PendingRed,
                        fontSize = 11.5.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (message.isBlank()) {
                        errorText = "कृपया संदेश दर्ज करें"
                        return@Button
                    }
                    onSave(sender.trim(), message.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("ग्रुप संवाद में जोड़ें ✓", fontWeight = FontWeight.Bold)
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
fun EventPhotoViewerDialog(
    photoUri: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.94f))
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
                        text = "कार्यक्रम फोटो",
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
                    model = photoUri,
                    contentDescription = "Event Full Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp)
                        .clip(RoundedCornerShape(12.dp))
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

private fun openExternalLink(context: Context, url: String) {
    try {
        val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "लिंक खोलने में समस्या: $url", Toast.LENGTH_SHORT).show()
    }
}
