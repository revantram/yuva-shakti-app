package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CycleHistoryEntity
import com.example.data.DepositNotificationEntity
import com.example.data.EventGalleryEntity
import com.example.data.ExpenseEntity
import com.example.data.GroupMessageEntity
import com.example.data.MemberEntity
import com.example.data.MemberRepository
import com.example.data.PaymentRequestEntity
import com.example.data.LoanRequestEntity
import com.example.data.RegistrationRequest
import com.example.util.FirestoreSyncHelper
import com.example.util.InstallationHelper
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val members: List<MemberEntity> = emptyList(),
    val filteredMembers: List<MemberEntity> = emptyList(),
    val selectedTab: String = "all", // "all", "paid", "pending"
    val searchQuery: String = "",
    val isAdmin: Boolean = false,
    val totalMembers: Int = 0,
    val paidMembers: Int = 0,
    val pendingMembers: Int = 0,
    val totalCollected: Int = 0,
    val targetAmount: Int = 0,
    val progressPercent: Int = 0,
    val amavasyaCycleName: String = "कार्तिक अमावस्या चक्र (मासिक बैठक)",
    val amavasyaBannerState: String = "day_after", // "day_after", "advance", "regular"
    val cycleHistory: List<CycleHistoryEntity> = emptyList(),
    val customTargetAmount: Int = 0,
    val consensusNote: String = ""
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = MemberRepository(database.memberDao(), application)

    private val _selectedTab = MutableStateFlow("all")
    private val _searchQuery = MutableStateFlow("")
    private val _isAdmin = MutableStateFlow(repository.isUserAdminRole())
    private val _amavasyaCycleName = MutableStateFlow(repository.getAmavasyaCycleName())
    private val _amavasyaBannerState = MutableStateFlow(repository.getAmavasyaBannerState())
    private val _customTargetAmount = MutableStateFlow(repository.getCustomTargetAmount())
    private val _consensusNote = MutableStateFlow(repository.getConsensusNote())

    // Main section navigation: "web", "members", "accounting", "loan", "gallery", "chat"
    private val _selectedMainSection = MutableStateFlow("web")
    val selectedMainSection: StateFlow<String> = _selectedMainSection

    // Dialog state flows
    private val _showAuthDialog = MutableStateFlow(false)
    private val _showAddEditDialog = MutableStateFlow(false)
    private val _showSetCycleTargetDialog = MutableStateFlow(false)
    val showSetCycleTargetDialog: StateFlow<Boolean> = _showSetCycleTargetDialog
    private val _memberToEdit = MutableStateFlow<MemberEntity?>(null)
    private val _showResetConfirmDialog = MutableStateFlow(false)
    private val _showHistoryDialog = MutableStateFlow(false)
    private val _showChangePinDialog = MutableStateFlow(false)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    private val _showNotificationsDialog = MutableStateFlow(false)
    val showNotificationsDialog: StateFlow<Boolean> = _showNotificationsDialog

    private val _showPaymentDialog = MutableStateFlow(false)
    val showPaymentDialog: StateFlow<Boolean> = _showPaymentDialog

    private val _selectedMemberForProfile = MutableStateFlow<MemberEntity?>(null)
    val selectedMemberForProfile: StateFlow<MemberEntity?> = _selectedMemberForProfile

    private val _showAddExpenseDialog = MutableStateFlow(false)
    val showAddExpenseDialog: StateFlow<Boolean> = _showAddExpenseDialog

    private val _expenseReceiptToView = MutableStateFlow<ExpenseEntity?>(null)
    val expenseReceiptToView: StateFlow<ExpenseEntity?> = _expenseReceiptToView

    private val _targetAmavasyaMillis = MutableStateFlow(
        repository.getTargetAmavasyaMillis().let { saved ->
            val nextAuto = com.example.util.AmavasyaCalculator.calculateNextAmavasyaMillis()
            if (saved > 0) {
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = saved }
                if (cal.get(java.util.Calendar.YEAR) == 2026 &&
                    cal.get(java.util.Calendar.MONTH) == java.util.Calendar.NOVEMBER &&
                    (cal.get(java.util.Calendar.DAY_OF_MONTH) == 9 || cal.get(java.util.Calendar.DAY_OF_MONTH) == 10)
                ) {
                    repository.setTargetAmavasyaMillis(nextAuto)
                    nextAuto
                } else {
                    saved
                }
            } else {
                nextAuto
            }
        }
    )
    val targetAmavasyaMillis: StateFlow<Long> = _targetAmavasyaMillis

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val groupMessages: StateFlow<List<GroupMessageEntity>> = repository.allGroupMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notifications: StateFlow<List<DepositNotificationEntity>> = repository.allNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val events: StateFlow<List<EventGalleryEntity>> = repository.allEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _showAddEventDialog = MutableStateFlow(false)
    val showAddEventDialog: StateFlow<Boolean> = _showAddEventDialog

    private val _showUrgentBroadcastDialog = MutableStateFlow(false)
    val showUrgentBroadcastDialog: StateFlow<Boolean> = _showUrgentBroadcastDialog

    private val _showAddWhatsAppDialog = MutableStateFlow(false)
    val showAddWhatsAppDialog: StateFlow<Boolean> = _showAddWhatsAppDialog

    private val _themeMode = MutableStateFlow(repository.getThemeMode())
    val themeMode: StateFlow<String> = _themeMode

    private val _appLanguage = MutableStateFlow(repository.getAppLanguage())
    val appLanguage: StateFlow<String> = _appLanguage

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog

    private val _countdownInfo = MutableStateFlow(
        com.example.util.AmavasyaCalculator.computeCountdown(
            targetMillis = _targetAmavasyaMillis.value,
            currentMillis = System.currentTimeMillis(),
            isCustom = repository.getTargetAmavasyaMillis() > 0
        )
    )
    val countdownInfo: StateFlow<com.example.util.CountdownInfo> = _countdownInfo

    private val _showDatePickerDialog = MutableStateFlow(false)
    val showDatePickerDialog: StateFlow<Boolean> = _showDatePickerDialog

    init {
        viewModelScope.launch {
            try {
                repository.ensureInitialized()
            } catch (t: Throwable) {
                android.util.Log.e("MainViewModel", "ensureInitialized error", t)
            }
        }
        // Continuous realtime listeners running in ViewModel lifecycle
        try {
            com.example.util.FirestoreSyncHelper.listenToMembersRealtime { cloudMembers ->
                viewModelScope.launch {
                    try {
                        repository.syncMembersFromCloud(cloudMembers)
                    } catch (t: Throwable) {
                        android.util.Log.e("MainViewModel", "syncMembersFromCloud error", t)
                    }
                }
            }
            com.example.util.FirestoreSyncHelper.listenToSettings { upiId, waLink, target, cycleName, amavasyaMillis, apkUrl, version, notes ->
                try {
                    upiId?.let {
                        if (it.isNotBlank()) {
                            val cleanUpi = repository.sanitizeUpiId(it)
                            setFundUpiId(cleanUpi, showSnackbar = false)
                            if (cleanUpi != it.trim()) {
                                viewModelScope.launch {
                                    com.example.util.FirestoreSyncHelper.saveSettings(upiId = cleanUpi)
                                }
                            }
                        }
                    }
                    waLink?.let { if (it.isNotBlank()) setWhatsAppGroupLink(it) }
                    target?.let { if (it > 0) setCustomTargetAmount(it) }
                    cycleName?.let { if (it.isNotBlank()) setAmavasyaCycleName(it) }
                    amavasyaMillis?.let { if (it > 0) setTargetAmavasyaMillis(it) }
                } catch (t: Throwable) {
                    android.util.Log.e("MainViewModel", "listenToSettings error", t)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.e("MainViewModel", "Firestore listener init error", t)
        }
        viewModelScope.launch {
            while (true) {
                try {
                    val target = _targetAmavasyaMillis.value
                    val isCustom = repository.getTargetAmavasyaMillis() > 0
                    _countdownInfo.value = com.example.util.AmavasyaCalculator.computeCountdown(
                        targetMillis = target,
                        currentMillis = System.currentTimeMillis(),
                        isCustom = isCustom
                    )
                } catch (t: Throwable) {
                    // ignore
                }
                kotlinx.coroutines.delay(1000L)
            }
        }
        // Continuous 5-minute periodic auto-refresh loop from Firebase Firestore
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(5 * 60 * 1000L) // 5 minutes
                try {
                    android.util.Log.d("MainViewModel", "Continuous 5-minute auto-refresh from Firestore")
                    repository.syncWithFirestore()
                } catch (t: Throwable) {
                    android.util.Log.e("MainViewModel", "Periodic sync error", t)
                }
            }
        }

        // Listen to Pending Member Registrations in Realtime (for Admin verification)
        try {
            FirestoreSyncHelper.listenToPendingRegistrations { list ->
                _pendingRegistrations.value = list
            }
        } catch (t: Throwable) {
            android.util.Log.e("MainViewModel", "Pending registrations listener init error", t)
        }

        // If this device has a pending registration awaiting Admin verification, attach status listener
        val savedPendingReqId = repository.getPendingRegistrationRequestId()
        if (savedPendingReqId.isNotBlank() && !_isUserLoggedIn.value) {
            attachRegistrationStatusListener(savedPendingReqId)
        }
    }

    val allPaymentRequests: StateFlow<List<PaymentRequestEntity>> = repository.allPaymentRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingPaymentRequests: StateFlow<List<PaymentRequestEntity>> = repository.allPaymentRequests.map { list ->
        list.filter { it.status == "Pending" }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _customQrCodeUri = MutableStateFlow(repository.getCustomQrCodeUri())
    val customQrCodeUri: StateFlow<String> = _customQrCodeUri

    private val _fundUpiId = MutableStateFlow(repository.getUpiId())
    val fundUpiId: StateFlow<String> = _fundUpiId

    private val _whatsAppGroupLink = MutableStateFlow(repository.getWhatsAppGroupLink())
    val whatsAppGroupLink: StateFlow<String> = _whatsAppGroupLink

    private val _showPaymentVerificationDialog = MutableStateFlow(false)
    val showPaymentVerificationDialog: StateFlow<Boolean> = _showPaymentVerificationDialog

    private val _showPendingRequestsDialog = MutableStateFlow(false)
    val showPendingRequestsDialog: StateFlow<Boolean> = _showPendingRequestsDialog

    private val _screenshotToView = MutableStateFlow<String?>(null)
    val screenshotToView: StateFlow<String?> = _screenshotToView

    val allLoanRequests: StateFlow<List<LoanRequestEntity>> = repository.allLoanRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _showCreateLoanDialog = MutableStateFlow(false)
    val showCreateLoanDialog: StateFlow<Boolean> = _showCreateLoanDialog

    private val _loanToEditByAdmin = MutableStateFlow<LoanRequestEntity?>(null)
    val loanToEditByAdmin: StateFlow<LoanRequestEntity?> = _loanToEditByAdmin

    private val _isAppLockEnabled = MutableStateFlow(repository.isAppLockEnabled())
    val isAppLockEnabled: StateFlow<Boolean> = _isAppLockEnabled

    private val _isBiometricEnabled = MutableStateFlow(repository.isBiometricEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled

    private val _isAppLocked = MutableStateFlow(repository.isAppLockEnabled())
    val isAppLocked: StateFlow<Boolean> = _isAppLocked

    private val _showChangeAppLockPinDialog = MutableStateFlow(false)
    val showChangeAppLockPinDialog: StateFlow<Boolean> = _showChangeAppLockPinDialog

    private val _activeMemberId = MutableStateFlow(repository.getMyActiveMemberId())
    val activeMemberId: StateFlow<Long> = _activeMemberId

    private val _showOnlineMembersDialog = MutableStateFlow(false)
    val showOnlineMembersDialog: StateFlow<Boolean> = _showOnlineMembersDialog

    private val _pendingRegistrations = MutableStateFlow<List<RegistrationRequest>>(emptyList())
    val pendingRegistrations: StateFlow<List<RegistrationRequest>> = _pendingRegistrations

    private val _showPendingRegistrationsDialog = MutableStateFlow(false)
    val showPendingRegistrationsDialog: StateFlow<Boolean> = _showPendingRegistrationsDialog

    fun openPendingRegistrationsDialog(show: Boolean) {
        _showPendingRegistrationsDialog.value = show
    }

    private val _pendingRegistrationRequestId = MutableStateFlow(repository.getPendingRegistrationRequestId())
    val pendingRegistrationRequestId: StateFlow<String> = _pendingRegistrationRequestId

    private val _pendingRegistrationCode = MutableStateFlow(repository.getPendingRegistrationCode())
    val pendingRegistrationCode: StateFlow<String> = _pendingRegistrationCode

    private val _pendingRegistrationName = MutableStateFlow(repository.getPendingRegistrationName())
    val pendingRegistrationName: StateFlow<String> = _pendingRegistrationName

    private val _pendingRegistrationStatus = MutableStateFlow<RegistrationRequest?>(null)
    val pendingRegistrationStatus: StateFlow<RegistrationRequest?> = _pendingRegistrationStatus

    private val _isUserLoggedIn = MutableStateFlow(repository.isUserLoggedIn())
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _currentUserFullName = MutableStateFlow(repository.getUserFullName())
    val currentUserFullName: StateFlow<String> = _currentUserFullName

    private val _currentUserPhone = MutableStateFlow(repository.getUserPhone())
    val currentUserPhone: StateFlow<String> = _currentUserPhone

    private val _currentUserMemberCode = MutableStateFlow(repository.getUserMemberCode())
    val currentUserMemberCode: StateFlow<String> = _currentUserMemberCode

    private val _currentUserInstagram = MutableStateFlow(repository.getUserInstagram())
    val currentUserInstagram: StateFlow<String> = _currentUserInstagram

    private val _currentUserAddress = MutableStateFlow(repository.getUserAddress())
    val currentUserAddress: StateFlow<String> = _currentUserAddress

    private val _currentUserProfilePhotoUri = MutableStateFlow(repository.getUserProfilePhotoUri())
    val currentUserProfilePhotoUri: StateFlow<String> = _currentUserProfilePhotoUri

    private val _showWelcomeGreetingDialog = MutableStateFlow(false)
    val showWelcomeGreetingDialog: StateFlow<Boolean> = _showWelcomeGreetingDialog

    fun dismissWelcomeGreetingDialog() {
        _showWelcomeGreetingDialog.value = false
    }

    private val _filterState = combine(_selectedTab, _searchQuery, _isAdmin) { tab, query, isAdmin ->
        Triple(tab, query, isAdmin)
    }

    private data class CyclePlanData(
        val cycleName: String,
        val bannerState: String,
        val customTarget: Int,
        val consensusNote: String
    )

    private val _cyclePlanInfo = combine(
        _amavasyaCycleName,
        _amavasyaBannerState,
        _customTargetAmount,
        _consensusNote
    ) { cycleName, bannerState, customTarget, consensusNote ->
        CyclePlanData(cycleName, bannerState, customTarget, consensusNote)
    }

    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog
    val showAddEditDialog: StateFlow<Boolean> = _showAddEditDialog
    val memberToEdit: StateFlow<MemberEntity?> = _memberToEdit
    val showResetConfirmDialog: StateFlow<Boolean> = _showResetConfirmDialog
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog
    val showChangePinDialog: StateFlow<Boolean> = _showChangePinDialog
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    val uiState: StateFlow<MainUiState> = combine(
        repository.allMembers,
        _filterState,
        _cyclePlanInfo,
        repository.cycleHistory
    ) { members: List<MemberEntity>, (tab, query, isAdmin), plan: CyclePlanData, history: List<CycleHistoryEntity> ->
        val filteredBySearch = if (query.isBlank()) {
            members
        } else {
            members.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phone.contains(query, ignoreCase = true)
            }
        }

        val paidCount = members.count { it.status == "Paid" }
        val pendingCount = members.size - paidCount
        val totalCollected = members.filter { it.status == "Paid" }.sumOf { it.monthlyAmount }
        val targetAmount = if (plan.customTarget > 0) plan.customTarget else members.sumOf { it.monthlyAmount }
        val progress = if (targetAmount > 0) ((totalCollected.toFloat() / targetAmount) * 100).toInt() else 0

        val filteredByTab = when (tab) {
            "paid" -> filteredBySearch.filter { it.status == "Paid" }
            "pending" -> filteredBySearch.filter { it.status == "Pending" }
            else -> filteredBySearch
        }

        MainUiState(
            members = members,
            filteredMembers = filteredByTab,
            selectedTab = tab,
            searchQuery = query,
            isAdmin = isAdmin,
            totalMembers = members.size,
            paidMembers = paidCount,
            pendingMembers = pendingCount,
            totalCollected = totalCollected,
            targetAmount = targetAmount,
            progressPercent = progress,
            amavasyaCycleName = plan.cycleName,
            amavasyaBannerState = plan.bannerState,
            cycleHistory = history,
            customTargetAmount = plan.customTarget,
            consensusNote = plan.consensusNote
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    fun setTab(tab: String) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showAuthDialog(show: Boolean) {
        _showAuthDialog.value = show
    }

    fun verifyAdminCredentials(enteredPhone: String, enteredPin: String): Boolean {
        val cleanPhone = enteredPhone.trim().replace("\\s".toRegex(), "").replace("-", "")
        val isAdminPhone = cleanPhone == "8696663615" || cleanPhone.endsWith("8696663615")
        val isPinCorrect = repository.verifyAdminPin(enteredPin)

        return if (isAdminPhone && isPinCorrect) {
            repository.setAdminMode(true)
            _isAdmin.value = true
            _showAuthDialog.value = false
            _snackbarMessage.value = "व्यवस्थापक (8696663615) सत्यापित! एडमिन मोड सक्रिय हुआ।"
            true
        } else {
            false
        }
    }

    fun verifyAdminPin(enteredPin: String): Boolean {
        val success = repository.verifyAdminPin(enteredPin)
        if (success) {
            repository.setAdminMode(true)
            _isAdmin.value = true
            _showAuthDialog.value = false
            _snackbarMessage.value = "एडमिन मोड सक्रिय हुआ! सभी संपादन चालू हैं।"
        }
        return success
    }

    fun logoutAdmin() {
        repository.setAdminMode(false)
        _isAdmin.value = false
        _snackbarMessage.value = "सामान्य सदस्य मोड में वापस लौटे।"
    }

    fun changeAdminPin(newPin: String) {
        repository.setAdminPin(newPin)
        _showChangePinDialog.value = false
        _snackbarMessage.value = "एडमिन पिन सफलतापूर्वक बदला गया।"
    }

    fun toggleMemberPayment(member: MemberEntity, preferredMode: String = "UPI") {
        viewModelScope.launch {
            repository.toggleMemberPayment(member, preferredMode)
        }
    }

    fun updatePaymentWithDetails(member: MemberEntity, status: String, mode: String, date: String) {
        viewModelScope.launch {
            if (status == "Paid") {
                repository.recordDeposit(member, mode, date)
                _snackbarMessage.value = "${member.name} का ₹${member.monthlyAmount} भुगतान दर्ज हुआ! ($mode)"
            } else {
                repository.updateMember(
                    member.copy(
                        status = status,
                        paymentMode = "",
                        paidDate = ""
                    )
                )
                _snackbarMessage.value = "${member.name} की स्थिति 'बाकी' सेट की गई।"
            }
        }
    }

    fun openAddMemberDialog() {
        _memberToEdit.value = null
        _showAddEditDialog.value = true
    }

    fun openEditMemberDialog(member: MemberEntity) {
        _memberToEdit.value = member
        _showAddEditDialog.value = true
    }

    fun closeAddEditDialog() {
        _showAddEditDialog.value = false
        _memberToEdit.value = null
    }

    fun setMainSection(section: String) {
        _selectedMainSection.value = section
    }

    fun openNotificationsDialog(show: Boolean) {
        _showNotificationsDialog.value = show
    }

    fun openPaymentDialog(show: Boolean) {
        _showPaymentDialog.value = show
    }

    fun openMemberProfile(member: MemberEntity?) {
        _selectedMemberForProfile.value = member
    }

    fun openAddExpenseDialog(show: Boolean) {
        _showAddExpenseDialog.value = show
    }

    fun viewExpenseReceipt(expense: ExpenseEntity?) {
        _expenseReceiptToView.value = expense
    }

    fun getUpiId(): String = _fundUpiId.value

    fun setFundUpiId(id: String, showSnackbar: Boolean = true) {
        val clean = repository.sanitizeUpiId(id)
        _fundUpiId.value = clean
        repository.setUpiId(clean)
        if (showSnackbar) {
            _snackbarMessage.value = "UPI आईडी / PhonePe नंबर अपडेट हुआ: $clean"
        }
    }

    fun setWhatsAppGroupLink(link: String) {
        _whatsAppGroupLink.value = link.trim()
        repository.setWhatsAppGroupLink(link.trim())
        _snackbarMessage.value = "व्हाट्सएप ग्रुप लिंक अपडेट हुआ।"
    }

    fun syncMembersFromCloud(cloudMembers: List<MemberEntity>) {
        viewModelScope.launch {
            repository.syncMembersFromCloud(cloudMembers)
        }
    }

    fun syncWithFirestore() {
        refreshAllDataFromFirebase(isSilent = false)
    }

    fun refreshAllDataFromFirebase(isSilent: Boolean = false) {
        viewModelScope.launch {
            try {
                val ok = repository.syncWithFirestore()
                if (!isSilent) {
                    if (ok) {
                        _snackbarMessage.value = "🔥 Firebase Firestore से डेटा ताज़ा (Refresh) हुआ!"
                    } else {
                        _snackbarMessage.value = "⚠️ Firestore सिंक नहीं हो सका, कृपया इंटरनेट चेक करें।"
                    }
                }
            } catch (e: Exception) {
                if (!isSilent) {
                    _snackbarMessage.value = "त्रुटि: ${e.message}"
                }
            }
        }
    }

    fun restoreFromCloudBackup(jsonString: String, onComplete: (Boolean, Int) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val count = repository.restoreFromCloudBackup(jsonString)
                _fundUpiId.value = repository.getUpiId()
                _whatsAppGroupLink.value = repository.getWhatsAppGroupLink()
                _amavasyaCycleName.value = repository.getAmavasyaCycleName()
                _snackbarMessage.value = "✅ डेटा सफलतापूर्वक रीस्टोर हुआ ($count सदस्य)!"
                onComplete(true, count)
            } catch (e: Exception) {
                _snackbarMessage.value = "डेटा रीस्टोर त्रुटि: ${e.message}"
                onComplete(false, 0)
            }
        }
    }

    fun restoreFromLocalBackup(onComplete: (Boolean, Int) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val file = repository.getLocalBackupFile()
                if (file != null && file.exists()) {
                    val content = file.readText(Charsets.UTF_8)
                    val count = repository.restoreFromCloudBackup(content)
                    _fundUpiId.value = repository.getUpiId()
                    _whatsAppGroupLink.value = repository.getWhatsAppGroupLink()
                    _amavasyaCycleName.value = repository.getAmavasyaCycleName()
                    _snackbarMessage.value = "✅ स्थानीय बैकअप से $count सदस्य पुनर्स्थापित हुए!"
                    onComplete(true, count)
                } else {
                    _snackbarMessage.value = "कोई बैकअप फ़ाइल नहीं मिली।"
                    onComplete(false, 0)
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "रीस्टोर त्रुटि: ${e.message}"
                onComplete(false, 0)
            }
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun addExpense(itemName: String, amount: Int, category: String, date: String, slipUri: String) {
        viewModelScope.launch {
            val userTitle = if (_isAdmin.value) "एडमिन" else "सदस्य"
            repository.insertExpense(
                itemName = itemName,
                amount = amount,
                category = category,
                date = date,
                receiptSlipUri = slipUri,
                recordedBy = userTitle
            )
            _showAddExpenseDialog.value = false
            _snackbarMessage.value = "नया खर्च बहीखाते में दर्ज किया गया (₹$amount)"
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _snackbarMessage.value = "${expense.itemName} का खर्च हटाया गया।"
        }
    }

    fun sendGroupMessage(senderName: String, message: String) {
        viewModelScope.launch {
            val role = if (_isAdmin.value) "मुख्य व्यवस्थापक (Admin)" else "सदस्य"
            repository.sendGroupMessage(
                senderName = senderName,
                senderRole = role,
                message = message
            )
        }
    }

    fun submitOnlineDeposit(member: MemberEntity, mode: String, utr: String) {
        viewModelScope.launch {
            val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            repository.recordDeposit(
                member = member,
                mode = if (utr.isNotBlank()) "$mode (UTR: $utr)" else mode,
                date = todayStr
            )
            _showPaymentDialog.value = false
            _snackbarMessage.value = "${member.name} का ₹${member.monthlyAmount} ऑनलाइन जमा दर्ज हुआ! पावती जारी।"
        }
    }

    fun saveMember(
        name: String,
        phone: String,
        amount: Int,
        notes: String,
        instagramId: String = "",
        facebookId: String = "",
        whatsappNumber: String = "",
        villageCity: String = ""
    ) {
        viewModelScope.launch {
            val current = _memberToEdit.value
            if (current != null) {
                repository.updateMember(
                    current.copy(
                        name = name.trim(),
                        phone = phone.trim(),
                        monthlyAmount = amount,
                        notes = notes.trim(),
                        instagramId = instagramId.trim(),
                        facebookId = facebookId.trim(),
                        whatsappNumber = whatsappNumber.trim(),
                        villageCity = villageCity.trim()
                    )
                )
                _snackbarMessage.value = "सदस्य विवरण व प्रोफ़ाइल अपडेट की गई।"
            } else {
                val newMember = MemberEntity(
                    name = name.trim(),
                    phone = phone.trim(),
                    monthlyAmount = amount,
                    status = "Pending",
                    notes = notes.trim(),
                    sortOrder = (uiState.value.members.maxOfOrNull { it.sortOrder } ?: 0) + 1,
                    instagramId = instagramId.trim(),
                    facebookId = facebookId.trim(),
                    whatsappNumber = whatsappNumber.trim(),
                    villageCity = villageCity.trim()
                )
                repository.insertMember(newMember)
                _snackbarMessage.value = "नया सदस्य जोड़ा गया। अब कुल सदस्य: ${uiState.value.members.size + 1}"
            }
            closeAddEditDialog()
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member)
            _snackbarMessage.value = "${member.name} को हटाया गया।"
        }
    }

    fun showResetCycleDialog(show: Boolean) {
        _showResetConfirmDialog.value = show
    }

    fun resetMonthlyCycle() {
        viewModelScope.launch {
            val state = uiState.value
            repository.resetMonthlyCycle(
                cycleName = state.amavasyaCycleName,
                totalCollected = state.totalCollected,
                targetAmount = state.targetAmount,
                totalMembers = state.totalMembers,
                paidMembers = state.paidMembers
            )
            _showResetConfirmDialog.value = false
            _snackbarMessage.value = "नया अमावस्या चक्र शुरू किया गया। पिछला रिकॉर्ड सहेजा गया।"
        }
    }

    fun setAmavasyaBannerState(state: String) {
        _amavasyaBannerState.value = state
        repository.setAmavasyaBannerState(state)
    }

    fun setAmavasyaCycleName(name: String) {
        _amavasyaCycleName.value = name
        repository.setAmavasyaCycleName(name)
    }

    fun showHistoryDialog(show: Boolean) {
        _showHistoryDialog.value = show
    }

    fun showChangePinDialog(show: Boolean) {
        _showChangePinDialog.value = show
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun generateWhatsAppReport(): String {
        val members = uiState.value.members
        val paid = members.filter { it.status == "Paid" }
        val pending = members.filter { it.status == "Pending" }
        val totalAmt = paid.sumOf { it.monthlyAmount }
        val targetAmt = if (uiState.value.targetAmount > 0) uiState.value.targetAmount else members.sumOf { it.monthlyAmount }
        val percent = if (targetAmt > 0) ((totalAmt.toFloat() / targetAmt) * 100).toInt() else 0

        val cd = countdownInfo.value
        val sb = StringBuilder()
        sb.append("🚩 *युवा शक्ति ग्रुप, भाटी परिवार* 🚩\n")
        sb.append("*मासिक अमावस्या अंशदान रिपोर्ट*\n")
        sb.append("📅 ${uiState.value.amavasyaCycleName}\n")
        if (uiState.value.consensusNote.isNotBlank()) {
            sb.append("📝 *निर्णय:* ${uiState.value.consensusNote}\n")
        }
        sb.append("🌑 अगली अमावस्या: ${cd.formattedDate} (${cd.hindiTithiName})\n")
        val countdownText = if (cd.isToday) "🎯 आज बैठक व अंशदान का दिन है!" else "⏳ बैठक में ${cd.days} दिन शेष"
        sb.append("⏱️ स्थिति: $countdownText\n")
        sb.append("-----------------------------------\n")
        sb.append("✅ *जमा करने वाले सदस्य (${paid.size}):*\n")
        paid.forEachIndexed { i, m ->
            val modeStr = if (m.paymentMode.isNotBlank()) " (${m.paymentMode})" else ""
            sb.append("${i + 1}. ${m.name} - ₹${m.monthlyAmount}$modeStr\n")
        }

        sb.append("\n⏳ *बकाया सदस्य (${pending.size}):*\n")
        pending.forEachIndexed { i, m ->
            sb.append("${i + 1}. ${m.name} - ₹${m.monthlyAmount}\n")
        }

        sb.append("-----------------------------------\n")
        sb.append("💰 *कुल एकत्रित राशि:* ₹$totalAmt\n")
        sb.append("🎯 *कुल लक्ष्य:* ₹$targetAmt ($percent% पूर्ण)\n")
        sb.append("(यह संदेश रिकॉर्ड एवं पारदर्शिता हेतु साझा किया गया है)")
        return sb.toString()
    }

    fun generateIndividualReminder(member: MemberEntity): String {
        val cd = countdownInfo.value
        val dateLine = if (cd.days > 0) "अगली अमावस्या ${cd.formattedDate} (${cd.days} दिन शेष) को है।" else "आज अमावस्या का दिन है।"
        return "🚩 *युवा शक्ति ग्रुप, भाटी परिवार* 🚩\n\n" +
                " सादर जय श्री कृष्णा / राम राम सा!\n\n" +
                "प्रिय ${member.name} जी,\n" +
                "मासिक अमावस्या फंड का आपका अंशदान (₹${member.monthlyAmount}) अभी बाकी है। $dateLine\n" +
                "कृपया समूह की पारदर्शिता एवं आगामी कार्यक्रमों की व्यवस्था हेतु अपनी राशि जल्द से जल्द जमा कराएं।\n\n" +
                "धन्यवाद! 🙏\n" +
                "- युवा शक्ति ग्रुप, भाटी परिवार"
    }

    fun showDatePickerDialog(show: Boolean) {
        _showDatePickerDialog.value = show
    }

    fun setCustomAmavasyaDate(targetMillis: Long) {
        _targetAmavasyaMillis.value = targetMillis
        repository.setTargetAmavasyaMillis(targetMillis)
        _showDatePickerDialog.value = false
        _snackbarMessage.value = "अगली अमावस्या की तारीख अपडेट की गई।"
    }

    fun resetToAutoAmavasya() {
        val autoMillis = com.example.util.AmavasyaCalculator.calculateNextAmavasyaMillis()
        _targetAmavasyaMillis.value = autoMillis
        repository.setTargetAmavasyaMillis(0L)
        _showDatePickerDialog.value = false
        _snackbarMessage.value = "स्वचालित खगोलीय अमावस्या तिथि सेट की गई।"
    }

    fun setTargetAmavasyaMillis(millis: Long) {
        setCustomAmavasyaDate(millis)
    }

    fun setCustomTargetAmount(amount: Int) {
        _customTargetAmount.value = amount
        repository.setCustomTargetAmount(amount)
    }

    fun openPaymentVerificationDialog(show: Boolean) {
        _showPaymentVerificationDialog.value = show
    }

    fun openPendingRequestsDialog(show: Boolean) {
        _showPendingRequestsDialog.value = show
    }

    fun viewScreenshot(uri: String?) {
        _screenshotToView.value = uri
    }

    fun setCustomQrCodeUri(uri: String) {
        _customQrCodeUri.value = uri
        repository.setCustomQrCodeUri(uri)
        _snackbarMessage.value = "नया QR कोड सफलतापूर्वक अपडेट किया गया!"
    }

    fun submitPaymentVerificationRequest(
        member: MemberEntity,
        amount: Int,
        screenshotUri: String,
        utrOrRef: String,
        note: String
    ) {
        viewModelScope.launch {
            repository.submitPaymentRequest(
                member = member,
                amount = amount,
                screenshotUri = screenshotUri,
                utrOrRef = utrOrRef,
                note = note
            )
            _showPaymentVerificationDialog.value = false
            _snackbarMessage.value = "भुगतान सत्यापन अनुरोध भेजा गया! एडमिन जाँचकर जमा दर्ज करेंगे।"
        }
    }

    fun approvePaymentRequest(request: PaymentRequestEntity) {
        viewModelScope.launch {
            repository.approvePaymentRequest(request, uiState.value.members)
            _snackbarMessage.value = "${request.memberName} का भुगतान स्वीकृत एवं जमा दर्ज हुआ!"
        }
    }

    fun rejectPaymentRequest(request: PaymentRequestEntity, reason: String = "") {
        viewModelScope.launch {
            repository.rejectPaymentRequest(request, reason)
            _snackbarMessage.value = "${request.memberName} का अनुरोध अस्वीकृत किया गया।"
        }
    }

    fun openSetCycleTargetDialog(show: Boolean) {
        _showSetCycleTargetDialog.value = show
    }

    fun setMonthlyCyclePlan(
        targetAmount: Int,
        perMemberAmount: Int,
        applyToAllMembers: Boolean,
        note: String,
        postToChat: Boolean
    ) {
        viewModelScope.launch {
            _customTargetAmount.value = targetAmount
            repository.setCustomTargetAmount(targetAmount)

            _consensusNote.value = note
            repository.setConsensusNote(note)

            if (applyToAllMembers && perMemberAmount > 0) {
                repository.updateAllMembersAmount(perMemberAmount)
            }

            if (postToChat && (note.isNotBlank() || targetAmount > 0 || perMemberAmount > 0)) {
                val chatNotice = buildString {
                    append("📢 *मासिक लक्ष्य एवं अंशदान घोषणा*\n")
                    if (targetAmount > 0) append("🎯 कुल लक्ष्य: ₹$targetAmount\n")
                    if (perMemberAmount > 0) append("👥 सर्वसम्मति अंशदान: ₹$perMemberAmount प्रति सदस्य\n")
                    if (note.isNotBlank()) append("📝 निर्णय: $note")
                }
                repository.sendGroupMessage(
                    senderName = "मुख्य व्यवस्थापक (Admin)",
                    senderRole = "एडमिन",
                    message = chatNotice
                )
            }

            _showSetCycleTargetDialog.value = false
            _snackbarMessage.value = "इस माह का लक्ष्य व अंशदान सफलतापूर्वक अपडेट किया गया।"
        }
    }

    fun openAddEventDialog(show: Boolean) {
        _showAddEventDialog.value = show
    }

    fun openUrgentBroadcastDialog(show: Boolean) {
        _showUrgentBroadcastDialog.value = show
    }

    fun openAddWhatsAppDialog(show: Boolean) {
        _showAddWhatsAppDialog.value = show
    }

    fun addEvent(title: String, eventDate: String, description: String, driveLink: String, photosJson: String) {
        viewModelScope.launch {
            repository.insertEvent(title, eventDate, description, driveLink, photosJson)
            _showAddEventDialog.value = false
            _snackbarMessage.value = "कार्यक्रम फोटो व डाउनलोड लिंक गैलरी में जोड़ी गई!"
        }
    }

    fun deleteEvent(event: EventGalleryEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            _snackbarMessage.value = "${event.title} गैलरी से हटाया गया।"
        }
    }

    fun broadcastUrgentAlert(title: String, message: String) {
        viewModelScope.launch {
            val adminName = if (_isAdmin.value) "रेवंत राम जी (व्यवस्थापक)" else "मुख्य व्यवस्थापक"
            val pendingMembers = uiState.value.members.filter { it.status == "Pending" }
            repository.broadcastUrgentAlert(title, message, adminName)
            com.example.util.FirestoreSyncHelper.broadcastEmergencyAlert(
                title = title,
                message = message,
                adminName = adminName,
                isSiren = true,
                pendingMembers = pendingMembers
            )
            _showUrgentBroadcastDialog.value = false
            _snackbarMessage.value = "🚨 आवश्यक सूचना सभी सदस्यों के मोबाइल पर सायरन के साथ प्रसारित की गई!"
        }
    }

    fun addWhatsAppMessage(sender: String, message: String) {
        viewModelScope.launch {
            repository.addWhatsAppMessage(sender, message)
            _showAddWhatsAppDialog.value = false
            _snackbarMessage.value = "व्हाट्सएप संदेश ग्रुप संवाद में जोड़ा गया।"
        }
    }

    fun openSettingsDialog(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        repository.setThemeMode(mode)
        val text = when (mode) {
            "auto_time" -> "🌅 सुबह लाइट व रात में डार्क थीम सेट हुई"
            "light" -> "⚪ लाइट मोड (सफेद) सेट हुआ"
            "dark" -> "⚫ डार्क मोड सेट हुआ"
            else -> "📱 सिस्टम थीम मोड सेट हुआ"
        }
        _snackbarMessage.value = text
    }

    fun setAppLanguage(lang: String) {
        _appLanguage.value = lang
        repository.setAppLanguage(lang)
        _snackbarMessage.value = if (lang == "en") "Language changed to English" else "भाषा बदलकर हिन्दी की गई"
    }

    fun sendDailyThoughtNotification() {
        val thought = com.example.util.DailyThoughtHelper.getTodayThought()
        com.example.util.NotificationHelper.sendMorningThoughtNotification(
            context = getApplication(),
            thoughtText = thought.hindi
        )
        _snackbarMessage.value = "आज का सुविचार नोटिफिकेशन में भेजा गया!"
    }

    // Loan Assistance methods
    fun openCreateLoanDialog(show: Boolean) {
        _showCreateLoanDialog.value = show
    }

    fun openEditLoanDialog(request: LoanRequestEntity?) {
        _loanToEditByAdmin.value = request
    }

    fun submitLoanRequest(
        memberId: Long,
        memberName: String,
        memberPhone: String,
        amount: Int,
        purpose: String,
        returnDays: Int
    ) {
        viewModelScope.launch {
            repository.submitLoanRequest(
                memberId = memberId,
                memberName = memberName,
                memberPhone = memberPhone,
                amountRequested = amount,
                purpose = purpose,
                returnPromiseDays = returnDays
            )
            _showCreateLoanDialog.value = false
            _snackbarMessage.value = "🤝 सहायता अनुरोध सफलतापूर्वक दर्ज हुआ! सभी सदस्यों को सूचना प्रेषित हुई।"
        }
    }

    fun voteLoanRequest(request: LoanRequestEntity, isUpVote: Boolean) {
        viewModelScope.launch {
            repository.voteLoanRequest(
                request = request,
                voterMemberId = _activeMemberId.value,
                isUpVote = isUpVote
            )
            val voteText = if (isUpVote) "👍 आपका समर्थन दर्ज हुआ" else "👎 आपकी असहमति दर्ज हुई"
            _snackbarMessage.value = voteText
        }
    }

    fun updateLoanRequestByAdmin(request: LoanRequestEntity) {
        viewModelScope.launch {
            repository.updateLoanRequestByAdmin(request)
            _loanToEditByAdmin.value = null
            _snackbarMessage.value = "सहायता अनुरोध विवरण अपडेट किया गया।"
        }
    }

    fun setLoanRequestStatus(request: LoanRequestEntity, newStatus: String, adminNote: String = "") {
        viewModelScope.launch {
            repository.setLoanRequestStatus(request, newStatus, adminNote)
            _snackbarMessage.value = "स्थिति अपडेट हुई: $newStatus"
        }
    }

    // App Lock methods
    fun unlockAppWithPin(pin: String): Boolean {
        val cleanPin = pin.trim()
        val correctAdminPin = "992942"
        val userPersonalPin = repository.getUserPersonalPin()
        val appLockPin = repository.getAppLockPin()

        return if (cleanPin == correctAdminPin || repository.verifyAdminPin(cleanPin)) {
            repository.setAdminMode(true)
            _isAdmin.value = true
            _isAppLocked.value = false
            _snackbarMessage.value = "एडमिन मोड सक्रिय हुआ!"
            true
        } else if (cleanPin == userPersonalPin || cleanPin == appLockPin) {
            _isAdmin.value = repository.isUserAdminRole()
            _isAppLocked.value = false
            true
        } else {
            false
        }
    }

    suspend fun isCodeTaken(code: String, excludeMemberId: Long = 0L): Boolean {
        return repository.isMemberCodeTaken(code, excludeMemberId)
    }

    fun loginWithCode(code: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cleanCode = code.trim()
            if (cleanCode == "992942") {
                // Official Admin Login
                val adminName = "रेवंत राम जी (व्यवस्थापक)"
                val adminPhone = "8696663615"
                repository.saveUserProfile(
                    name = adminName,
                    phone = adminPhone,
                    address = "कुचेरा",
                    photoUri = repository.getUserProfilePhotoUri(),
                    insta = "",
                    pin = "992942",
                    isAdmin = true,
                    memberCode = "992942"
                )
                repository.setAdminMode(true)
                _isUserLoggedIn.value = true
                _isAdmin.value = true
                _currentUserFullName.value = adminName
                _currentUserPhone.value = adminPhone
                _currentUserMemberCode.value = "992942"
                _activeMemberId.value = 1L
                repository.setMyActiveMemberId(1L)
                _showWelcomeGreetingDialog.value = true
                _snackbarMessage.value = "🛡️ मुख्य व्यवस्थापक (एडमिन) कोड सत्यापित! संपूर्ण नियंत्रण अनलॉक।"
                onResult(true, "✅ व्यवस्थापक लॉगिन सफल!")
                return@launch
            }

            // Member Login via Unique Member Code
            val member = repository.findMemberByCode(cleanCode)
            if (member != null) {
                val isAdmin = (member.phone == "8696663615" || cleanCode == "992942")
                val codeToSave = if (cleanCode.isNotBlank()) cleanCode else (1000 + member.id).toString()
                repository.saveUserProfile(
                    name = member.name,
                    phone = member.phone,
                    address = member.villageCity,
                    photoUri = repository.getUserProfilePhotoUri(),
                    insta = member.instagramId,
                    pin = if (isAdmin) "992942" else "",
                    isAdmin = isAdmin,
                    memberCode = codeToSave
                )
                repository.setAdminMode(isAdmin)
                _isUserLoggedIn.value = true
                _isAdmin.value = isAdmin
                _currentUserFullName.value = member.name
                _currentUserPhone.value = member.phone
                _currentUserMemberCode.value = codeToSave
                _currentUserAddress.value = member.villageCity
                _currentUserInstagram.value = member.instagramId
                _activeMemberId.value = member.id
                repository.setMyActiveMemberId(member.id)
                _showWelcomeGreetingDialog.value = true
                _snackbarMessage.value = "👋 स्वागत है, ${member.name}! (सदस्य कोड: $codeToSave)"
                onResult(true, "✅ स्वागत है, ${member.name} जी!")
            } else {
                onResult(false, "❌ यह सदस्य कोड सिस्टम में दर्ज नहीं है। कृपया सही कोड दर्ज करें या नया पंजीकरण करें।")
            }
        }
    }

    fun attachRegistrationStatusListener(reqId: String) {
        if (reqId.isBlank()) return
        FirestoreSyncHelper.listenToRegistrationStatus(reqId) { req ->
            _pendingRegistrationStatus.value = req
            if (req.status == "Approved") {
                viewModelScope.launch {
                    val finalCode = if (req.memberCode.isNotBlank()) req.memberCode else (1000 + req.assignedMemberId).toString()
                    repository.saveUserProfile(
                        name = req.name,
                        phone = req.phone,
                        address = req.address,
                        photoUri = req.photoUri,
                        insta = req.instagramId,
                        pin = "",
                        isAdmin = false,
                        memberCode = finalCode
                    )
                    repository.clearPendingRegistration()
                    _pendingRegistrationRequestId.value = ""
                    _pendingRegistrationCode.value = ""
                    _pendingRegistrationName.value = ""
                    _isUserLoggedIn.value = true
                    _isAdmin.value = false
                    _currentUserFullName.value = req.name
                    _currentUserPhone.value = req.phone
                    _currentUserMemberCode.value = finalCode
                    _currentUserAddress.value = req.address
                    _activeMemberId.value = req.assignedMemberId
                    repository.setMyActiveMemberId(req.assignedMemberId)
                    _showWelcomeGreetingDialog.value = true
                    _snackbarMessage.value = "🎉 बधाई हो! व्यवस्थापक ने आपका पंजीकरण सत्यापित कर दिया है। युवा शक्ति ग्रुप में आपका स्वागत है!"
                    repository.syncWithFirestore()
                }
            }
        }
    }

    fun submitMemberRegistrationRequest(
        name: String,
        phone: String,
        address: String,
        memberCode: String,
        photoUri: String,
        insta: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanCode = memberCode.trim()
            val cleanPhone = phone.trim()
            val cleanName = name.trim()
            val cleanAddress = address.trim()

            if (cleanName.isBlank()) {
                onResult(false, "कृपया अपना पूरा नाम दर्ज करें।")
                return@launch
            }
            if (cleanPhone.length < 10) {
                onResult(false, "कृपया 10 अंकों का मान्य मोबाइल नंबर दर्ज करें।")
                return@launch
            }
            if (cleanAddress.isBlank()) {
                onResult(false, "कृपया गाँव / शहर का नाम दर्ज करें।")
                return@launch
            }
            if (cleanCode.length < 4) {
                onResult(false, "कृपया कम से कम 4-अंकों का सदस्य कोड दर्ज करें।")
                return@launch
            }

            if (repository.isMemberCodeTaken(cleanCode)) {
                onResult(false, "⚠️ यह सदस्य कोड '$cleanCode' पहले से किसी अन्य सदस्य द्वारा उपयोग में है! कृपया दूसरा कोड चुनें।")
                return@launch
            }

            val reqId = "reg_" + System.currentTimeMillis() + "_" + (100..999).random()
            val deviceId = InstallationHelper.getDeviceUuid(getApplication())

            val req = RegistrationRequest(
                requestId = reqId,
                name = cleanName,
                phone = cleanPhone,
                address = cleanAddress.ifBlank { "कुचेरा" },
                memberCode = cleanCode,
                monthlyAmount = 100,
                instagramId = insta.trim(),
                photoUri = photoUri.trim(),
                status = "Pending",
                timestamp = System.currentTimeMillis(),
                deviceId = deviceId
            )

            val ok = FirestoreSyncHelper.submitRegistrationRequest(req)
            if (ok) {
                repository.savePendingRegistrationRequestId(reqId, cleanCode, cleanName)
                _pendingRegistrationRequestId.value = reqId
                _pendingRegistrationCode.value = cleanCode
                _pendingRegistrationName.value = cleanName
                _pendingRegistrationStatus.value = req

                attachRegistrationStatusListener(reqId)
                onResult(true, "✅ पंजीकरण अनुरोध व्यवस्थापक (Admin) को सत्यापन हेतु भेजा गया!")
            } else {
                onResult(false, "❌ ऑनलाइन पंजीकरण भेजने में त्रुटि हुई। कृपया इंटरनेट जांचें व पुनः प्रयास करें।")
            }
        }
    }

    fun cancelPendingRegistration() {
        repository.clearPendingRegistration()
        _pendingRegistrationRequestId.value = ""
        _pendingRegistrationCode.value = ""
        _pendingRegistrationName.value = ""
        _pendingRegistrationStatus.value = null
    }

    fun approveRegistration(request: RegistrationRequest) {
        viewModelScope.launch {
            val currentMembers = repository.allMembers.firstOrNull() ?: emptyList()
            val newId = FirestoreSyncHelper.approveRegistrationRequest(request, currentMembers)
            if (newId > 0L) {
                val cleanCode = if (request.memberCode.isNotBlank()) request.memberCode else (1000 + newId).toString()
                val newMember = MemberEntity(
                    id = newId,
                    name = request.name,
                    phone = request.phone,
                    monthlyAmount = request.monthlyAmount,
                    status = "Pending",
                    paidDate = "",
                    paymentMode = "UPI",
                    notes = "ऑनलाइन सत्यापित सदस्य",
                    sortOrder = newId.toInt(),
                    instagramId = request.instagramId,
                    whatsappNumber = request.phone,
                    villageCity = request.address.ifBlank { "कुचेरा" },
                    memberCode = cleanCode
                )
                repository.insertMember(newMember)
                repository.announceNewMemberApproved(newMember)
                _snackbarMessage.value = "✅ ${request.name} जी को सत्यापित कर सदस्य सूची में जोड़ा गया!"
                repository.syncWithFirestore()
            } else {
                _snackbarMessage.value = "❌ सत्यापन में त्रुटि हुई, कृपया पुनः प्रयास करें।"
            }
        }
    }

    fun rejectRegistration(request: RegistrationRequest, reason: String = "") {
        viewModelScope.launch {
            val ok = FirestoreSyncHelper.rejectRegistrationRequest(request.requestId, reason)
            if (ok) {
                _snackbarMessage.value = "ℹ️ ${request.name} का अनुरोध निरस्त कर दिया गया।"
            } else {
                _snackbarMessage.value = "❌ अस्वीकार करने में त्रुटि हुई।"
            }
        }
    }

    fun registerNewMemberWithCode(
        name: String,
        phone: String,
        address: String,
        memberCode: String,
        photoUri: String,
        insta: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanCode = memberCode.trim()
        val cleanPhone = phone.trim()
        val isAdmin = (cleanCode == "992942" || cleanPhone == "8696663615")
        if (isAdmin) {
            viewModelScope.launch {
                val newMember = MemberEntity(
                    name = name.trim(),
                    phone = cleanPhone,
                    monthlyAmount = 100,
                    status = "Pending",
                    notes = "मुख्य व्यवस्थापक",
                    instagramId = insta.trim(),
                    whatsappNumber = cleanPhone,
                    villageCity = address.trim().ifBlank { "कुचेरा" },
                    memberCode = cleanCode
                )
                val newId = repository.insertMember(newMember)
                repository.saveUserProfile(
                    name = name.trim(),
                    phone = cleanPhone,
                    address = address.trim(),
                    photoUri = photoUri.trim(),
                    insta = insta.trim(),
                    pin = "992942",
                    isAdmin = true,
                    memberCode = cleanCode
                )
                _isUserLoggedIn.value = true
                _isAdmin.value = true
                _currentUserFullName.value = name.trim()
                _currentUserPhone.value = cleanPhone
                _currentUserMemberCode.value = cleanCode
                _currentUserAddress.value = address.trim()
                _currentUserInstagram.value = insta.trim()
                _activeMemberId.value = newId
                repository.setMyActiveMemberId(newId)
                _showWelcomeGreetingDialog.value = true
                _snackbarMessage.value = "✅ व्यवस्थापक पंजीकरण सफल!"
                onResult(true, "✅ पंजीकरण सफल!")
            }
        } else {
            submitMemberRegistrationRequest(
                name = name,
                phone = phone,
                address = address,
                memberCode = memberCode,
                photoUri = photoUri,
                insta = insta,
                onResult = onResult
            )
        }
    }

    fun registerOrLoginUser(
        name: String,
        phone: String,
        address: String,
        photoUri: String,
        insta: String,
        isAdmin: Boolean
    ) {
        viewModelScope.launch {
            val cleanPhone = phone.trim()
            val fallbackCode = if (isAdmin || cleanPhone == "8696663615") "992942" else ""
            repository.saveUserProfile(
                name = name,
                phone = cleanPhone,
                address = address,
                photoUri = photoUri,
                insta = insta,
                pin = if (isAdmin) "992942" else "",
                isAdmin = isAdmin,
                memberCode = fallbackCode
            )
            _isUserLoggedIn.value = true
            _isAdmin.value = isAdmin
            _currentUserFullName.value = name
            _currentUserPhone.value = cleanPhone
            _currentUserAddress.value = address
            _currentUserProfilePhotoUri.value = photoUri
            _currentUserInstagram.value = insta
            if (fallbackCode.isNotBlank()) {
                _currentUserMemberCode.value = fallbackCode
            }

            // Link or add to members database
            try {
                val currentMembers = repository.allMembers.firstOrNull() ?: emptyList()
                val existing = currentMembers.find {
                    it.phone.trim().takeLast(10) == cleanPhone.takeLast(10)
                }
                if (existing == null) {
                    val newMember = MemberEntity(
                        name = name.trim(),
                        phone = cleanPhone,
                        monthlyAmount = 100,
                        status = "Pending",
                        notes = if (isAdmin) "मुख्य व्यवस्थापक" else "परिवार सदस्य",
                        instagramId = insta.trim(),
                        whatsappNumber = cleanPhone,
                        villageCity = address.trim().ifBlank { "कुचेरा" },
                        memberCode = fallbackCode
                    )
                    val newId = repository.insertMember(newMember)
                    _activeMemberId.value = newId
                    repository.setMyActiveMemberId(newId)
                    if (fallbackCode.isBlank()) {
                        val genCode = (1000 + newId).toString()
                        _currentUserMemberCode.value = genCode
                        repository.setUserMemberCode(genCode)
                    }
                } else {
                    _activeMemberId.value = existing.id
                    repository.setMyActiveMemberId(existing.id)
                    val existingCode = if (existing.memberCode.isNotBlank()) existing.memberCode else (1000 + existing.id).toString()
                    _currentUserMemberCode.value = existingCode
                    repository.setUserMemberCode(existingCode)
                    val updated = existing.copy(
                        name = name.trim().ifBlank { existing.name },
                        villageCity = address.trim().ifBlank { existing.villageCity },
                        instagramId = insta.trim().ifBlank { existing.instagramId },
                        notes = if (isAdmin) "मुख्य व्यवस्थापक" else existing.notes.ifBlank { "परिवार सदस्य" },
                        memberCode = existingCode
                    )
                    repository.updateMember(updated)
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error linking member during login", e)
            }

            _showWelcomeGreetingDialog.value = true
            _snackbarMessage.value = if (isAdmin) "स्वागत है, $name (एडमिन)! ऐप तैयार है।" else "स्वागत है, $name! युवा शक्ति ग्रुप में आपका स्वागत है।"
        }
    }

    fun changeAppLockPin(newPin: String) {
        val clean = newPin.trim()
        repository.setAppLockPin(clean)
        _snackbarMessage.value = "✅ नया ऐप लॉक पिन ($clean) सुरक्षित हो गया है!"
    }

    fun updateUserProfilePhoto(photoUri: String) {
        repository.setUserProfilePhotoUri(photoUri)
        _currentUserProfilePhotoUri.value = photoUri
        _snackbarMessage.value = "✅ आपकी प्रोफ़ाइल फोटो सुरक्षित हो गई!"
    }

    fun logoutUser() {
        repository.logoutUser()
        repository.setAdminMode(false)
        _isUserLoggedIn.value = false
        _isAdmin.value = false
        _currentUserMemberCode.value = ""
    }

    fun triggerManualSirenTgada(context: Context) {
        viewModelScope.launch {
            val pendingMembers = uiState.value.members.filter { it.status == "Pending" }
            val adminName = if (_isAdmin.value) "रेवंत राम जी (व्यवस्थापक)" else "मुख्य व्यवस्थापक"
            val title = "🚨 अमावस्या फंड बकाया सायरन तगादा!"
            val totalPendingAmt = if (pendingMembers.isNotEmpty()) pendingMembers.sumOf { it.monthlyAmount } else 100
            val msg = if (pendingMembers.isNotEmpty()) {
                "युवा शक्ति ग्रुप (भाटी परिवार कुचेरा): ${pendingMembers.size} सदस्यों का कुल ₹$totalPendingAmt मासिक अंशदान बाकी है। कृपया तुरंत PhonePe से जमा कराएं।"
            } else {
                "युवा शक्ति ग्रुप (भाटी परिवार कुचेरा): आगामी अमावस्या फंड अंशदान हेतु सायरन तगादा।"
            }

            // Local siren audio & notification
            com.example.util.SoundHelper.playSirenAlarm(context)
            com.example.util.NotificationHelper.triggerPendingSirenNotification(
                context = context,
                title = title,
                message = msg,
                memberName = if (pendingMembers.isNotEmpty()) "${pendingMembers.size} बकाया सदस्यों" else "सभी सदस्यों",
                amount = totalPendingAmt
            )

            // Realtime Broadcast across ALL installed phones via Firestore!
            com.example.util.FirestoreSyncHelper.broadcastEmergencyAlert(
                title = title,
                message = msg,
                adminName = adminName,
                isSiren = true,
                pendingMembers = pendingMembers
            )

            _snackbarMessage.value = "🚨 सभी सदस्यों के मोबाइल पर सायरन अलार्म व बकाया तगादा भेजा गया!"
        }
    }

    fun unlockAppBiometrically() {
        _isAppLocked.value = false
    }

    fun lockAppNow() {
        if (_isAppLockEnabled.value) {
            _isAppLocked.value = true
            _showSettingsDialog.value = false
            _snackbarMessage.value = "ऐप सुरक्षित रूप से लॉक कर दिया गया है।"
        } else {
            _snackbarMessage.value = "कृपया पहले सेटिंग्स से ऐप लॉक सक्रिय करें।"
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        _isAppLockEnabled.value = enabled
        repository.setAppLockEnabled(enabled)
        _isAppLocked.value = enabled
        _snackbarMessage.value = if (enabled) "🔒 ऐप लॉक सक्रिय किया गया" else "🔓 ऐप लॉक निष्क्रिय किया गया"
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
        repository.setBiometricEnabled(enabled)
        _snackbarMessage.value = if (enabled) "👆 फिंगरप्रिंट व फेस लॉक सक्रिय हुआ" else "बायोमेट्रिक लॉक बंद किया गया"
    }

    fun setAppLockPin(pin: String) {
        repository.setAppLockPin(pin)
        _showChangeAppLockPinDialog.value = false
        _snackbarMessage.value = "नया ऐप लॉक पिन सुरक्षित हुआ!"
    }

    fun openChangeAppLockPinDialog(show: Boolean) {
        _showChangeAppLockPinDialog.value = show
    }

    // Active presence & member selection
    fun setActiveMemberId(id: Long) {
        _activeMemberId.value = id
        repository.setMyActiveMemberId(id)
        _snackbarMessage.value = "सक्रिय प्रोफ़ाइल अपडेट हुई।"
    }

    fun openOnlineMembersDialog(show: Boolean) {
        _showOnlineMembersDialog.value = show
    }

    // App Update & Developer Code Console
    private val _showAppUpdateConsoleDialog = MutableStateFlow(false)
    val showAppUpdateConsoleDialog: StateFlow<Boolean> = _showAppUpdateConsoleDialog

    private val _updateApkUrl = MutableStateFlow(repository.getUpdateApkUrl())
    val updateApkUrl: StateFlow<String> = _updateApkUrl

    private val _latestVersionNote = MutableStateFlow(repository.getLatestVersionNote())
    val latestVersionNote: StateFlow<String> = _latestVersionNote

    fun openAppUpdateConsole(show: Boolean) {
        _showAppUpdateConsoleDialog.value = show
    }

    fun broadcastAppUpdate(version: String, notes: String, apkUrl: String) {
        viewModelScope.launch {
            repository.broadcastAppUpdate(version, notes, apkUrl)
            _updateApkUrl.value = apkUrl
            _latestVersionNote.value = notes
            _showAppUpdateConsoleDialog.value = false
            _snackbarMessage.value = "🚀 ऐप अपडेट सूचना ($version) सभी सदस्यों को भेजी गई!"
        }
    }

    fun executeCodePatch(command: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.executeMaintenancePatch(command)
            onResult(res)
            _snackbarMessage.value = "पैच निष्पादन पूर्ण हुआ।"
        }
    }
}

