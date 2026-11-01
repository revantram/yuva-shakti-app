package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.MemberEntity
import com.example.data.RegistrationRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FirestoreSyncHelper {
    private const val TAG = "FirestoreSync"
    private const val COLLECTION_MEMBERS = "members"
    private const val COLLECTION_CONTRIBUTIONS = "contributions"
    private const val COLLECTION_PRESENCE = "presence"
    private const val COLLECTION_SETTINGS = "settings"
    private const val COLLECTION_EMERGENCY_ALERTS = "emergency_alerts"
    private const val COLLECTION_REGISTRATIONS = "pending_registrations"
    private const val DOC_GENERAL = "general"

    val isConfigured: Boolean = true

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (t: Throwable) {
            Log.e(TAG, "FirebaseFirestore instance unavailable", t)
            null
        }
    }

    private suspend fun <T> Task<T>.awaitTask(): T =
        suspendCancellableCoroutine { cont ->
            addOnSuccessListener { result ->
                cont.resume(result)
            }
            addOnFailureListener { exception ->
                cont.resumeWith(Result.failure(exception))
            }
            addOnCanceledListener {
                cont.cancel()
            }
        }

    suspend fun uploadMembersToFirestore(members: List<MemberEntity>): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            val batch = fs.batch()
            for (member in members) {
                val docRef = fs.collection(COLLECTION_MEMBERS).document(member.id.toString())
                val cleanCode = if (member.id == 1L || member.phone == "8696663615") "992942" else if (member.memberCode.isNotBlank()) member.memberCode else (1000 + member.id).toString()
                val data = mapOf(
                    "id" to member.id,
                    "name" to member.name,
                    "phone" to member.phone,
                    "monthlyAmount" to member.monthlyAmount,
                    "status" to member.status,
                    "paidDate" to member.paidDate,
                    "paymentMode" to member.paymentMode,
                    "notes" to member.notes,
                    "sortOrder" to member.sortOrder,
                    "instagramId" to member.instagramId,
                    "facebookId" to member.facebookId,
                    "whatsappNumber" to member.whatsappNumber,
                    "villageCity" to member.villageCity,
                    "memberCode" to cleanCode,
                    "updatedAt" to System.currentTimeMillis()
                )
                batch.set(docRef, data, SetOptions.merge())
            }
            batch.commit().awaitTask()
            Log.d(TAG, "Uploaded ${members.size} members to Firestore successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading members to Firestore", e)
            false
        }
    }

    suspend fun saveSingleMember(member: MemberEntity): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            val docRef = fs.collection(COLLECTION_MEMBERS).document(member.id.toString())
            val cleanCode = if (member.id == 1L || member.phone == "8696663615") "992942" else if (member.memberCode.isNotBlank()) member.memberCode else (1000 + member.id).toString()
            val data = mapOf(
                "id" to member.id,
                "name" to member.name,
                "phone" to member.phone,
                "monthlyAmount" to member.monthlyAmount,
                "status" to member.status,
                "paidDate" to member.paidDate,
                "paymentMode" to member.paymentMode,
                "notes" to member.notes,
                "sortOrder" to member.sortOrder,
                "instagramId" to member.instagramId,
                "facebookId" to member.facebookId,
                "whatsappNumber" to member.whatsappNumber,
                "villageCity" to member.villageCity,
                "memberCode" to cleanCode,
                "updatedAt" to System.currentTimeMillis()
            )
            docRef.set(data, SetOptions.merge()).awaitTask()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving single member to Firestore", e)
            false
        }
    }

    suspend fun deleteMember(memberId: Long): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            fs.collection(COLLECTION_MEMBERS).document(memberId.toString()).delete().awaitTask()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting member $memberId from Firestore", e)
            false
        }
    }

    suspend fun fetchMembersFromFirestore(): List<MemberEntity> {
        return try {
            val fs = getFirestore() ?: return emptyList()
            val snapshot = fs.collection(COLLECTION_MEMBERS).get().awaitTask()
            val list = mutableListOf<MemberEntity>()
            for (doc in snapshot.documents) {
                val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: (doc.getString("id")?.toLongOrNull() ?: 0L)
                val name = doc.getString("name") ?: ""
                val phone = doc.getString("phone") ?: (doc.getLong("phone")?.toString() ?: "")
                val monthlyAmount = doc.getLong("monthlyAmount")?.toInt() ?: (doc.getString("monthlyAmount")?.toIntOrNull() ?: 0)
                val status = doc.getString("status") ?: "Pending"
                val paidDate = doc.getString("paidDate") ?: ""
                val paymentMode = doc.getString("paymentMode") ?: ""
                val notes = doc.getString("notes") ?: ""
                val sortOrder = doc.getLong("sortOrder")?.toInt() ?: (doc.getString("sortOrder")?.toIntOrNull() ?: 0)
                val instagramId = doc.getString("instagramId") ?: ""
                val facebookId = doc.getString("facebookId") ?: ""
                val whatsappNumber = doc.getString("whatsappNumber") ?: (doc.getLong("whatsappNumber")?.toString() ?: "")
                val villageCity = doc.getString("villageCity") ?: ""
                val memberCode = doc.getString("memberCode") ?: (if (id == 1L || phone == "8696663615") "992942" else (1000 + id).toString())

                if (name.isNotBlank()) {
                    list.add(
                        MemberEntity(
                            id = id,
                            name = name,
                            phone = phone,
                            monthlyAmount = monthlyAmount,
                            status = status,
                            paidDate = paidDate,
                            paymentMode = paymentMode,
                            notes = notes,
                            sortOrder = sortOrder,
                            instagramId = instagramId,
                            facebookId = facebookId,
                            whatsappNumber = whatsappNumber,
                            villageCity = villageCity,
                            memberCode = memberCode
                        )
                    )
                }
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching members from Firestore", e)
            emptyList()
        }
    }

    fun listenToMembersRealtime(onMembersUpdated: (List<MemberEntity>) -> Unit): ListenerRegistration? {
        return try {
            val fs = getFirestore() ?: return null
            fs.collection(COLLECTION_MEMBERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Realtime member listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = mutableListOf<MemberEntity>()
                        for (doc in snapshot.documents) {
                            val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: (doc.getString("id")?.toLongOrNull() ?: 0L)
                            val name = doc.getString("name") ?: ""
                            val phone = doc.getString("phone") ?: (doc.getLong("phone")?.toString() ?: "")
                            val monthlyAmount = doc.getLong("monthlyAmount")?.toInt() ?: (doc.getString("monthlyAmount")?.toIntOrNull() ?: 0)
                            val status = doc.getString("status") ?: "Pending"
                            val paidDate = doc.getString("paidDate") ?: ""
                            val paymentMode = doc.getString("paymentMode") ?: ""
                            val notes = doc.getString("notes") ?: ""
                            val sortOrder = doc.getLong("sortOrder")?.toInt() ?: (doc.getString("sortOrder")?.toIntOrNull() ?: 0)
                            val instagramId = doc.getString("instagramId") ?: ""
                            val facebookId = doc.getString("facebookId") ?: ""
                            val whatsappNumber = doc.getString("whatsappNumber") ?: (doc.getLong("whatsappNumber")?.toString() ?: "")
                            val villageCity = doc.getString("villageCity") ?: ""
                            val memberCode = doc.getString("memberCode") ?: (if (id == 1L || phone == "8696663615") "992942" else (1000 + id).toString())

                            if (name.isNotBlank()) {
                                list.add(
                                    MemberEntity(
                                        id = id,
                                        name = name,
                                        phone = phone,
                                        monthlyAmount = monthlyAmount,
                                        status = status,
                                        paidDate = paidDate,
                                        paymentMode = paymentMode,
                                        notes = notes,
                                        sortOrder = sortOrder,
                                        instagramId = instagramId,
                                        facebookId = facebookId,
                                        whatsappNumber = whatsappNumber,
                                        villageCity = villageCity,
                                        memberCode = memberCode
                                    )
                                )
                            }
                        }
                        if (list.isNotEmpty()) {
                            onMembersUpdated(list)
                        }
                    }
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Error setting up realtime listener", t)
            null
        }
    }

    data class OnlineUserDetail(
        val memberId: Long = 0L,
        val userName: String = "",
        val memberCode: String = "",
        val lastActive: Long = 0L
    )

    /**
     * Heartbeat Presence: Records that a device/member is actively using the app.
     */
    fun recordPresence(
        deviceId: String,
        userName: String,
        memberId: Long = 0L,
        memberCode: String = "",
        isOnline: Boolean = true,
        deviceModel: String = ""
    ) {
        if (deviceId.isBlank()) return
        try {
            val fs = getFirestore() ?: return
            val docRef = fs.collection(COLLECTION_PRESENCE).document(deviceId)
            val data = mutableMapOf<String, Any>(
                "deviceId" to deviceId,
                "userName" to userName.ifBlank { "परिवार सदस्य" },
                "memberId" to memberId,
                "memberCode" to memberCode,
                "isOnline" to isOnline,
                "lastActive" to System.currentTimeMillis()
            )
            if (deviceModel.isNotBlank()) {
                data["deviceModel"] = deviceModel
            }
            docRef.set(data, SetOptions.merge())
        } catch (t: Throwable) {
            Log.e(TAG, "Error recording presence", t)
        }
    }

    /**
     * Listens to live online presence in Firestore.
     * All devices that sent a heartbeat within the last 2 minutes (120,000 ms) and isOnline == true
     * are counted live online in real-time.
     */
    fun listenToOnlinePresence(
        onUpdate: (count: Int, onlineMemberIds: Set<Long>, activeUsers: List<String>, activeUserDetails: List<OnlineUserDetail>) -> Unit
    ): ListenerRegistration? {
        return try {
            val fs = getFirestore() ?: return null
            fs.collection(COLLECTION_PRESENCE)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Presence listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val cutoff = System.currentTimeMillis() - (120 * 1000L) // 2 minutes (120000 ms)
                        val activeUsers = mutableListOf<String>()
                        val onlineMemberIds = mutableSetOf<Long>()
                        val userDetails = mutableListOf<OnlineUserDetail>()
                        var liveDeviceCount = 0

                        for (doc in snapshot.documents) {
                            val lastActive = doc.getLong("lastActive") ?: 0L
                            val isOnlineDoc = doc.getBoolean("isOnline") ?: true
                            if (isOnlineDoc && lastActive >= cutoff) {
                                liveDeviceCount++
                                val mId = doc.getLong("memberId") ?: 0L
                                if (mId > 0L) {
                                    onlineMemberIds.add(mId)
                                }
                                val name = doc.getString("userName") ?: ""
                                val code = doc.getString("memberCode") ?: (if (mId > 0L) (1000 + mId).toString() else "")
                                if (name.isNotBlank()) {
                                    activeUsers.add(name)
                                    userDetails.add(OnlineUserDetail(mId, name, code, lastActive))
                                }
                            }
                        }
                        val onlineCount = maxOf(1, liveDeviceCount)
                        onUpdate(onlineCount, onlineMemberIds, activeUsers, userDetails)
                    }
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Error listening to presence", t)
            null
        }
    }

    data class EmergencyAlertData(
        val alertId: String = "",
        val title: String = "",
        val message: String = "",
        val sender: String = "",
        val isSiren: Boolean = true,
        val timestamp: Long = 0L,
        val pendingCount: Int = 0,
        val totalPendingAmount: Int = 0,
        val pendingMemberNames: List<String> = emptyList()
    )

    /**
     * Broadcasts an emergency siren alert or urgent notice to all installed devices in real time.
     */
    suspend fun broadcastEmergencyAlert(
        title: String,
        message: String,
        adminName: String = "मुख्य व्यवस्थापक",
        isSiren: Boolean = true,
        pendingMembers: List<MemberEntity> = emptyList()
    ): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            val alertId = "alert_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().take(8)
            val now = System.currentTimeMillis()
            val pendingCount = pendingMembers.size
            val totalPendingAmount = pendingMembers.sumOf { it.monthlyAmount }
            val pendingNames = pendingMembers.map { "${it.name} (₹${it.monthlyAmount})" }

            val data = mapOf(
                "alertId" to alertId,
                "title" to title,
                "message" to message,
                "sender" to adminName,
                "isSiren" to isSiren,
                "timestamp" to now,
                "pendingCount" to pendingCount,
                "totalPendingAmount" to totalPendingAmount,
                "pendingMemberNames" to pendingNames
            )

            // 1. Direct document for instantaneous 1-to-all snapshot push on all connected devices
            fs.collection(COLLECTION_EMERGENCY_ALERTS).document("current_live_alert")
                .set(data)
                .awaitTask()

            // 2. Also log to history collection
            fs.collection(COLLECTION_EMERGENCY_ALERTS).document(alertId)
                .set(data)
                .awaitTask()

            // 3. Dual-write to emergency_system for Web Dashboard siren compatibility
            try {
                val webSirenData = mapOf(
                    "active" to true,
                    "message" to message,
                    "timestamp" to now
                )
                fs.collection("emergency_system").document("current_alert")
                    .set(webSirenData, SetOptions.merge())

                // 4. Live alert banner notification for Web Dashboard
                val webAlertData = mapOf(
                    "message" to "$title: $message",
                    "timestamp" to now
                )
                fs.collection("live_alerts").document("latest_update")
                    .set(webAlertData, SetOptions.merge())
            } catch (tWeb: Throwable) {
                Log.w(TAG, "Web siren sync non-critical warning", tWeb)
            }

            Log.d(TAG, "Broadcasted emergency alert to Firestore: $title")
            true
        } catch (t: Throwable) {
            Log.e(TAG, "Error broadcasting emergency alert", t)
            false
        }
    }

    /**
     * Realtime listener on all devices for emergency alerts & siren alarms.
     * When triggered, rings the siren alarm and shows alert notification immediately.
     */
    fun listenToEmergencyAlerts(
        context: Context,
        onAlertReceived: (EmergencyAlertData) -> Unit
    ): ListenerRegistration? {
        return try {
            val fs = getFirestore() ?: return null
            val prefs = context.getSharedPreferences("yuva_shakti_alert_prefs", Context.MODE_PRIVATE)

            fs.collection(COLLECTION_EMERGENCY_ALERTS).document("current_live_alert")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Emergency alert listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val alertId = snapshot.getString("alertId") ?: ""
                        val timestamp = snapshot.getLong("timestamp") ?: 0L
                        val lastSeenId = prefs.getString("last_handled_alert_id", "") ?: ""

                        val isFirstAppLaunch = !prefs.contains("last_handled_alert_id")
                        if (isFirstAppLaunch) {
                            // On fresh install, register current alert as already handled so it doesn't ring retroactively
                            prefs.edit().putString("last_handled_alert_id", alertId).apply()
                            return@addSnapshotListener
                        }

                        // Robust check: valid alert ID, not previously handled, and within last 60 minutes
                        val isFresh = Math.abs(System.currentTimeMillis() - timestamp) < (60 * 60 * 1000L)
                        if (alertId.isNotBlank() && alertId != lastSeenId && isFresh) {
                            prefs.edit().putString("last_handled_alert_id", alertId).apply()
                            val title = snapshot.getString("title") ?: "🚨 आवश्यक सूचना"
                            val message = snapshot.getString("message") ?: ""
                            val sender = snapshot.getString("sender") ?: "मुख्य व्यवस्थापक"
                            val isSiren = snapshot.getBoolean("isSiren") ?: true
                            val pendingCount = snapshot.getLong("pendingCount")?.toInt() ?: 0
                            val totalPendingAmount = snapshot.getLong("totalPendingAmount")?.toInt() ?: 0
                            @Suppress("UNCHECKED_CAST")
                            val pendingMemberNames = (snapshot.get("pendingMemberNames") as? List<String>) ?: emptyList()

                            val alertData = EmergencyAlertData(
                                alertId = alertId,
                                title = title,
                                message = message,
                                sender = sender,
                                isSiren = isSiren,
                                timestamp = timestamp,
                                pendingCount = pendingCount,
                                totalPendingAmount = totalPendingAmount,
                                pendingMemberNames = pendingMemberNames
                            )
                            onAlertReceived(alertData)
                        }
                    }
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Error setting up emergency alert listener", t)
            null
        }
    }

    /**
     * Persists general settings online (UPI ID, WhatsApp link, Target Amount, Cycle Name).
     */
    suspend fun saveSettings(
        upiId: String? = null,
        whatsAppGroupLink: String? = null,
        amavasyaTarget: Int? = null,
        amavasyaCycleName: String? = null,
        targetAmavasyaMillis: Long? = null,
        latestApkUrl: String? = null,
        latestVersion: String? = null,
        updateNotes: String? = null
    ): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            val docRef = fs.collection(COLLECTION_SETTINGS).document(DOC_GENERAL)
            val data = mutableMapOf<String, Any>(
                "updatedAt" to System.currentTimeMillis()
            )
            upiId?.let { data["fundUpiId"] = it }
            whatsAppGroupLink?.let { data["whatsAppGroupLink"] = it }
            amavasyaTarget?.let { data["amavasyaTarget"] = it }
            amavasyaCycleName?.let { data["amavasyaCycleName"] = it }
            targetAmavasyaMillis?.let { data["targetAmavasyaMillis"] = it }
            latestApkUrl?.let { data["latestApkUrl"] = it }
            latestVersion?.let { data["latestVersion"] = it }
            updateNotes?.let { data["updateNotes"] = it }

            docRef.set(data, SetOptions.merge()).awaitTask()

            // Dual-write to app_settings/upcoming_amavasya for Web Dashboard reminder banner compatibility
            try {
                if (targetAmavasyaMillis != null && targetAmavasyaMillis > 0L) {
                    val dateFormatted = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Date(targetAmavasyaMillis))
                    val amavasData = mapOf(
                        "date" to dateFormatted,
                        "title" to (amavasyaCycleName ?: "मासिक अमावस बैठक"),
                        "message" to "सभी सदस्य समय पर बैठक में उपस्थित हों एवं अपना योगदान जमा कराएं।",
                        "updatedAt" to System.currentTimeMillis()
                    )
                    fs.collection("app_settings").document("upcoming_amavasya")
                        .set(amavasData, SetOptions.merge())
                }
            } catch (tWeb: Throwable) {
                Log.w(TAG, "Web upcoming amavasya sync warning", tWeb)
            }

            Log.d(TAG, "Saved settings to Firestore successfully")
            true
        } catch (t: Throwable) {
            Log.e(TAG, "Error saving settings to Firestore", t)
            false
        }
    }

    /**
     * Realtime listener for online settings so all members get instant updates without reinstalling.
     */
    fun listenToSettings(
        onSettings: (
            upiId: String?,
            whatsAppGroupLink: String?,
            amavasyaTarget: Int?,
            amavasyaCycleName: String?,
            targetAmavasyaMillis: Long?,
            latestApkUrl: String?,
            latestVersion: String?,
            updateNotes: String?
        ) -> Unit
    ): ListenerRegistration? {
        return try {
            val fs = getFirestore() ?: return null
            fs.collection(COLLECTION_SETTINGS).document(DOC_GENERAL)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Settings listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val upiId = snapshot.getString("fundUpiId")
                        val waLink = snapshot.getString("whatsAppGroupLink")
                        val target = snapshot.getLong("amavasyaTarget")?.toInt()
                        val cycleName = snapshot.getString("amavasyaCycleName")
                        val amavasyaMillis = snapshot.getLong("targetAmavasyaMillis")
                        val apkUrl = snapshot.getString("latestApkUrl")
                        val version = snapshot.getString("latestVersion")
                        val notes = snapshot.getString("updateNotes")
                        onSettings(upiId, waLink, target, cycleName, amavasyaMillis, apkUrl, version, notes)
                    }
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Error setting up settings listener", t)
            null
        }
    }

    /**
     * Submits a new member registration request directly to Firebase Firestore pending_registrations.
     */
    suspend fun submitRegistrationRequest(request: RegistrationRequest): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            val data = mapOf(
                "requestId" to request.requestId,
                "name" to request.name,
                "phone" to request.phone,
                "address" to request.address,
                "memberCode" to request.memberCode,
                "monthlyAmount" to request.monthlyAmount,
                "instagramId" to request.instagramId,
                "photoUri" to request.photoUri,
                "status" to "Pending",
                "timestamp" to request.timestamp,
                "deviceId" to request.deviceId,
                "assignedMemberId" to 0L
            )
            fs.collection(COLLECTION_REGISTRATIONS).document(request.requestId)
                .set(data, SetOptions.merge())
                .awaitTask()

            // Dual write to app_users for Web Dashboard compatibility
            try {
                val appUserData = mapOf(
                    "userCode" to request.memberCode,
                    "name" to request.name,
                    "phone" to request.phone,
                    "status" to "pending",
                    "role" to "member",
                    "createdAt" to FieldValue.serverTimestamp()
                )
                fs.collection("app_users").document(request.memberCode.ifBlank { request.requestId })
                    .set(appUserData, SetOptions.merge())
            } catch (tWeb: Throwable) {
                Log.w(TAG, "Web app_users sync warning", tWeb)
            }

            Log.d(TAG, "Registration request submitted: ${request.name} (Code: ${request.memberCode})")
            true
        } catch (t: Throwable) {
            Log.e(TAG, "Error submitting registration request", t)
            false
        }
    }

    /**
     * Listens to pending registration requests in real time for Admin verification.
     */
    fun listenToPendingRegistrations(
        onUpdate: (List<RegistrationRequest>) -> Unit
    ): ListenerRegistration? {
        return try {
            val fs = getFirestore() ?: return null
            fs.collection(COLLECTION_REGISTRATIONS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Pending registrations listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = mutableListOf<RegistrationRequest>()
                        for (doc in snapshot.documents) {
                            val status = doc.getString("status") ?: "Pending"
                            if (status.equals("Pending", ignoreCase = true)) {
                                val req = RegistrationRequest(
                                    requestId = doc.getString("requestId") ?: doc.id,
                                    name = doc.getString("name") ?: "",
                                    phone = doc.getString("phone") ?: "",
                                    address = doc.getString("address") ?: "",
                                    memberCode = doc.getString("memberCode") ?: "",
                                    monthlyAmount = doc.getLong("monthlyAmount")?.toInt() ?: 100,
                                    instagramId = doc.getString("instagramId") ?: "",
                                    photoUri = doc.getString("photoUri") ?: "",
                                    status = status,
                                    timestamp = doc.getLong("timestamp") ?: 0L,
                                    deviceId = doc.getString("deviceId") ?: "",
                                    assignedMemberId = doc.getLong("assignedMemberId") ?: 0L
                                )
                                if (req.name.isNotBlank()) {
                                    list.add(req)
                                }
                            }
                        }
                        list.sortByDescending { it.timestamp }
                        onUpdate(list)
                    }
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Error listening to pending registrations", t)
            null
        }
    }

    /**
     * Fetches a single registration request directly by its requestId.
     */
    suspend fun fetchSingleRegistrationRequest(requestId: String): RegistrationRequest? {
        if (requestId.isBlank()) return null
        return try {
            val fs = getFirestore() ?: return null
            val doc = fs.collection(COLLECTION_REGISTRATIONS).document(requestId).get().awaitTask()
            if (doc != null && doc.exists()) {
                RegistrationRequest(
                    requestId = doc.getString("requestId") ?: doc.id,
                    name = doc.getString("name") ?: "",
                    phone = doc.getString("phone") ?: "",
                    address = doc.getString("address") ?: "",
                    memberCode = doc.getString("memberCode") ?: "",
                    monthlyAmount = doc.getLong("monthlyAmount")?.toInt() ?: 100,
                    instagramId = doc.getString("instagramId") ?: "",
                    photoUri = doc.getString("photoUri") ?: "",
                    status = doc.getString("status") ?: "Pending",
                    timestamp = doc.getLong("timestamp") ?: 0L,
                    deviceId = doc.getString("deviceId") ?: "",
                    assignedMemberId = doc.getLong("assignedMemberId") ?: 0L,
                    rejectionReason = doc.getString("rejectionReason") ?: ""
                )
            } else null
        } catch (t: Throwable) {
            Log.e(TAG, "Error fetching single registration", t)
            null
        }
    }

    /**
     * Listens to a specific registration request for the applicant's device to notify them when verified.
     */
    fun listenToRegistrationStatus(
        requestId: String,
        onStatusUpdate: (RegistrationRequest) -> Unit
    ): ListenerRegistration? {
        if (requestId.isBlank()) return null
        return try {
            val fs = getFirestore() ?: return null
            fs.collection(COLLECTION_REGISTRATIONS).document(requestId)
                .addSnapshotListener { doc, error ->
                    if (error != null || doc == null || !doc.exists()) return@addSnapshotListener
                    val req = RegistrationRequest(
                        requestId = doc.getString("requestId") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        memberCode = doc.getString("memberCode") ?: "",
                        monthlyAmount = doc.getLong("monthlyAmount")?.toInt() ?: 100,
                        instagramId = doc.getString("instagramId") ?: "",
                        photoUri = doc.getString("photoUri") ?: "",
                        status = doc.getString("status") ?: "Pending",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        deviceId = doc.getString("deviceId") ?: "",
                        assignedMemberId = doc.getLong("assignedMemberId") ?: 0L,
                        rejectionReason = doc.getString("rejectionReason") ?: ""
                    )
                    onStatusUpdate(req)
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Error listening to single registration status", t)
            null
        }
    }

    /**
     * Admin approves a registration:
     * - Finds the next global member ID
     * - Saves member into Firestore 'members' collection
     * - Updates registration status to 'Approved'
     */
    suspend fun approveRegistrationRequest(
        request: RegistrationRequest,
        currentMembers: List<MemberEntity>
    ): Long {
        return try {
            val fs = getFirestore() ?: return 0L
            val nextId = (currentMembers.maxOfOrNull { it.id } ?: 0L) + 1L
            val cleanCode = if (request.memberCode.isNotBlank()) request.memberCode else (1000 + nextId).toString()

            val memberData = mapOf(
                "id" to nextId,
                "name" to request.name,
                "phone" to request.phone,
                "monthlyAmount" to request.monthlyAmount,
                "status" to "Pending",
                "paidDate" to "",
                "paymentMode" to "UPI",
                "notes" to "ऑनलाइन सत्यापित सदस्य",
                "sortOrder" to nextId.toInt(),
                "instagramId" to request.instagramId,
                "facebookId" to "",
                "whatsappNumber" to request.phone,
                "villageCity" to request.address.ifBlank { "कुचेरा" },
                "memberCode" to cleanCode,
                "photoUri" to request.photoUri,
                "updatedAt" to System.currentTimeMillis()
            )

            // 1. Add to members collection
            fs.collection(COLLECTION_MEMBERS).document(nextId.toString())
                .set(memberData, SetOptions.merge())
                .awaitTask()

            // 2. Mark registration as Approved
            fs.collection(COLLECTION_REGISTRATIONS).document(request.requestId)
                .set(
                    mapOf(
                        "status" to "Approved",
                        "assignedMemberId" to nextId,
                        "approvedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                )
                .awaitTask()

            // 3. Dual write to app_users and amavas_status for Web Dashboard
            try {
                fs.collection("app_users").document(cleanCode)
                    .set(mapOf("status" to "approved", "role" to "member", "updatedAt" to FieldValue.serverTimestamp()), SetOptions.merge())
                fs.collection("amavas_status").document(cleanCode)
                    .set(
                        mapOf(
                            "memberCode" to cleanCode,
                            "name" to request.name,
                            "amount" to request.monthlyAmount,
                            "status" to "unpaid",
                            "createdAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
            } catch (tWeb: Throwable) {
                Log.w(TAG, "Web approval sync warning", tWeb)
            }

            Log.d(TAG, "Successfully approved registration for ${request.name} with ID $nextId")
            nextId
        } catch (t: Throwable) {
            Log.e(TAG, "Error approving registration request", t)
            0L
        }
    }

    /**
     * Admin rejects a registration request.
     */
    suspend fun rejectRegistrationRequest(requestId: String, reason: String = ""): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            fs.collection(COLLECTION_REGISTRATIONS).document(requestId)
                .set(
                    mapOf(
                        "status" to "Rejected",
                        "rejectionReason" to reason.ifBlank { "व्यवस्थापक द्वारा अस्वीकृत" },
                        "rejectedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                )
                .awaitTask()

            try {
                val userKey = requestId.removePrefix("reg_")
                fs.collection("app_users").document(userKey)
                    .set(
                        mapOf(
                            "status" to "rejected",
                            "rejectionReason" to reason.ifBlank { "व्यवस्थापक द्वारा अस्वीकृत" },
                            "updatedAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
            } catch (tWeb: Throwable) {
                Log.w(TAG, "Web reject sync warning", tWeb)
            }

            true
        } catch (t: Throwable) {
            Log.e(TAG, "Error rejecting registration request", t)
            false
        }
    }

    data class ContributionData(
        val id: String = "",
        val name: String = "",
        val amount: Double = 0.0,
        val timestamp: Long = 0L
    )

    /**
     * Add a real-time contribution to Firestore "contributions" and "amavas_contributions" collections
     */
    suspend fun addContribution(name: String, amount: Double): Boolean {
        return try {
            val fs = getFirestore() ?: return false
            val data = mapOf(
                "name" to name,
                "amount" to amount,
                "timestamp" to FieldValue.serverTimestamp()
            )
            fs.collection(COLLECTION_CONTRIBUTIONS).add(data).awaitTask()

            // Dual write to amavas_contributions & live_alerts for Web Dashboard
            try {
                val webContrib = mapOf(
                    "memberCode" to "-",
                    "name" to name,
                    "amount" to amount,
                    "month" to "अमावस",
                    "date" to java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                    "createdAt" to FieldValue.serverTimestamp()
                )
                fs.collection("amavas_contributions").add(webContrib)

                fs.collection("live_alerts").document("latest_update").set(
                    mapOf(
                        "message" to "$name ने ₹${amount.toInt()} जमा किए!",
                        "timestamp" to System.currentTimeMillis()
                    )
                )
            } catch (tWeb: Throwable) {
                Log.w(TAG, "Web contrib sync warning", tWeb)
            }

            Log.d(TAG, "Contribution added successfully for $name: $amount")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding contribution to Firestore", e)
            false
        }
    }

    /**
     * Listen to real-time contributions from Firestore
     */
    fun listenToContributions(onUpdate: (List<ContributionData>, Double) -> Unit): ListenerRegistration? {
        val fs = getFirestore() ?: return null
        return try {
            fs.collection(COLLECTION_CONTRIBUTIONS)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error listening to contributions", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        var total = 0.0
                        val list = mutableListOf<ContributionData>()
                        for (doc in snapshot.documents) {
                            val name = doc.getString("name") ?: ""
                            val amount = doc.getDouble("amount") ?: (doc.getLong("amount")?.toDouble() ?: 0.0)
                            val ts = doc.getTimestamp("timestamp")?.toDate()?.time ?: (doc.getLong("timestamp") ?: 0L)
                            total += amount
                            list.add(ContributionData(id = doc.id, name = name, amount = amount, timestamp = ts))
                        }
                        onUpdate(list, total)
                    }
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to attach contributions listener", t)
            null
        }
    }
}
