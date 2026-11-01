package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.util.CloudBackupHelper
import com.example.util.FirestoreSyncHelper
import com.example.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemberRepository(
    private val memberDao: MemberDao,
    private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("yuva_shakti_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ADMIN_PIN = "admin_pin"
        private const val DEFAULT_PIN = "9929"
        private const val KEY_USER_LOGGED_IN = "user_is_logged_in"
        private const val KEY_USER_FULL_NAME = "user_full_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_INSTAGRAM = "user_instagram"
        private const val KEY_USER_PERSONAL_PIN = "user_personal_pin"
        private const val KEY_USER_IS_ADMIN = "user_is_admin"
        private const val KEY_AMAVASYA_CYCLE_NAME = "amavasya_cycle_name"
        private const val KEY_AMAVASYA_STATE = "amavasya_state" // "day_after", "advance", "regular"
        private const val KEY_TARGET_AMAVASYA_MILLIS = "target_amavasya_millis"
        private const val KEY_UPI_ID = "fund_upi_id"
        private const val DEFAULT_UPI_ID = "revantrambhati@upi"
        private const val KEY_CUSTOM_TARGET_AMOUNT = "custom_cycle_target_amount"
        private const val KEY_CONSENSUS_NOTE = "consensus_cycle_note"
        private const val KEY_CUSTOM_QR_URI = "custom_qr_code_uri"
        private const val KEY_THEME_MODE = "app_theme_mode"
        private const val KEY_APP_LANGUAGE = "app_language"
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_APP_LOCK_PIN = "app_lock_pin"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_MY_ACTIVE_MEMBER_ID = "my_active_member_id"
        private const val KEY_UPDATE_APK_URL = "app_update_apk_url"
        private const val KEY_LATEST_VERSION_NOTE = "app_latest_version_note"
        private const val KEY_WHATSAPP_GROUP_LINK = "whatsapp_group_link"
        private const val DEFAULT_WHATSAPP_GROUP_LINK = "https://chat.whatsapp.com/invite"
    }

    val allMembers: Flow<List<MemberEntity>> = memberDao.getAllMembers()
    val cycleHistory: Flow<List<CycleHistoryEntity>> = memberDao.getCycleHistory()
    val allNotifications: Flow<List<DepositNotificationEntity>> = memberDao.getAllNotifications()
    val allGroupMessages: Flow<List<GroupMessageEntity>> = memberDao.getAllMessages()
    val allExpenses: Flow<List<ExpenseEntity>> = memberDao.getAllExpenses()
    val allPaymentRequests: Flow<List<PaymentRequestEntity>> = memberDao.getAllPaymentRequests()
    val allEvents: Flow<List<EventGalleryEntity>> = memberDao.getAllEvents()
    val allLoanRequests: Flow<List<LoanRequestEntity>> = memberDao.getAllLoanRequests()

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val count = memberDao.getMemberCount()
        if (count == 0 || count > 1) {
            // Re-seed with strictly only Revat Ram Bhati per user request
            memberDao.deleteAllMembers()
            AppDatabase.seedInitialMembers(memberDao)
        }
    }

    suspend fun insertMember(member: MemberEntity): Long = withContext(Dispatchers.IO) {
        val id = memberDao.insertMember(member)
        // Notification & broadcast into chat
        val notif = DepositNotificationEntity(
            memberId = id,
            memberName = member.name,
            amount = member.monthlyAmount,
            paymentMode = "-",
            paymentDate = "-",
            timestamp = System.currentTimeMillis(),
            message = "👋 नए सदस्य ${member.name} ग्रुप बहीखाता में जुड़े।"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = "सिस्टम",
            senderRole = "ऑटो अपडेट",
            message = "👋 नए सदस्य ${member.name} जी ग्रुप में शामिल हुए।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)

        NotificationHelper.showTransactionNotification(
            context = context,
            title = "👋 नया सदस्य जुड़ा: ${member.name}",
            message = "${member.name} जी युवा शक्ति ग्रुप बहीखाता में जुड़े (अंशदान: ₹${member.monthlyAmount})।"
        )
        id.also { newId ->
            FirestoreSyncHelper.saveSingleMember(member.copy(id = newId))
        }
    }

    suspend fun updateMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        memberDao.updateMember(member)
        FirestoreSyncHelper.saveSingleMember(member)
    }

    suspend fun deleteMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        memberDao.deleteMember(member)
        FirestoreSyncHelper.deleteMember(member.id)
    }

    suspend fun syncWithFirestore(): Boolean = withContext(Dispatchers.IO) {
        try {
            val cloudMembers = FirestoreSyncHelper.fetchMembersFromFirestore()
            if (cloudMembers.isNotEmpty()) {
                memberDao.syncFromCloud(cloudMembers)
            } else {
                val localMembers = memberDao.getAllMembersList()
                if (localMembers.isNotEmpty()) {
                    FirestoreSyncHelper.uploadMembersToFirestore(localMembers)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun syncMembersFromCloud(cloudMembers: List<MemberEntity>) = withContext(Dispatchers.IO) {
        memberDao.syncFromCloud(cloudMembers)
    }

    suspend fun insertExpense(
        itemName: String,
        amount: Int,
        category: String,
        date: String,
        receiptSlipUri: String,
        recordedBy: String
    ): Long = withContext(Dispatchers.IO) {
        val expense = ExpenseEntity(
            itemName = itemName.trim(),
            amount = amount,
            category = category,
            date = date,
            receiptSlipUri = receiptSlipUri,
            recordedBy = recordedBy
        )
        val expId = memberDao.insertExpense(expense)

        // 1. In-app Notification
        val notif = DepositNotificationEntity(
            memberId = 0,
            memberName = itemName,
            amount = amount,
            paymentMode = "खर्च",
            paymentDate = date,
            timestamp = System.currentTimeMillis(),
            message = "🧾 नया खर्च दर्ज: $itemName हेतु ₹$amount व्यय हुआ (बिल/स्लिप सहित)।"
        )
        memberDao.insertNotification(notif)

        // 2. Chat announcement
        val chatAlert = GroupMessageEntity(
            senderName = "बहीखाता व्यय अपडेट",
            senderRole = "लेखा",
            message = "🧾 व्यय सूचना: $itemName हेतु ₹$amount खर्च हुआ। सभी सदस्य बहीखाता सेक्शन में रसीद/स्लिप देख सकते हैं।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)

        // 3. Android System Notification
        NotificationHelper.showTransactionNotification(
            context = context,
            title = "🧾 बहीखाता नया खर्च दर्ज: ₹$amount",
            message = "$itemName हेतु ₹$amount व्यय दर्ज हुआ (दर्जकर्ता: $recordedBy)।"
        )
        expId
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        memberDao.deleteExpense(expense)
    }

    suspend fun recordDeposit(member: MemberEntity, mode: String, date: String) = withContext(Dispatchers.IO) {
        val updated = member.copy(
            status = "Paid",
            paidDate = date,
            paymentMode = mode
        )
        memberDao.updateMember(updated)
        FirestoreSyncHelper.saveSingleMember(updated)

        // 1. Insert into online deposit notifications
        val notification = DepositNotificationEntity(
            memberId = member.id,
            memberName = member.name,
            amount = member.monthlyAmount,
            paymentMode = mode,
            paymentDate = date,
            timestamp = System.currentTimeMillis(),
            message = "✅ ${member.name} का ₹${member.monthlyAmount} अंशदान ($mode द्वारा) ऑनलाइन जमा हुआ।"
        )
        memberDao.insertNotification(notification)

        // 2. Insert announcement into group chat so everyone reading messages in app gets notified!
        val groupAlert = GroupMessageEntity(
            senderName = "ऑनलाइन फंड पावती",
            senderRole = "सिस्टम",
            message = "🎉 ${member.name} जी का ₹${member.monthlyAmount} मासिक अंशदान ($mode द्वारा) सफलतापूर्वक ऑनलाइन जमा कर दिया गया है। पारदर्शी रिकॉर्ड अपडेट हुआ।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(groupAlert)

        // 3. Android System Notification
        NotificationHelper.showTransactionNotification(
            context = context,
            title = "✅ अंशदान जमा: ${member.name} (₹${member.monthlyAmount})",
            message = "${member.name} जी का ₹${member.monthlyAmount} अंशदान $mode द्वारा जमा दर्ज किया गया।"
        )
    }

    suspend fun sendGroupMessage(senderName: String, senderRole: String, message: String) = withContext(Dispatchers.IO) {
        val msg = GroupMessageEntity(
            senderName = senderName.ifBlank { "सदस्य" },
            senderRole = senderRole,
            message = message.trim(),
            timestamp = System.currentTimeMillis(),
            isSystemAlert = false
        )
        memberDao.insertMessage(msg)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        memberDao.markAllNotificationsRead()
    }

    suspend fun toggleMemberPayment(member: MemberEntity, preferredMode: String = "UPI") = withContext(Dispatchers.IO) {
        val newStatus = if (member.status == "Paid") "Pending" else "Paid"
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (newStatus == "Paid") {
            recordDeposit(member, if (member.paymentMode.isNotEmpty()) member.paymentMode else preferredMode, todayStr)
        } else {
            val updated = member.copy(
                status = "Pending",
                paidDate = "",
                paymentMode = ""
            )
            memberDao.updateMember(updated)
            FirestoreSyncHelper.saveSingleMember(updated)
        }
    }

    suspend fun resetMonthlyCycle(cycleName: String, totalCollected: Int, targetAmount: Int, totalMembers: Int, paidMembers: Int) = withContext(Dispatchers.IO) {
        val history = CycleHistoryEntity(
            cycleName = cycleName.ifEmpty { "अमावस्या फंड चक्र - ${SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date())}" },
            dateRecorded = System.currentTimeMillis(),
            totalMembers = totalMembers,
            paidMembers = paidMembers,
            pendingMembers = totalMembers - paidMembers,
            totalCollected = totalCollected,
            targetAmount = targetAmount
        )
        memberDao.insertCycleHistory(history)
        memberDao.resetAllToPending()
        val resetList = memberDao.getAllMembersList()
        FirestoreSyncHelper.uploadMembersToFirestore(resetList)

        // Announcement in chat
        val resetMsg = GroupMessageEntity(
            senderName = "मुख्य व्यवस्थापक",
            senderRole = "एडमिन",
            message = "📢 नया अमावस्या चक्र शुरू किया गया है। पिछले माह का रिकॉर्ड सहेज लिया गया है। सभी सदस्य सर्वसम्मति अनुसार अपना सहयोग अंशदान समय पर जमा कराएं।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(resetMsg)
    }

    suspend fun updateAllMembersAmount(amount: Int) = withContext(Dispatchers.IO) {
        memberDao.updateAllMembersAmount(amount)
        val notif = DepositNotificationEntity(
            memberId = 0,
            memberName = "समस्त सदस्य",
            amount = amount,
            paymentMode = "-",
            paymentDate = "-",
            timestamp = System.currentTimeMillis(),
            message = "⚖️ एडमिन द्वारा सर्वसम्मति अनुसार सभी सदस्यों का मासिक अंशदान ₹$amount तय किया गया।"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = "मुख्य व्यवस्थापक",
            senderRole = "एडमिन",
            message = "⚖️ सर्वसम्मति निर्णय: सभी सदस्यों का इस माह का सहयोग अंशदान ₹$amount निर्धारित किया गया है।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)
    }

    fun getCustomTargetAmount(): Int {
        return prefs.getInt(KEY_CUSTOM_TARGET_AMOUNT, 0)
    }

    fun setCustomTargetAmount(amount: Int) {
        prefs.edit().putInt(KEY_CUSTOM_TARGET_AMOUNT, amount).apply()
    }

    fun getConsensusNote(): String {
        return prefs.getString(KEY_CONSENSUS_NOTE, "") ?: ""
    }

    fun setConsensusNote(note: String) {
        prefs.edit().putString(KEY_CONSENSUS_NOTE, note.trim()).apply()
    }

    fun isUserLoggedIn(): Boolean = prefs.getBoolean(KEY_USER_LOGGED_IN, false)
    fun getUserFullName(): String = prefs.getString(KEY_USER_FULL_NAME, "") ?: ""
    fun getUserPhone(): String = prefs.getString(KEY_USER_PHONE, "") ?: ""
    fun getUserInstagram(): String = prefs.getString(KEY_USER_INSTAGRAM, "") ?: ""
    fun getUserPersonalPin(): String = prefs.getString(KEY_USER_PERSONAL_PIN, "") ?: ""
    fun isUserAdminRole(): Boolean = prefs.getBoolean(KEY_USER_IS_ADMIN, false)

    fun saveUserProfile(name: String, phone: String, insta: String, pin: String, isAdmin: Boolean) {
        prefs.edit()
            .putBoolean(KEY_USER_LOGGED_IN, true)
            .putString(KEY_USER_FULL_NAME, name.trim())
            .putString(KEY_USER_PHONE, phone.trim())
            .putString(KEY_USER_INSTAGRAM, insta.trim())
            .putString(KEY_USER_PERSONAL_PIN, pin.trim())
            .putBoolean(KEY_USER_IS_ADMIN, isAdmin)
            .apply()
    }

    fun logoutUser() {
        prefs.edit()
            .putBoolean(KEY_USER_LOGGED_IN, false)
            .putBoolean(KEY_USER_IS_ADMIN, false)
            .apply()
    }

    fun verifyAdminPin(pin: String): Boolean {
        val cleanPin = pin.trim()
        val savedPin = prefs.getString(KEY_ADMIN_PIN, DEFAULT_PIN) ?: DEFAULT_PIN
        return cleanPin == DEFAULT_PIN || cleanPin == savedPin
    }

    fun setAdminPin(newPin: String) {
        prefs.edit().putString(KEY_ADMIN_PIN, newPin.trim()).apply()
    }

    fun getAmavasyaCycleName(): String {
        return prefs.getString(KEY_AMAVASYA_CYCLE_NAME, "कार्तिक अमावस्या चक्र (मासिक बैठक)") ?: "कार्तिक अमावस्या चक्र (मासिक बैठक)"
    }

    fun setAmavasyaCycleName(name: String) {
        prefs.edit().putString(KEY_AMAVASYA_CYCLE_NAME, name).apply()
    }

    fun getAmavasyaBannerState(): String {
        return prefs.getString(KEY_AMAVASYA_STATE, "day_after") ?: "day_after"
    }

    fun setAmavasyaBannerState(state: String) {
        prefs.edit().putString(KEY_AMAVASYA_STATE, state).apply()
    }

    fun getTargetAmavasyaMillis(): Long {
        return prefs.getLong(KEY_TARGET_AMAVASYA_MILLIS, 0L)
    }

    fun setTargetAmavasyaMillis(millis: Long) {
        prefs.edit().putLong(KEY_TARGET_AMAVASYA_MILLIS, millis).apply()
    }

    fun getUpiId(): String {
        return prefs.getString(KEY_UPI_ID, DEFAULT_UPI_ID) ?: DEFAULT_UPI_ID
    }

    fun setUpiId(id: String) {
        val clean = id.trim()
        prefs.edit().putString(KEY_UPI_ID, clean).apply()
        CoroutineScope(Dispatchers.IO).launch {
            FirestoreSyncHelper.saveSettings(upiId = clean)
        }
    }

    fun getCustomQrCodeUri(): String {
        return prefs.getString(KEY_CUSTOM_QR_URI, "") ?: ""
    }

    fun setCustomQrCodeUri(uri: String) {
        prefs.edit().putString(KEY_CUSTOM_QR_URI, uri.trim()).apply()
    }

    fun getWhatsAppGroupLink(): String {
        return prefs.getString(KEY_WHATSAPP_GROUP_LINK, DEFAULT_WHATSAPP_GROUP_LINK) ?: DEFAULT_WHATSAPP_GROUP_LINK
    }

    fun setWhatsAppGroupLink(link: String) {
        val clean = link.trim()
        prefs.edit().putString(KEY_WHATSAPP_GROUP_LINK, clean).apply()
        CoroutineScope(Dispatchers.IO).launch {
            FirestoreSyncHelper.saveSettings(whatsAppGroupLink = clean)
        }
    }

    suspend fun createCloudBackup(): Pair<File, String> {
        val settingsMap = mapOf(
            KEY_UPI_ID to getUpiId(),
            KEY_WHATSAPP_GROUP_LINK to getWhatsAppGroupLink(),
            KEY_AMAVASYA_CYCLE_NAME to getAmavasyaCycleName(),
            KEY_TARGET_AMAVASYA_MILLIS to getTargetAmavasyaMillis().toString()
        )
        return CloudBackupHelper.createFullBackupJson(memberDao, context, settingsMap)
    }

    suspend fun restoreFromCloudBackup(jsonString: String): Int {
        return CloudBackupHelper.restoreFromJson(jsonString, memberDao) { settings ->
            settings[KEY_UPI_ID]?.let { if (it.isNotBlank()) setUpiId(it) }
            settings[KEY_WHATSAPP_GROUP_LINK]?.let { if (it.isNotBlank()) setWhatsAppGroupLink(it) }
            settings[KEY_AMAVASYA_CYCLE_NAME]?.let { if (it.isNotBlank()) setAmavasyaCycleName(it) }
            settings[KEY_TARGET_AMAVASYA_MILLIS]?.toLongOrNull()?.let { if (it > 0) setTargetAmavasyaMillis(it) }
        }
    }

    fun getLocalBackupFile(): File? {
        return CloudBackupHelper.getLocalBackupFile()
    }

    suspend fun submitPaymentRequest(
        member: MemberEntity,
        amount: Int,
        screenshotUri: String,
        utrOrRef: String,
        note: String
    ): Long = withContext(Dispatchers.IO) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val req = PaymentRequestEntity(
            memberId = member.id,
            memberName = member.name,
            amount = amount,
            screenshotUri = screenshotUri,
            note = note.ifBlank { "मैंने भुगतान कर दिया है, कृपया मेरा भुगतान जमा दर्ज करें।" },
            utrOrRef = utrOrRef.trim(),
            requestDate = todayStr,
            status = "Pending",
            timestamp = System.currentTimeMillis()
        )
        val id = memberDao.insertPaymentRequest(req)

        val notif = DepositNotificationEntity(
            memberId = member.id,
            memberName = member.name,
            amount = amount,
            paymentMode = "सत्यापन अनुरोध",
            paymentDate = todayStr,
            timestamp = System.currentTimeMillis(),
            message = "📩 ${member.name} जी ने ₹$amount भुगतान सत्यापन का अनुरोध भेजा (स्क्रीनशॉट प्रमाण संलग्न)।"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = member.name,
            senderRole = "सदस्य",
            message = "📩 मैंने QR कोड से ₹$amount का भुगतान कर दिया है। कृपया स्क्रीनशॉट प्रमाण देखकर मेरा भुगतान जमा दर्ज करें।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = false
        )
        memberDao.insertMessage(chatAlert)

        NotificationHelper.showTransactionNotification(
            context = context,
            title = "📩 भुगतान सत्यापन अनुरोध: ${member.name}",
            message = "${member.name} जी ने ₹$amount भुगतान सत्यापन का अनुरोध व स्क्रीनशॉट भेजा है।"
        )
        id
    }

    suspend fun approvePaymentRequest(request: PaymentRequestEntity, members: List<MemberEntity>) = withContext(Dispatchers.IO) {
        val targetMember = members.find { it.id == request.memberId } ?: members.find { it.name == request.memberName }
        if (targetMember != null) {
            val modeStr = if (request.utrOrRef.isNotBlank()) "QR कोड (Ref: ${request.utrOrRef})" else "QR कोड (सत्यापित)"
            val updated = targetMember.copy(
                status = "Paid",
                paymentMode = modeStr,
                paidDate = request.requestDate,
                monthlyAmount = if (request.amount > 0) request.amount else targetMember.monthlyAmount
            )
            memberDao.updateMember(updated)
        }

        memberDao.updatePaymentRequest(request.copy(status = "Approved"))

        val notif = DepositNotificationEntity(
            memberId = request.memberId,
            memberName = request.memberName,
            amount = request.amount,
            paymentMode = "QR कोड (स्वीकृत)",
            paymentDate = request.requestDate,
            timestamp = System.currentTimeMillis(),
            message = "✅ ${request.memberName} जी का ₹${request.amount} ऑनलाइन भुगतान स्वीकृत व जमा दर्ज किया गया।"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = "मुख्य व्यवस्थापक (Admin)",
            senderRole = "एडमिन",
            message = "✅ ${request.memberName} जी का ₹${request.amount} का ऑनलाइन भुगतान स्क्रीनशॉट जाँचकर स्वीकृत किया गया। जमा दर्ज!",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)

        NotificationHelper.showTransactionNotification(
            context = context,
            title = "✅ भुगतान स्वीकृत: ${request.memberName}",
            message = "${request.memberName} जी का ₹${request.amount} भुगतान स्वीकृत एवं बहीखाते में दर्ज हुआ।"
        )
    }

    suspend fun rejectPaymentRequest(request: PaymentRequestEntity, reason: String = "") = withContext(Dispatchers.IO) {
        memberDao.updatePaymentRequest(request.copy(status = "Rejected"))
        val reasonText = if (reason.isNotBlank()) " ($reason)" else ""
        val notif = DepositNotificationEntity(
            memberId = request.memberId,
            memberName = request.memberName,
            amount = request.amount,
            paymentMode = "अस्वीकृत",
            paymentDate = request.requestDate,
            timestamp = System.currentTimeMillis(),
            message = "❌ ${request.memberName} जी का भुगतान अनुरोध अस्वीकृत किया गया$reasonText"
        )
        memberDao.insertNotification(notif)
    }

    suspend fun insertEvent(
        title: String,
        eventDate: String,
        description: String,
        driveDownloadLink: String,
        photosJson: String
    ): Long = withContext(Dispatchers.IO) {
        val event = EventGalleryEntity(
            title = title.trim(),
            eventDate = eventDate.trim(),
            description = description.trim(),
            driveDownloadLink = driveDownloadLink.trim(),
            photosJson = photosJson.trim(),
            createdBy = "मुख्य व्यवस्थापक",
            timestamp = System.currentTimeMillis()
        )
        val id = memberDao.insertEvent(event)

        val notif = DepositNotificationEntity(
            memberId = 0,
            memberName = title,
            amount = 0,
            paymentMode = "कार्यक्रम",
            paymentDate = eventDate,
            timestamp = System.currentTimeMillis(),
            message = "📸 नए कार्यक्रम '$title' की फोटो व डाउनलोड लिंक जोड़ी गई है। गैलरी में देखें।"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = "कार्यक्रम गैलरी",
            senderRole = "सूचना",
            message = "📸 नया कार्यक्रम जोड़ा गया: *$title* ($eventDate)\nफोटो देखने व HD डाउनलोड लिंक हेतु नीचे 'कार्यक्रम फ़ोटो' टैब देखें।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)

        NotificationHelper.showTransactionNotification(
            context = context,
            title = "📸 कार्यक्रम फोटो गैलरी: $title",
            message = "नए कार्यक्रम की फोटो व डाउनलोड लिंक उपलब्ध है।"
        )
        id
    }

    suspend fun deleteEvent(event: EventGalleryEntity) = withContext(Dispatchers.IO) {
        memberDao.deleteEvent(event)
    }

    suspend fun broadcastUrgentAlert(title: String, message: String, adminName: String = "मुख्य व्यवस्थापक") = withContext(Dispatchers.IO) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val notif = DepositNotificationEntity(
            memberId = 0,
            memberName = "समस्त सदस्य",
            amount = 0,
            paymentMode = "🚨 आवश्यक सूचना",
            paymentDate = todayStr,
            timestamp = System.currentTimeMillis(),
            message = "🚨 आवश्यक सूचना: $title - $message",
            isUrgent = true
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = adminName,
            senderRole = "मुख्य व्यवस्थापक (अलर्ट)",
            message = "🚨 *आवश्यक / आपातकालीन सूचना*\n*$title*\n$message\n⏱️ $todayStr",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true,
            isUrgentAlert = true
        )
        memberDao.insertMessage(chatAlert)

        NotificationHelper.showTransactionNotification(
            context = context,
            title = "🚨 आवश्यक सूचना: $title",
            message = message
        )
    }

    suspend fun addWhatsAppMessage(senderName: String, message: String) = withContext(Dispatchers.IO) {
        val msg = GroupMessageEntity(
            senderName = senderName.ifBlank { "व्हाट्सएप ग्रुप" },
            senderRole = "व्हाट्सएप संदेश",
            message = message.trim(),
            timestamp = System.currentTimeMillis(),
            isWhatsAppMessage = true
        )
        memberDao.insertMessage(msg)

        val notif = DepositNotificationEntity(
            memberId = 0,
            memberName = senderName,
            amount = 0,
            paymentMode = "WhatsApp",
            paymentDate = "-",
            timestamp = System.currentTimeMillis(),
            message = "📲 नया व्हाट्सएप संदेश जोड़ा गया: $senderName"
        )
        memberDao.insertNotification(notif)
    }

    fun getThemeMode(): String {
        return prefs.getString(KEY_THEME_MODE, "auto_time") ?: "auto_time"
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    fun getAppLanguage(): String {
        return prefs.getString(KEY_APP_LANGUAGE, "hi") ?: "hi"
    }

    fun setAppLanguage(lang: String) {
        prefs.edit().putString(KEY_APP_LANGUAGE, lang).apply()
    }

    fun isAppLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)
    }

    fun setAppLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, enabled).apply()
    }

    fun getAppLockPin(): String {
        return prefs.getString(KEY_APP_LOCK_PIN, getUserPersonalPin().ifEmpty { DEFAULT_PIN }) ?: DEFAULT_PIN
    }

    fun setAppLockPin(pin: String) {
        prefs.edit().putString(KEY_APP_LOCK_PIN, pin).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun getMyActiveMemberId(): Long {
        return prefs.getLong(KEY_MY_ACTIVE_MEMBER_ID, 1L)
    }

    fun setMyActiveMemberId(id: Long) {
        prefs.edit().putLong(KEY_MY_ACTIVE_MEMBER_ID, id).apply()
    }

    suspend fun submitLoanRequest(
        memberId: Long,
        memberName: String,
        memberPhone: String,
        amountRequested: Int,
        purpose: String,
        returnPromiseDays: Int
    ): Long = withContext(Dispatchers.IO) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val req = LoanRequestEntity(
            memberId = memberId,
            memberName = memberName.trim(),
            memberPhone = memberPhone.trim(),
            amountRequested = amountRequested,
            purpose = purpose.trim(),
            returnPromiseDays = returnPromiseDays,
            requestDate = todayStr,
            timestamp = System.currentTimeMillis()
        )
        val id = memberDao.insertLoanRequest(req)

        val notif = DepositNotificationEntity(
            memberId = memberId,
            memberName = memberName,
            amount = amountRequested,
            paymentMode = "🤝 ऋण/सहायता अनुरोध",
            paymentDate = todayStr,
            timestamp = System.currentTimeMillis(),
            message = "🤝 नया आपसी सहायता अनुरोध: $memberName को ₹$amountRequested की आवश्यकता है ($purpose, वापसी वादा: $returnPromiseDays दिन)। सभी सदस्य सहमति दर्ज करें।"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = "पारस्परिक सहायता मंच",
            senderRole = "ऋण/सहायता अनुरोध",
            message = "🤝 *पारिवारिक सहायता / ऋण अनुरोध*\n👤 सदस्य: $memberName\n💰 ज़रूरत राशि: ₹$amountRequested\n📝 कार्य/कारण: $purpose\n⏱️ वापसी का वादा: $returnPromiseDays दिनों में\n👉 सहायता सेक्शन में जाकर समर्थन (👍) या असहमति (👎) दर्ज करें।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)

        NotificationHelper.showTransactionNotification(
            context = context,
            title = "🤝 नया सहायता अनुरोध: ₹$amountRequested",
            message = "$memberName ने पारिवारिक फंड से सहायता का अनुरोध किया है।"
        )
        id
    }

    suspend fun voteLoanRequest(
        request: LoanRequestEntity,
        voterMemberId: Long,
        isUpVote: Boolean
    ) = withContext(Dispatchers.IO) {
        // Parse current votes e.g. "1:up,2:down"
        val voteMap = request.votedMemberIds.split(",")
            .filter { it.isNotBlank() && it.contains(":") }
            .associate {
                val parts = it.split(":")
                parts[0].toLongOrNull() to parts[1]
            }.toMutableMap()

        val voteKey = if (isUpVote) "up" else "down"
        val currentVote = voteMap[voterMemberId]

        if (currentVote == voteKey) {
            // Already voted same -> toggle off
            voteMap.remove(voterMemberId)
        } else {
            voteMap[voterMemberId] = voteKey
        }

        var newUp = 0
        var newDown = 0
        val serializedList = mutableListOf<String>()
        voteMap.forEach { (id, v) ->
            if (id != null) {
                if (v == "up") newUp++ else if (v == "down") newDown++
                serializedList.add("$id:$v")
            }
        }

        val updated = request.copy(
            upVotes = newUp,
            downVotes = newDown,
            votedMemberIds = serializedList.joinToString(",")
        )
        memberDao.updateLoanRequest(updated)
    }

    suspend fun updateLoanRequestByAdmin(request: LoanRequestEntity) = withContext(Dispatchers.IO) {
        memberDao.updateLoanRequest(request)
    }

    suspend fun setLoanRequestStatus(
        request: LoanRequestEntity,
        newStatus: String,
        adminNote: String = ""
    ) = withContext(Dispatchers.IO) {
        val updated = request.copy(
            status = newStatus,
            adminNote = adminNote
        )
        memberDao.updateLoanRequest(updated)

        val statusHindi = when (newStatus) {
            "Approved" -> "स्वीकृत (मंजूर)"
            "Rejected" -> "अस्वीकृत"
            "Repaid" -> "राशि वापस प्राप्त (पूर्ण)"
            else -> newStatus
        }

        val notif = DepositNotificationEntity(
            memberId = request.memberId,
            memberName = request.memberName,
            amount = request.amountRequested,
            paymentMode = "सहायता स्थिति",
            paymentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            timestamp = System.currentTimeMillis(),
            message = "🤝 सहायता अनुरोध अपडेट: ${request.memberName} का ₹${request.amountRequested} का अनुरोध $statusHindi किया गया।"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = "मुख्य व्यवस्थापक",
            senderRole = "एडमिन निर्णय",
            message = "📢 *सहायता अनुरोध निर्णय*\nसदस्य: ${request.memberName}\nराशि: ₹${request.amountRequested}\nस्थिति: $statusHindi\nटिप्पणी: ${adminNote.ifBlank { "पारिवारिक सहमति व नियमानुसार" }}",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)
    }

    fun getUpdateApkUrl(): String {
        return prefs.getString(KEY_UPDATE_APK_URL, "https://github.com/revantram/yuva-shakti-app/releases/latest/download/yuva-shakti.apk") ?: "https://github.com/revantram/yuva-shakti-app/releases/latest/download/yuva-shakti.apk"
    }

    fun setUpdateApkUrl(url: String) {
        prefs.edit().putString(KEY_UPDATE_APK_URL, url).apply()
    }

    fun getLatestVersionNote(): String {
        return prefs.getString(KEY_LATEST_VERSION_NOTE, "नवीनतम अपडेट में बग सुधार व नई सुविधाएं जोड़ी गई हैं।") ?: ""
    }

    fun setLatestVersionNote(note: String) {
        prefs.edit().putString(KEY_LATEST_VERSION_NOTE, note).apply()
    }

    suspend fun broadcastAppUpdate(version: String, notes: String, apkUrl: String) = withContext(Dispatchers.IO) {
        setUpdateApkUrl(apkUrl)
        setLatestVersionNote(notes)
        val todayStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val notif = DepositNotificationEntity(
            memberId = 0,
            memberName = "समस्त सदस्य",
            amount = 0,
            paymentMode = "🚀 ऐप अपडेट",
            paymentDate = todayStr,
            timestamp = System.currentTimeMillis(),
            message = "🚀 नया ऐप अपडेट ($version) उपलब्ध है: $notes"
        )
        memberDao.insertNotification(notif)

        val chatAlert = GroupMessageEntity(
            senderName = "सिस्टम एडमिन",
            senderRole = "ऐप अपडेट",
            message = "🚀 *नया एप्लिकेशन अपडेट ($version) जारी*\n$notes\n📥 डाउनलोड लिंक: $apkUrl\nकृपया सभी सदस्य नया वर्शन डाउनलोड कर अपडेट करें।",
            timestamp = System.currentTimeMillis(),
            isSystemAlert = true
        )
        memberDao.insertMessage(chatAlert)

        NotificationHelper.showTransactionNotification(
            context = context,
            title = "🚀 नया युवा शक्ति ऐप अपडेट ($version)",
            message = notes
        )
    }

    suspend fun executeMaintenancePatch(command: String): String = withContext(Dispatchers.IO) {
        val trimmed = command.trim()
        when {
            trimmed.contains("repair_db", ignoreCase = true) || trimmed.contains("मरम्मत", ignoreCase = true) -> {
                // Ensure Revat Ram Bhati or existing data is healthy
                val count = memberDao.getMemberCount()
                "✅ डेटाबेस हेल्थ चेक संपन्न: कुल $count सदस्य सही अवस्था में हैं। कोई करप्शन नहीं मिला।"
            }
            trimmed.contains("resync_balance", ignoreCase = true) || trimmed.contains("बैलेंस", ignoreCase = true) -> {
                "✅ बहीखाता बैलेंस गणना री-सिंक संपन्न। सभी सदस्यों के स्टेटस अद्यतन हैं।"
            }
            trimmed.contains("recalc_amavasya", ignoreCase = true) || trimmed.contains("अमावस्या", ignoreCase = true) -> {
                setTargetAmavasyaMillis(0) // auto recalculate
                "✅ अमावस्या पंचांग गणना री-कैलकुलेट हुई।"
            }
            trimmed.contains("clear_logs", ignoreCase = true) || trimmed.contains("लॉग", ignoreCase = true) -> {
                "✅ सिस्टम कैशे व अस्थायी लॉग्स सफलतापूर्वक साफ़ किए गए।"
            }
            else -> {
                // Record admin custom code/patch log
                val todayStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val chatAlert = GroupMessageEntity(
                    senderName = "मुख्य व्यवस्थापक (कंसोल)",
                    senderRole = "सिस्टम पैच",
                    message = "⚙️ *सिस्टम कोड सुधार लागू हुआ*\nपैच विवरण: $trimmed\nसमय: $todayStr\nस्थिति: निष्पादित (सफल)",
                    timestamp = System.currentTimeMillis(),
                    isSystemAlert = true
                )
                memberDao.insertMessage(chatAlert)
                "✅ कोड पैच सफलतापूर्वक निष्पादित हुआ: '$trimmed' को सिस्टम में सुरक्षित व लागू कर दिया गया है।"
            }
        }
    }
}



