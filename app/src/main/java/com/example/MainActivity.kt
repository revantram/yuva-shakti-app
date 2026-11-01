package com.example

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DepositNotificationEntity
import com.example.data.ExpenseEntity
import com.example.data.GroupMessageEntity
import com.example.data.MemberEntity
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.components.AccountingExpenseView
import com.example.ui.components.AddEditMemberDialog
import com.example.ui.components.AddEventDialog
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.AddMemberSlotCard
import com.example.ui.components.AddWhatsAppMessageDialog
import com.example.ui.components.AdminControlBar
import com.example.ui.components.AdminPinDialog
import com.example.ui.components.AmavasyaBannerCard
import com.example.ui.components.AmavasyaDatePickerDialog
import com.example.ui.components.AmavasyaMiniCalendar
import com.example.ui.components.AppHeader
import com.example.ui.components.AppLockScreen
import com.example.ui.components.AppUpdateAndCodeConsoleDialog
import com.example.ui.components.ChangePinDialog
import com.example.ui.components.CreateLoanRequestDialog
import com.example.ui.components.CycleHistoryDialog
import com.example.ui.components.DailyThoughtCard
import com.example.ui.components.EditCycleNameDialog
import com.example.ui.components.EditLoanRequestDialog
import com.example.ui.components.EventGalleryView
import com.example.ui.components.EventPhotoViewerDialog
import com.example.ui.components.FundProgressCard
import com.example.ui.components.GroupChatView
import com.example.ui.components.LoanAssistanceView
import com.example.ui.components.MemberCard
import com.example.ui.components.MemberProfileDialog
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.OnboardingLoginDialog
import com.example.ui.components.OnlineMembersDialog
import com.example.ui.components.OnlinePaymentDialog
import com.example.ui.components.PaymentModeSelectionDialog
import com.example.ui.components.PaymentVerificationDialog
import com.example.ui.components.PendingMemberRegistrationsDialog
import com.example.ui.components.PendingPaymentRequestsDialog
import com.example.ui.components.ResetCycleConfirmDialog
import com.example.ui.components.ScreenshotViewerDialog
import com.example.ui.components.SetCycleTargetDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SlipViewerDialog
import com.example.ui.components.UrgentBroadcastDialog
import com.example.ui.components.WebDashboardView
import com.example.ui.components.WhatsAppShareCard
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PendingRed
import com.example.ui.theme.SaffronGold
import com.example.util.ApkShareHelper
import com.example.util.DailyThoughtHelper
import com.example.util.FestivalHelper
import com.example.util.FirestoreSyncHelper
import com.example.util.NotificationHelper
import com.example.util.OnlinePresenceHelper
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "enableEdgeToEdge error", t)
        }

        try {
            setContent {
                val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
                val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
                val systemInDark = isSystemInDarkTheme()

                // Auto-schedule: Morning (6:00 to 18:59) is Light, Night (19:00 to 5:59) is Dark
                val isDark = when (themeMode) {
                    "dark" -> true
                    "light" -> false
                    "auto_time" -> currentHour < 6 || currentHour >= 19
                    else -> false // Default is Light iOS theme as requested
                }

                MyApplicationTheme(darkTheme = isDark) {
                    MainScreen(viewModel = viewModel)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "setContent fatal error", t)
            try {
                val sw = java.io.StringWriter()
                t.printStackTrace(java.io.PrintWriter(sw))
                java.io.File("/sdcard/Download/yuva_shakti_crash.txt").writeText("onCreate error:\n$sw")
            } catch (_: Throwable) {}
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUserMemberCode by viewModel.currentUserMemberCode.collectAsStateWithLifecycle()
    val targetAmavasyaMillis by viewModel.targetAmavasyaMillis.collectAsStateWithLifecycle()
    val selectedMainSection by viewModel.selectedMainSection.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val groupMessages by viewModel.groupMessages.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val allLoanRequests by viewModel.allLoanRequests.collectAsStateWithLifecycle()
    val pendingPaymentRequests by viewModel.pendingPaymentRequests.collectAsStateWithLifecycle()
    val customQrCodeUri by viewModel.customQrCodeUri.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val activeMemberId by viewModel.activeMemberId.collectAsStateWithLifecycle()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    // Dialog state flows
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    val showAddEditDialog by viewModel.showAddEditDialog.collectAsStateWithLifecycle()
    val memberToEdit by viewModel.memberToEdit.collectAsStateWithLifecycle()
    val showResetConfirmDialog by viewModel.showResetConfirmDialog.collectAsStateWithLifecycle()
    val showHistoryDialog by viewModel.showHistoryDialog.collectAsStateWithLifecycle()
    val showChangePinDialog by viewModel.showChangePinDialog.collectAsStateWithLifecycle()
    val showDatePickerDialog by viewModel.showDatePickerDialog.collectAsStateWithLifecycle()
    val showNotificationsDialog by viewModel.showNotificationsDialog.collectAsStateWithLifecycle()
    val showPaymentDialog by viewModel.showPaymentDialog.collectAsStateWithLifecycle()
    val showSetCycleTargetDialog by viewModel.showSetCycleTargetDialog.collectAsStateWithLifecycle()
    val showPaymentVerificationDialog by viewModel.showPaymentVerificationDialog.collectAsStateWithLifecycle()
    val showPendingRequestsDialog by viewModel.showPendingRequestsDialog.collectAsStateWithLifecycle()
    val pendingRegistrations by viewModel.pendingRegistrations.collectAsStateWithLifecycle()
    val showPendingRegistrationsDialog by viewModel.showPendingRegistrationsDialog.collectAsStateWithLifecycle()
    val pendingRegistrationRequestId by viewModel.pendingRegistrationRequestId.collectAsStateWithLifecycle()
    val pendingRegistrationName by viewModel.pendingRegistrationName.collectAsStateWithLifecycle()
    val pendingRegistrationCode by viewModel.pendingRegistrationCode.collectAsStateWithLifecycle()
    val pendingRegistrationStatus by viewModel.pendingRegistrationStatus.collectAsStateWithLifecycle()
    val screenshotToView by viewModel.screenshotToView.collectAsStateWithLifecycle()
    val selectedMemberForProfile by viewModel.selectedMemberForProfile.collectAsStateWithLifecycle()
    val showAddExpenseDialog by viewModel.showAddExpenseDialog.collectAsStateWithLifecycle()
    val expenseReceiptToView by viewModel.expenseReceiptToView.collectAsStateWithLifecycle()
    val showAddEventDialog by viewModel.showAddEventDialog.collectAsStateWithLifecycle()
    val showUrgentBroadcastDialog by viewModel.showUrgentBroadcastDialog.collectAsStateWithLifecycle()
    val showAddWhatsAppDialog by viewModel.showAddWhatsAppDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showCreateLoanDialog by viewModel.showCreateLoanDialog.collectAsStateWithLifecycle()
    val loanToEditByAdmin by viewModel.loanToEditByAdmin.collectAsStateWithLifecycle()
    val showOnlineMembersDialog by viewModel.showOnlineMembersDialog.collectAsStateWithLifecycle()
    val showAppUpdateConsoleDialog by viewModel.showAppUpdateConsoleDialog.collectAsStateWithLifecycle()
    val updateApkUrl by viewModel.updateApkUrl.collectAsStateWithLifecycle()
    val latestVersionNote by viewModel.latestVersionNote.collectAsStateWithLifecycle()
    val fundUpiId by viewModel.fundUpiId.collectAsStateWithLifecycle()
    val whatsAppGroupLink by viewModel.whatsAppGroupLink.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val showWelcomeGreetingDialog by viewModel.showWelcomeGreetingDialog.collectAsStateWithLifecycle()
    val currentUserFullName by viewModel.currentUserFullName.collectAsStateWithLifecycle()
    val currentUserPhone by viewModel.currentUserPhone.collectAsStateWithLifecycle()
    val currentUserAddress by viewModel.currentUserAddress.collectAsStateWithLifecycle()
    val currentUserProfilePhotoUri by viewModel.currentUserProfilePhotoUri.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var memberForPaymentDetails by remember { mutableStateOf<MemberEntity?>(null) }
    var showEditCycleNameDialog by remember { mutableStateOf(false) }
    var eventPhotoToView by remember { mutableStateOf<Pair<String, String>?>(null) }

    val keyguardManager = remember {
        context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
    }

    val biometricLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.unlockAppBiometrically()
        }
    }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val deviceId = remember {
        com.example.util.InstallationHelper.getDeviceUuid(context)
    }
    val deviceModel = remember {
        com.example.util.InstallationHelper.getDeviceInfo()
    }

    var firestoreOnlineCount by remember { mutableIntStateOf(1) }
    var firestoreActiveUsers by remember { mutableStateOf<List<String>>(emptyList()) }
    var activeIncomingAlert by remember { mutableStateOf<FirestoreSyncHelper.EmergencyAlertData?>(null) }

    // Auto-refresh from Firebase and update online presence on app resume / background
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, activeMemberId, currentUserFullName, isUserLoggedIn) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshAllDataFromFirebase(isSilent = true)
                val myMember = uiState.members.find { it.id == activeMemberId }
                val myName = if (isUserLoggedIn) (myMember?.name ?: currentUserFullName.ifBlank { "परिवार सदस्य" }) else "नया सदस्य (ऐप सक्रिय)"
                val codeToSend = if (activeMemberId == 1L || currentUserPhone == "8696663615") "992942" else currentUserMemberCode.ifBlank { myMember?.memberCode ?: "" }
                FirestoreSyncHelper.recordPresence(
                    deviceId = deviceId,
                    userName = myName,
                    memberId = if (isUserLoggedIn) activeMemberId else 0L,
                    memberCode = codeToSend,
                    isOnline = true,
                    deviceModel = deviceModel
                )
            } else if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP || event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE) {
                val myMember = uiState.members.find { it.id == activeMemberId }
                val myName = if (isUserLoggedIn) (myMember?.name ?: currentUserFullName.ifBlank { "परिवार सदस्य" }) else "परिवार सदस्य"
                val codeToSend = if (activeMemberId == 1L || currentUserPhone == "8696663615") "992942" else currentUserMemberCode.ifBlank { myMember?.memberCode ?: "" }
                FirestoreSyncHelper.recordPresence(
                    deviceId = deviceId,
                    userName = myName,
                    memberId = if (isUserLoggedIn) activeMemberId else 0L,
                    memberCode = codeToSend,
                    isOnline = false,
                    deviceModel = deviceModel
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Realtime Firestore presence, settings, and emergency siren listeners
    DisposableEffect(Unit) {
        val presenceReg = FirestoreSyncHelper.listenToOnlinePresence { count, memberIds, users, userDetails ->
            firestoreOnlineCount = count
            firestoreActiveUsers = users
            OnlinePresenceHelper.updateOnlinePresence(count, memberIds, users, userDetails)
        }
        val settingsReg = FirestoreSyncHelper.listenToSettings { upiId, waLink, target, cycleName, amavasyaMillis, apkUrl, version, notes ->
            upiId?.let {
                val clean = it.trim()
                val isOldOrCorrupt = clean.isBlank() ||
                        clean == "revantrambhati@upi" ||
                        clean.contains("850mbhati") ||
                        clean.contains("77850983600") ||
                        clean.contains("777850983600") ||
                        clean.count { c -> c == '@' } != 1 ||
                        !clean.contains("@")
                val finalUpi = if (isOldOrCorrupt) {
                    "7850983600@ibl"
                } else {
                    clean
                }
                viewModel.setFundUpiId(finalUpi, showSnackbar = false)
            }
            waLink?.let {
                val clean = it.trim()
                val finalWa = if (clean == "https://chat.whatsapp.com/invite" || clean.isBlank()) {
                    "https://chat.whatsapp.com/JOVKSohSoBHEokrCXOSkTh?s=cl&p=a&iam=2"
                } else {
                    clean
                }
                viewModel.setWhatsAppGroupLink(finalWa)
            }
            target?.let { if (it > 0) viewModel.setCustomTargetAmount(it) }
            cycleName?.let { if (it.isNotBlank()) viewModel.setAmavasyaCycleName(it) }
            amavasyaMillis?.let { if (it > 0) viewModel.setTargetAmavasyaMillis(it) }
        }
        val alertReg = FirestoreSyncHelper.listenToEmergencyAlerts(context) { alertData ->
            if (alertData.isSiren) {
                com.example.util.SoundHelper.playSirenAlarm(context)
                com.example.util.NotificationHelper.triggerPendingSirenNotification(
                    context = context,
                    title = alertData.title,
                    message = alertData.message,
                    memberName = if (alertData.pendingCount > 0) "${alertData.pendingCount} बकाया सदस्यों" else "परिवार सदस्य",
                    amount = if (alertData.totalPendingAmount > 0) alertData.totalPendingAmount else 100
                )
            }
            activeIncomingAlert = alertData
        }
        onDispose {
            presenceReg?.remove()
            settingsReg?.remove()
            alertReg?.remove()
        }
    }

    LaunchedEffect(Unit) {
        try {
            val soundPrefs = context.getSharedPreferences("yuva_shakti_sound_prefs", Context.MODE_PRIVATE)
            val hasPlayedFirstLaunch = soundPrefs.getBoolean("has_played_welcome_sound", false)
            if (!hasPlayedFirstLaunch) {
                com.example.util.SoundHelper.playJaiShreeRam(context)
                soundPrefs.edit().putBoolean("has_played_welcome_sound", true).apply()
            }

            com.example.util.NotificationHelper.createNotificationChannel(context)
            OnlinePresenceHelper.recordHeartbeat(activeMemberId)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "Error in startup LaunchedEffect", t)
        }
    }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // 0. Live Persistent Heartbeat Loop (every 45 seconds as requested)
    LaunchedEffect(activeMemberId, currentUserMemberCode, isUserLoggedIn) {
        while (true) {
            val myMember = uiState.members.find { it.id == activeMemberId }
            val myName = if (isUserLoggedIn) (myMember?.name ?: currentUserFullName.ifBlank { "परिवार सदस्य" }) else "नया सदस्य (ऐप सक्रिय)"
            val codeToSend = if (activeMemberId == 1L || currentUserPhone == "8696663615") "992942" else currentUserMemberCode.ifBlank { myMember?.memberCode ?: "" }
            FirestoreSyncHelper.recordPresence(
                deviceId = deviceId,
                userName = myName,
                memberId = if (isUserLoggedIn) activeMemberId else 0L,
                memberCode = codeToSend,
                isOnline = true,
                deviceModel = deviceModel
            )
            delay(45000L) // Har 45 second mein update bhejna
        }
    }

    // 1. High-Priority Emergency Siren Alert Dialog (Appears immediately over any screen including Lock / Login)
    val currentAlert = activeIncomingAlert
    if (currentAlert != null) {
        val alertTitle = currentAlert.title
        val alertMessage = currentAlert.message
        val myMember = uiState.members.find { it.id == activeMemberId }
            ?: uiState.members.find { currentUserPhone.isNotBlank() && it.phone.takeLast(10) == currentUserPhone.takeLast(10) }
        val isMyDuePending = myMember != null && myMember.status == "Pending"
        val myDueAmount = myMember?.monthlyAmount ?: 100

        AlertDialog(
            onDismissRequest = {
                com.example.util.SoundHelper.stopSound()
                activeIncomingAlert = null
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color(0xFFFEE2E2), CircleShape)
                        .border(2.dp, PendingRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚨", fontSize = 28.sp)
                }
            },
            title = {
                Text(
                    text = alertTitle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = PendingRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = alertMessage,
                        fontSize = 13.5.sp,
                        color = Color(0xFF1E293B),
                        lineHeight = 19.sp
                    )

                    // 1. Personalized Warning Card if this member has pending dues!
                    if (isMyDuePending) {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "⚠️ प्रिय ${myMember?.name} जी! आपका ₹$myDueAmount अंशदान अभी बकाया है।",
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        com.example.util.SoundHelper.stopSound()
                                        activeIncomingAlert = null
                                        val upiUri = Uri.parse("upi://pay?pa=$fundUpiId&pn=${Uri.encode("Yuva Shakti Bhati Parivar")}&am=$myDueAmount&cu=INR&tn=${Uri.encode("Amavasya Fund - ${myMember?.name}")}")
                                        val intent = Intent(Intent.ACTION_VIEW, upiUri).apply {
                                            setPackage("com.phonepe.app")
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            val chooser = Intent(Intent.ACTION_VIEW, upiUri)
                                            context.startActivity(Intent.createChooser(chooser, "PhonePe या UPI से भुगतान करें"))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("📱 PhonePe से ₹$myDueAmount तुरंत जमा करें", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // 2. Pending dues summary card & WhatsApp broadcast button
                    if (currentAlert.pendingCount > 0) {
                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "👥 कुल बकाया सदस्य: ${currentAlert.pendingCount} | कुल बकाया: ₹${currentAlert.totalPendingAmount}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        val pendingMsg = buildString {
                                            append("🚨 *युवा शक्ति ग्रुप (भाटी परिवार कुचेरा)* 🚨\n")
                                            append("*अत्यावश्यक सायरन तगादा - बकाया अंशदान*\n\n")
                                            append("${currentAlert.message}\n\n")
                                            if (currentAlert.pendingMemberNames.isNotEmpty()) {
                                                append("⏳ *बकाया सदस्यों की सूची:*\n")
                                                currentAlert.pendingMemberNames.forEachIndexed { i, name ->
                                                    append("${i + 1}. $name\n")
                                                }
                                                append("\n💰 *कुल बकाया राशि:* ₹${currentAlert.totalPendingAmount}\n")
                                            }
                                            append("📲 *PhonePe / UPI आईडी:* $fundUpiId\n")
                                            append("समिति की पारदर्शिता एवं आगामी बैठक व्यवस्था हेतु तुरंत अपना अंशदान जमा कराएं। 🙏")
                                        }
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, pendingMsg)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "व्हाट्सएप पर तगादा भेजें"))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("💬 व्हाट्सएप पर बकाया तगादा भेजें", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                            }
                        }
                    }

                    Text(
                        text = "🔊 सायरन अलार्म बज रहा है। बंद करने हेतु नीचे बटन दबाएं।",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        com.example.util.SoundHelper.stopSound()
                        activeIncomingAlert = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PendingRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🚨 सायरन बंद करें (ठीक है)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // If App Lock is enabled and app is locked, render Lock Screen directly
    if (isAppLocked) {
        AppLockScreen(
            isBiometricAvailable = isBiometricEnabled && (keyguardManager?.isDeviceSecure == true),
            onPinEntered = { pin -> viewModel.unlockAppWithPin(pin) },
            onBiometricClick = {
                val intent = keyguardManager?.createConfirmDeviceCredentialIntent(
                    "युवा शक्ति ग्रुप",
                    "ऐप अनलॉक करने के लिए बायोमेट्रिक या पिन सत्यापित करें"
                )
                if (intent != null) {
                    biometricLauncher.launch(intent)
                } else {
                    Toast.makeText(context, "बायोमेट्रिक प्रमाणीकरण उपलब्ध नहीं है, कृपया पिन दर्ज करें", Toast.LENGTH_SHORT).show()
                }
            }
        )
        return
    }

    // If user is not logged in, show Onboarding / Member Code Login Screen
    if (!isUserLoggedIn) {
        OnboardingLoginDialog(
            onLoginWithCode = { code, onResult ->
                viewModel.loginWithCode(code, onResult)
            },
            onRegisterWithCode = { name, phone, address, code, photo, insta, onResult ->
                viewModel.registerNewMemberWithCode(name, phone, address, code, photo, insta, onResult)
            },
            onCheckCodeTaken = { code ->
                viewModel.isCodeTaken(code)
            },
            pendingRequestId = pendingRegistrationRequestId,
            pendingName = pendingRegistrationName,
            pendingCode = pendingRegistrationCode,
            pendingStatus = pendingRegistrationStatus,
            onCancelPendingRegistration = {
                viewModel.cancelPendingRegistration()
            }
        )
        return
    }

    // Morning Hindu Festival Notification & Greetings
    LaunchedEffect(targetAmavasyaMillis) {
        val todayFest = FestivalHelper.getTodayFestival(targetAmavasyaMillis)
        if (todayFest != null) {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val festPrefs = context.getSharedPreferences("yuva_shakti_fest_prefs", Context.MODE_PRIVATE)
            val lastGreeted = festPrefs.getString("last_fest_greeted_date", "")
            if (lastGreeted != todayStr) {
                festPrefs.edit().putString("last_fest_greeted_date", todayStr).apply()
                NotificationHelper.showFestivalGreetingNotification(
                    context = context,
                    festivalName = todayFest.name,
                    wishes = FestivalHelper.getFestivalGreeting(todayFest)
                )
            }
        }
    }

    val onlineCount = remember(uiState.members, activeMemberId, firestoreOnlineCount) {
        maxOf(firestoreOnlineCount, OnlinePresenceHelper.getOnlineCount(uiState.members, activeMemberId))
    }

    val isDark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> false // Apple iOS Light Mode by default
    }
    val appBackgroundBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF0F172A),
                    Color(0xFF1E293B),
                    Color(0xFF0F172A)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFF2F2F7),
                    Color(0xFFFFFFFF),
                    Color(0xFFF2F2F7)
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundBrush)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {},
            bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(Color.Transparent)
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Frosted Apple iOS Style Dock
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    color = if (isDark) Color(0xDC0F172A) else Color(0xF2FFFFFF),
                    border = BorderStroke(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0xFFE5E5EA))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val loanPendingCount = remember(allLoanRequests) {
                            allLoanRequests.count { it.status == "Pending" }
                        }

                        // 0. Live Web Cloud Ledger / लाइव वेब
                        FloatingNavItem(
                            title = "लाइव वेब",
                            icon = Icons.Default.Cloud,
                            isSelected = selectedMainSection == "web" && !showSettingsDialog,
                            onClick = { viewModel.setMainSection("web") }
                        )

                        // 1. Members / सदस्य
                        FloatingNavItem(
                            title = "सदस्य",
                            icon = Icons.Default.People,
                            isSelected = selectedMainSection == "members" && !showSettingsDialog,
                            onClick = { viewModel.setMainSection("members") }
                        )

                        // 2. Accounting / बहीखाता
                        FloatingNavItem(
                            title = "बहीखाता",
                            icon = Icons.Default.ReceiptLong,
                            isSelected = selectedMainSection == "accounting" && !showSettingsDialog,
                            onClick = { viewModel.setMainSection("accounting") }
                        )

                        // 3. Mutual Aid / ऋण सहयोग
                        FloatingNavItem(
                            title = "ऋण सहयोग",
                            icon = Icons.Default.Handshake,
                            isSelected = selectedMainSection == "loan" && !showSettingsDialog,
                            badgeCount = loanPendingCount,
                            badgeColor = Color(0xFFEF4444),
                            onClick = { viewModel.setMainSection("loan") }
                        )

                        // 4. Gallery / गैलरी
                        FloatingNavItem(
                            title = "गैलरी",
                            icon = Icons.Default.Collections,
                            isSelected = selectedMainSection == "gallery" && !showSettingsDialog,
                            onClick = { viewModel.setMainSection("gallery") }
                        )

                        // 5. Chat / चर्चा
                        FloatingNavItem(
                            title = "चर्चा",
                            icon = Icons.Default.Forum,
                            isSelected = selectedMainSection == "chat" && !showSettingsDialog,
                            badgeCount = groupMessages.size,
                            badgeColor = Color(0xFFF59E0B),
                            onClick = { viewModel.setMainSection("chat") }
                        )

                        // 6. Settings / सेटिंग्स
                        FloatingNavItem(
                            title = "सेटिंग्स",
                            icon = Icons.Default.Settings,
                            isSelected = showSettingsDialog,
                            onClick = { viewModel.openSettingsDialog(true) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (selectedMainSection) {
                "web" -> {
                    WebDashboardView(
                        modifier = Modifier.fillMaxSize(),
                        onNavigateBack = { viewModel.setMainSection("members") }
                    )
                }

                "loan" -> {
                    // Mutual Aid Fund Loan / Assistance Section with voting
                    LoanAssistanceView(
                        loanRequests = allLoanRequests,
                        members = uiState.members,
                        currentVoterId = activeMemberId,
                        isAdmin = uiState.isAdmin,
                        onRequestNewLoanClick = { viewModel.openCreateLoanDialog(true) },
                        onVoteClick = { req, isUp -> viewModel.voteLoanRequest(req, isUp) },
                        onAdminEditClick = { req -> viewModel.openEditLoanDialog(req) },
                        onAdminStatusChange = { req, status -> viewModel.setLoanRequestStatus(req, status) }
                    )
                }

                "accounting" -> {
                    // Accounting Ledger & Expense Area
                    AccountingExpenseView(
                        totalIncome = uiState.totalCollected,
                        expenses = expenses,
                        isAdmin = uiState.isAdmin,
                        onAddExpense = {
                            if (uiState.isAdmin) {
                                viewModel.openAddExpenseDialog(true)
                            } else {
                                viewModel.showAuthDialog(true)
                            }
                        },
                        onDeleteExpense = { viewModel.deleteExpense(it) }
                    )
                }

                "chat" -> {
                    // Community Notices & Group Chat
                    GroupChatView(
                        messages = groupMessages,
                        isAdmin = uiState.isAdmin,
                        whatsAppGroupLink = whatsAppGroupLink,
                        onUpdateWhatsAppGroupLink = { viewModel.setWhatsAppGroupLink(it) },
                        onSendMessage = { name, msg ->
                            viewModel.sendGroupMessage(name, msg)
                        },
                        onAddWhatsAppMessageClick = {
                            viewModel.openAddWhatsAppDialog(true)
                        }
                    )
                }

                "gallery" -> {
                    // Function & Event Photo Gallery
                    EventGalleryView(
                        events = events,
                        isAdmin = uiState.isAdmin,
                        onAddEventClick = { viewModel.openAddEventDialog(true) },
                        onDeleteEvent = { viewModel.deleteEvent(it) }
                    )
                }

                else -> {
                    // Default "members" view
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 0. App Header with Countdown & Admin Toggle (Scrolls naturally with content!)
                        item(key = "app_header") {
                            AppHeader(
                                isAdmin = uiState.isAdmin,
                                totalCollected = uiState.totalCollected,
                                countdownFlow = viewModel.countdownInfo,
                                unreadNotificationCount = unreadNotifCount,
                                onlineMemberCount = onlineCount,
                                onAuthClick = {
                                    if (uiState.isAdmin) {
                                        viewModel.logoutAdmin()
                                    } else {
                                        viewModel.showAuthDialog(true)
                                    }
                                },
                                onDateSettingsClick = {
                                    viewModel.showDatePickerDialog(true)
                                },
                                onNotificationsClick = {
                                    viewModel.openNotificationsDialog(true)
                                },
                                onPaymentClick = {
                                    viewModel.openPaymentDialog(true)
                                },
                                onSettingsClick = {
                                    viewModel.openSettingsDialog(true)
                                },
                                onUrgentBroadcastClick = {
                                    viewModel.openUrgentBroadcastDialog(true)
                                },
                                onOnlineMembersClick = {
                                    viewModel.openOnlineMembersDialog(true)
                                },
                                onSirenTgadaClick = {
                                    viewModel.triggerManualSirenTgada(context)
                                }
                            )
                        }

                        // 0A. Morning Hindu Festival Greeting Banner (if today has festival)
                        val todayFest = FestivalHelper.getTodayFestival(targetAmavasyaMillis)
                        if (todayFest != null) {
                            item {
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.2.dp, Color(0xFFFDE68A)),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = todayFest.icon, fontSize = 24.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = todayFest.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = Color(0xFF92400E)
                                                    )
                                                    Text(
                                                        text = todayFest.tithi,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFFB45309),
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                            ) {
                                                Text(
                                                    text = todayFest.category,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = FestivalHelper.getFestivalGreeting(todayFest),
                                            fontSize = 12.sp,
                                            color = Color(0xFF78350F),
                                            lineHeight = 17.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Surface(
                                            color = Color(0xFFD97706),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "text/plain"
                                                        putExtra(
                                                            Intent.EXTRA_TEXT,
                                                            "🚩 *युवा शक्ति ग्रुप, भाटी परिवार कुचेरा*\n\n" +
                                                            FestivalHelper.getFestivalGreeting(todayFest)
                                                        )
                                                    }
                                                    context.startActivity(Intent.createChooser(shareIntent, "शुभकामनाएं साझा करें"))
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = "🚩 ", fontSize = 13.sp)
                                                Text(
                                                    text = "परिवार में शुभकामनाएं साझा करें (Share Wishes)",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 0B. In-App Direct Update Card
                        item {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.2.dp, Color(0xFFBFDBFE)),
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .background(Color(0xFF2563EB), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Collections,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "🚀 नया ऐप वर्शन (Official APK)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF1E40AF)
                                            )
                                            Text(
                                                text = "रीयल-टाइम ऑनलाइन सिंक व नए फीचर्स",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFF3B82F6)
                                            )
                                        }
                                    }
                                    Surface(
                                        color = Color(0xFF2563EB),
                                        shape = RoundedCornerShape(10.dp),
                                        shadowElevation = 2.dp,
                                        modifier = Modifier.clickable {
                                            ApkShareHelper.openDirectDownload(context, updateApkUrl)
                                        }
                                    ) {
                                        Text(
                                            text = "सीधे अपडेट करें ↗",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 1. Daily Inspirational Thought (दैनिक सुविचार) - Rotates Daily
                        item {
                            DailyThoughtCard(
                                thought = DailyThoughtHelper.getTodayThought(),
                                onNotifyClick = {
                                    viewModel.sendDailyThoughtNotification()
                                }
                            )
                        }

                        // 2. Amavasya Mini Calendar
                        item {
                            AmavasyaMiniCalendar(targetAmavasyaMillis = targetAmavasyaMillis)
                        }

                        // 3. Amavasya Alert Card (Dynamic consensus cycle)
                        item {
                            AmavasyaBannerCard(
                                uiState = uiState,
                                onBannerStateCycle = { newState ->
                                    viewModel.setAmavasyaBannerState(newState)
                                },
                                onEditCycleName = {
                                    showEditCycleNameDialog = true
                                }
                            )
                        }

                        // 4. Fund Progress & Overview Stats Card
                        item {
                            FundProgressCard(
                                uiState = uiState,
                                onAddExpenseClick = { viewModel.setMainSection("accounting") },
                                onSetTargetClick = {
                                    if (uiState.isAdmin) {
                                        viewModel.openSetCycleTargetDialog(true)
                                    } else {
                                        viewModel.showAuthDialog(true)
                                    }
                                }
                            )
                        }

                        // 5. WhatsApp Broadcast Share Banner
                        item {
                            WhatsAppShareCard(
                                onShareClick = {
                                    val reportText = viewModel.generateWhatsAppReport()
                                    shareToWhatsApp(context, reportText)
                                }
                            )
                        }

                        // 6. Admin Only Actions Panel
                        if (uiState.isAdmin) {
                            item {
                                AdminControlBar(
                                    pendingPaymentRequestCount = pendingPaymentRequests.size,
                                    pendingRegistrationCount = pendingRegistrations.size,
                                    onAddMember = { viewModel.openAddMemberDialog() },
                                    onSetCycleTarget = { viewModel.openSetCycleTargetDialog(true) },
                                    onViewPaymentRequests = { viewModel.openPendingRequestsDialog(true) },
                                    onViewRegistrationRequests = { viewModel.openPendingRegistrationsDialog(true) },
                                    onUrgentBroadcast = { viewModel.openUrgentBroadcastDialog(true) },
                                    onResetCycle = { viewModel.showResetCycleDialog(true) },
                                    onViewHistory = { viewModel.showHistoryDialog(true) },
                                    onChangePin = { viewModel.showChangePinDialog(true) },
                                    onOpenAppUpdateConsole = { viewModel.openAppUpdateConsole(true) }
                                )
                            }
                        }

                        // 7. Search Bar
                        item {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        text = "सदस्य का नाम या मोबाइल नंबर खोजें...",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = if (isDark) Color(0xD91E293B) else Color(0xEEFFFFFF),
                                    unfocusedContainerColor = if (isDark) Color(0xB31E293B) else Color(0xD9FFFFFF),
                                    focusedTextColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                    unfocusedTextColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                    focusedBorderColor = if (isDark) SaffronGold else DeepNavy,
                                    unfocusedBorderColor = if (isDark) Color(0xFF334155) else BorderSlate
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_members_input")
                            )
                        }

                        // 8. Tab Filters
                        item {
                            TabFilterRow(
                                selectedTab = uiState.selectedTab,
                                totalCount = uiState.totalMembers,
                                paidCount = uiState.paidMembers,
                                pendingCount = uiState.pendingMembers,
                                onTabSelected = { viewModel.setTab(it) }
                            )
                        }

                        // 9. Member Cards List
                        if (uiState.filteredMembers.isEmpty()) {
                            item {
                                EmptyMembersPlaceholder(searchQuery = uiState.searchQuery)
                            }
                        } else {
                            items(
                                items = uiState.filteredMembers,
                                key = { it.id },
                                contentType = { "member_card" }
                            ) { member ->
                                val isOnline = OnlinePresenceHelper.isOnline(member.id, activeMemberId, uiState.members.size)
                                MemberCard(
                                    member = member,
                                    isAdmin = uiState.isAdmin,
                                    isOnline = isOnline,
                                    isDark = isDark,
                                    onCardClick = {
                                        viewModel.openMemberProfile(member)
                                    },
                                    onToggleStatus = {
                                        if (member.status == "Pending") {
                                            memberForPaymentDetails = member
                                        } else {
                                            viewModel.toggleMemberPayment(member)
                                        }
                                    },
                                    onCallClick = {
                                        dialNumber(context, member.phone)
                                    },
                                    onWhatsAppReminder = {
                                        val reminderText = viewModel.generateIndividualReminder(member)
                                        sendIndividualWhatsApp(context, member.phone, reminderText)
                                    },
                                    onEditClick = {
                                        viewModel.openEditMemberDialog(member)
                                    },
                                    onDeleteClick = {
                                        viewModel.deleteMember(member)
                                    }
                                )
                            }
                        }

                        // 10. Unlimited Add Member Slot Card
                        item {
                            AddMemberSlotCard(
                                isAdmin = uiState.isAdmin,
                                onAddClick = {
                                    if (uiState.isAdmin) {
                                        viewModel.openAddMemberDialog()
                                    } else {
                                        viewModel.showAuthDialog(true)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { viewModel.openNotificationsDialog(false) },
            onMarkAllRead = { viewModel.markAllNotificationsRead() },
            onShareReceipt = { notif ->
                shareReceipt(context, notif)
            }
        )
    }

    if (showPaymentDialog) {
        OnlinePaymentDialog(
            customQrCodeUri = customQrCodeUri ?: "",
            isAdmin = uiState.isAdmin,
            members = uiState.members,
            pendingRequestsCount = pendingPaymentRequests.size,
            upiId = fundUpiId,
            onUpdateUpiId = { viewModel.setFundUpiId(it) },
            onDismiss = { viewModel.openPaymentDialog(false) },
            onUploadQrCode = { uri -> viewModel.setCustomQrCodeUri(uri) },
            onRequestVerificationClick = {
                viewModel.openPaymentDialog(false)
                viewModel.openPaymentVerificationDialog(true)
            },
            onViewPendingRequestsClick = {
                viewModel.openPaymentDialog(false)
                viewModel.openPendingRequestsDialog(true)
            },
            onSubmitDirectDeposit = { member, mode, utr ->
                viewModel.updatePaymentWithDetails(member, "Paid", mode, "")
            }
        )
    }

    if (showPaymentVerificationDialog) {
        PaymentVerificationDialog(
            members = uiState.members,
            onDismiss = { viewModel.openPaymentVerificationDialog(false) },
            onSubmitRequest = { member, amt, screenshotUri, utr, note ->
                viewModel.submitPaymentVerificationRequest(member, amt, screenshotUri, utr, note)
            }
        )
    }

    if (showPendingRequestsDialog) {
        PendingPaymentRequestsDialog(
            requests = pendingPaymentRequests,
            onDismiss = { viewModel.openPendingRequestsDialog(false) },
            onViewScreenshot = { uri -> viewModel.viewScreenshot(uri) },
            onApprove = { req -> viewModel.approvePaymentRequest(req) },
            onReject = { req -> viewModel.rejectPaymentRequest(req) }
        )
    }

    if (showPendingRegistrationsDialog) {
        PendingMemberRegistrationsDialog(
            requests = pendingRegistrations,
            onDismiss = { viewModel.openPendingRegistrationsDialog(false) },
            onApprove = { req -> viewModel.approveRegistration(req) },
            onReject = { req, reason -> viewModel.rejectRegistration(req, reason) }
        )
    }

    screenshotToView?.let { uri ->
        ScreenshotViewerDialog(
            screenshotUri = uri,
            onDismiss = { viewModel.viewScreenshot(null) }
        )
    }

    if (showSetCycleTargetDialog) {
        SetCycleTargetDialog(
            currentCycleName = uiState.amavasyaCycleName,
            currentTargetAmount = uiState.customTargetAmount,
            currentConsensusNote = uiState.consensusNote,
            totalMembersCount = uiState.totalMembers,
            defaultCalculatedTarget = uiState.targetAmount,
            onDismiss = { viewModel.openSetCycleTargetDialog(false) },
            onSavePlan = { target, perMember, applyToAll, note, postToChat ->
                viewModel.setMonthlyCyclePlan(target, perMember, applyToAll, note, postToChat)
            }
        )
    }

    if (showAddEventDialog) {
        AddEventDialog(
            onDismiss = { viewModel.openAddEventDialog(false) },
            onSave = { title, date, desc, driveLink, photosJson ->
                viewModel.addEvent(title, date, desc, driveLink, photosJson)
            }
        )
    }

    eventPhotoToView?.let { (uri, _) ->
        EventPhotoViewerDialog(
            photoUri = uri,
            onDismiss = { eventPhotoToView = null }
        )
    }

    if (showUrgentBroadcastDialog) {
        UrgentBroadcastDialog(
            onDismiss = { viewModel.openUrgentBroadcastDialog(false) },
            onBroadcast = { title, msg -> viewModel.broadcastUrgentAlert(title, msg) }
        )
    }

    if (showAddWhatsAppDialog) {
        AddWhatsAppMessageDialog(
            onDismiss = { viewModel.openAddWhatsAppDialog(false) },
            onSave = { sender, msg -> viewModel.addWhatsAppMessage(sender, msg) }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentThemeMode = themeMode,
            currentLanguage = appLanguage,
            isAdmin = uiState.isAdmin,
            isAppLockEnabled = isAppLockEnabled,
            isBiometricEnabled = isBiometricEnabled,
            currentApkUrl = updateApkUrl,
            latestVersionNote = latestVersionNote,
            whatsAppGroupLink = whatsAppGroupLink,
            fundUpiId = fundUpiId,
            onUpdateWhatsAppGroupLink = { viewModel.setWhatsAppGroupLink(it) },
            onUpdateFundUpiId = { viewModel.setFundUpiId(it) },
            onRestoreFromLocalBackup = { viewModel.restoreFromLocalBackup() },
            onSyncFirestore = { viewModel.syncWithFirestore() },
            onDismiss = { viewModel.openSettingsDialog(false) },
            onThemeSelect = { mode -> viewModel.setThemeMode(mode) },
            onLanguageSelect = { lang -> viewModel.setAppLanguage(lang) },
            onToggleAppLock = { enabled -> viewModel.setAppLockEnabled(enabled) },
            onToggleBiometric = { enabled -> viewModel.setBiometricEnabled(enabled) },
            onChangeAppLockPinClick = { viewModel.showChangePinDialog(true) },
            onLockNowClick = { viewModel.lockAppNow() },
            onChangePinClick = {
                viewModel.openSettingsDialog(false)
                viewModel.showChangePinDialog(true)
            },
            onSendDailyThoughtNotification = { viewModel.sendDailyThoughtNotification() },
            onOpenAppUpdateConsole = { viewModel.openAppUpdateConsole(true) },
            onSwitchAdminRoleClick = {
                viewModel.openSettingsDialog(false)
                if (uiState.isAdmin) {
                    viewModel.logoutAdmin()
                } else {
                    viewModel.showAuthDialog(true)
                }
            },
            onUrgentBroadcastClick = {
                viewModel.openUrgentBroadcastDialog(true)
            },
            onSirenTgadaClick = {
                viewModel.triggerManualSirenTgada(context)
            },
            userFullName = currentUserFullName,
            userPhone = currentUserPhone,
            userMemberCode = currentUserMemberCode,
            onLogoutUser = {
                viewModel.logoutUser()
            }
        )
    }

    if (showCreateLoanDialog) {
        CreateLoanRequestDialog(
            members = uiState.members,
            onDismiss = { viewModel.openCreateLoanDialog(false) },
            onSubmit = { memId, name, phone, amt, purpose, days ->
                viewModel.submitLoanRequest(memId, name, phone, amt, purpose, days)
            }
        )
    }

    loanToEditByAdmin?.let { req ->
        EditLoanRequestDialog(
            request = req,
            onDismiss = { viewModel.openEditLoanDialog(null) },
            onSave = { updated -> viewModel.updateLoanRequestByAdmin(updated) }
        )
    }

    if (showOnlineMembersDialog) {
        OnlineMembersDialog(
            members = uiState.members,
            activeMemberId = activeMemberId,
            onSelectActiveMember = { id -> viewModel.setActiveMemberId(id) },
            onDismiss = { viewModel.openOnlineMembersDialog(false) }
        )
    }

    if (showAppUpdateConsoleDialog) {
        AppUpdateAndCodeConsoleDialog(
            currentApkUrl = updateApkUrl,
            currentVersionNote = latestVersionNote,
            onDismiss = { viewModel.openAppUpdateConsole(false) },
            onBroadcastUpdate = { ver, notes, url ->
                viewModel.broadcastAppUpdate(ver, notes, url)
            },
            onExecuteCodePatch = { code, onResult ->
                viewModel.executeCodePatch(code, onResult)
            }
        )
    }

    selectedMemberForProfile?.let { member ->
        MemberProfileDialog(
            member = member,
            isAdmin = uiState.isAdmin,
            profilePhotoUri = if (member.id == activeMemberId) currentUserProfilePhotoUri else "",
            onDismiss = { viewModel.openMemberProfile(null) },
            onEditClick = {
                val m = member
                viewModel.openMemberProfile(null)
                viewModel.openEditMemberDialog(m)
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { viewModel.openAddExpenseDialog(false) },
            onSave = { item, amt, cat, dt, slip ->
                viewModel.addExpense(item, amt, cat, dt, slip)
            }
        )
    }

    expenseReceiptToView?.let { expense ->
        SlipViewerDialog(
            expense = expense,
            onDismiss = { viewModel.viewExpenseReceipt(null) }
        )
    }



    if (showAuthDialog) {
        AdminPinDialog(
            onDismiss = { viewModel.showAuthDialog(false) },
            onVerify = { phone, pin -> viewModel.verifyAdminCredentials(phone, pin) }
        )
    }

    if (showAddEditDialog) {
        AddEditMemberDialog(
            memberToEdit = memberToEdit,
            onDismiss = { viewModel.closeAddEditDialog() },
            onSave = { name, phone, amount, notes, insta, fb, wa, city ->
                viewModel.saveMember(name, phone, amount, notes, insta, fb, wa, city)
            }
        )
    }

    memberForPaymentDetails?.let { member ->
        PaymentModeSelectionDialog(
            member = member,
            onDismiss = { memberForPaymentDetails = null },
            onConfirm = { mode, date, upiRef ->
                val finalMode = if (upiRef.isNotBlank()) "$mode ($upiRef)" else mode
                viewModel.updatePaymentWithDetails(member, "Paid", finalMode, date)
                memberForPaymentDetails = null
            }
        )
    }

    if (showResetConfirmDialog) {
        ResetCycleConfirmDialog(
            cycleName = uiState.amavasyaCycleName,
            onDismiss = { viewModel.showResetCycleDialog(false) },
            onConfirm = { viewModel.resetMonthlyCycle() }
        )
    }

    if (showHistoryDialog) {
        CycleHistoryDialog(
            historyList = uiState.cycleHistory,
            onDismiss = { viewModel.showHistoryDialog(false) }
        )
    }

    if (showChangePinDialog) {
        ChangePinDialog(
            onDismiss = { viewModel.showChangePinDialog(false) },
            onChangePin = { newPin ->
                viewModel.changeAppLockPin(newPin)
                if (uiState.isAdmin) {
                    viewModel.changeAdminPin(newPin)
                }
            }
        )
    }

    if (showEditCycleNameDialog) {
        EditCycleNameDialog(
            currentName = uiState.amavasyaCycleName,
            onDismiss = { showEditCycleNameDialog = false },
            onSave = { newName ->
                viewModel.setAmavasyaCycleName(newName)
                showEditCycleNameDialog = false
            }
        )
    }

    if (showDatePickerDialog) {
        AmavasyaDatePickerDialog(
            currentCountdown = viewModel.countdownInfo.value,
            onDismiss = { viewModel.showDatePickerDialog(false) },
            onSetCustomDate = { millis -> viewModel.setCustomAmavasyaDate(millis) },
            onResetToAuto = { viewModel.resetToAutoAmavasya() }
        )
    }
    }
}

@Composable
fun MainSectionTabs(
    selectedSection: String,
    unreadNotifCount: Int,
    messageCount: Int,
    loanRequestCount: Int = 0,
    eventCount: Int = 0,
    onSectionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Surface(
        color = if (isDark) Color(0xD91E293B) else Color(0xEEFFFFFF),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else BorderSlate),
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            SectionTabButton(
                title = "👥 सदस्य",
                isSelected = selectedSection == "members",
                onClick = { onSectionSelect("members") },
                modifier = Modifier.weight(1f)
            )

            SectionTabButton(
                title = "🤝 सहायता",
                isSelected = selectedSection == "loan",
                onClick = { onSectionSelect("loan") },
                badge = if (loanRequestCount > 0) loanRequestCount.toString() else null,
                modifier = Modifier.weight(1f)
            )

            SectionTabButton(
                title = "📒 बहीखाता",
                isSelected = selectedSection == "accounting",
                onClick = { onSectionSelect("accounting") },
                modifier = Modifier.weight(1f)
            )

            SectionTabButton(
                title = "💬 संवाद",
                isSelected = selectedSection == "chat",
                onClick = { onSectionSelect("chat") },
                badge = if (messageCount > 0) messageCount.toString() else null,
                modifier = Modifier.weight(1f)
            )

            SectionTabButton(
                title = "📷 फ़ोटो",
                isSelected = selectedSection == "gallery",
                onClick = { onSectionSelect("gallery") },
                badge = if (eventCount > 0) eventCount.toString() else null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SectionTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    val isDark = isSystemInDarkTheme()
    Surface(
        color = if (isSelected) (if (isDark) SaffronGold else DeepNavy) else (if (isDark) Color(0x400F172A) else Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(9.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) (if (isDark) Color(0xFF451A03) else Color.White) else (if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)),
                textAlign = TextAlign.Center
            )
            if (badge != null && !isSelected) {
                Surface(
                    color = Color(0xFFDC2626),
                    shape = CircleShape,
                    modifier = Modifier.padding(start = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TabFilterRow(
    selectedTab: String,
    totalCount: Int,
    paidCount: Int,
    pendingCount: Int,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Surface(
        color = if (isDark) Color(0xD91E293B) else Color(0xD9E2E8F0),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabButton(
                title = "सभी सदस्य",
                count = totalCount,
                isSelected = selectedTab == "all",
                onClick = { onTabSelected("all") },
                modifier = Modifier.weight(1f),
                tag = "tab_all"
            )

            TabButton(
                title = "जमा सूची",
                count = paidCount,
                isSelected = selectedTab == "paid",
                activeColor = PaidGreen,
                onClick = { onTabSelected("paid") },
                modifier = Modifier.weight(1f),
                tag = "tab_paid"
            )

            TabButton(
                title = "बाकी सूची",
                count = pendingCount,
                isSelected = selectedTab == "pending",
                activeColor = PendingRed,
                onClick = { onTabSelected("pending") },
                modifier = Modifier.weight(1f),
                tag = "tab_pending"
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = DeepNavy,
    tag: String
) {
    val isDark = isSystemInDarkTheme()
    val tabTextColor = when {
        isSelected && isDark && activeColor == DeepNavy -> SaffronGold
        isSelected -> activeColor
        isDark -> Color(0xFF94A3B8)
        else -> Color(0xFF475569)
    }
    Surface(
        color = if (isSelected) (if (isDark) Color(0xFF0F172A) else Color.White) else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = if (isSelected) 1.dp else 0.dp,
        modifier = modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$title ($count)",
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = tabTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BottomRoleBar(
    isAdmin: Boolean,
    onToggleAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(1.dp, BorderSlate)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "लॉगिन मोड:",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = if (isAdmin) "सिस्टम प्रबंधन स्तर (Active)" else "मानक सदस्य स्तर (Active)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAdmin) Color(0xFFB45309) else Color(0xFF1E293B)
                )
            }

            Surface(
                color = if (isAdmin) PendingRed else DeepNavy,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .clickable { onToggleAuth() }
                    .testTag("btn_auth_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isAdmin) Color.White else SaffronGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAdmin) "लॉगआउट" else "एडमिन लॉगिन",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyMembersPlaceholder(searchQuery: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔍",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (searchQuery.isNotEmpty()) "कोई सदस्य नहीं मिला" else "सूची में कोई सदस्य नहीं है",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
            )
            Text(
                text = if (searchQuery.isNotEmpty()) "'$searchQuery' से संबंधित कोई परिणाम नहीं मिला।" else "कृपया अन्य टैब चुनें या नया सदस्य जोड़ें।",
                fontSize = 11.5.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Intent Handlers
private fun shareToWhatsApp(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.whatsapp")
    }

    try {
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        val chooserIntent = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            "व्हाट्सएप या अन्य ऐप पर शेयर करें"
        )
        try {
            context.startActivity(chooserIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "शेयर करने के लिए कोई उपयुक्त ऐप नहीं मिला", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun sendIndividualWhatsApp(context: Context, phone: String, message: String) {
    val cleanPhone = phone.replace(Regex("[^0-9]"), "")
    val formattedPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        shareToWhatsApp(context, message)
    }
}

private fun dialNumber(context: Context, phone: String) {
    if (phone.isBlank() || phone == "N/A") {
        Toast.makeText(context, "मोबाइल नंबर उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
        return
    }
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "कॉल शुरू नहीं हो सकी", Toast.LENGTH_SHORT).show()
    }
}

private fun shareReceipt(context: Context, notif: DepositNotificationEntity) {
    val text = "🚩 *युवा शक्ति ग्रुप, भाटी परिवार* 🚩\n\n" +
               "🧾 *डिजिटल अंशदान पावती (Online Receipt)*\n" +
               "-----------------------------------\n" +
               "👤 *सदस्य:* ${notif.memberName}\n" +
               "💰 *जमा राशि:* ₹${notif.amount}\n" +
               "💳 *माध्यम:* ${notif.paymentMode}\n" +
               "📅 *तारीख:* ${notif.paymentDate}\n" +
               "✅ *स्थिति:* ऑनलाइन सत्यापित व दर्ज\n" +
               "-----------------------------------\n" +
               "समूह की पारदर्शिता एवं प्रगति में योगदान हेतु धन्यवाद! 🙏\n" +
               "- युवा शक्ति ग्रुप, भाटी परिवार"
    shareToWhatsApp(context, text)
}


@Composable
private fun RowScope.FloatingNavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    badgeColor: Color = Color(0xFFEF4444),
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFFE5F1FF) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color(0xFF007AFF) else Color(0xFF8E8E93),
                    modifier = Modifier.size(20.dp)
                )
            }

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF007AFF) else Color(0xFF8E8E93),
            maxLines = 1
        )
    }
}
