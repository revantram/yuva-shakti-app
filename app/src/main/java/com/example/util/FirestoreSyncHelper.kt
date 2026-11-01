package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.MemberEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FirestoreSyncHelper {
    private const val TAG = "FirestoreSync"
    private const val COLLECTION_MEMBERS = "members"
    private const val COLLECTION_PRESENCE = "presence"
    private const val COLLECTION_SETTINGS = "settings"
    private const val COLLECTION_EMERGENCY_ALERTS = "emergency_alerts"
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
                            villageCity = villageCity
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
                                        villageCity = villageCity
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

    /**
     * Heartbeat Presence: Records that a device/member is actively using the app.
     */
    fun recordPresence(deviceId: String, userName: String, memberId: Long = 0L) {
        if (deviceId.isBlank()) return
        try {
            val fs = getFirestore() ?: return
            val docRef = fs.collection(COLLECTION_PRESENCE).document(deviceId)
            val data = mapOf(
                "deviceId" to deviceId,
                "userName" to userName.ifBlank { "परिवार सदस्य" },
                "memberId" to memberId,
                "lastActive" to System.currentTimeMillis()
            )
            docRef.set(data, SetOptions.merge())
        } catch (t: Throwable) {
            Log.e(TAG, "Error recording presence", t)
        }
    }

    /**
     * Listens to live online presence in Firestore.
     * All devices that sent a heartbeat within the last 3 minutes are counted online in real-time.
     */
    fun listenToOnlinePresence(
        onUpdate: (count: Int, onlineMemberIds: Set<Long>, activeUsers: List<String>) -> Unit
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
                        val cutoff = System.currentTimeMillis() - (180 * 1000L) // 3 minutes
                        val activeUsers = mutableListOf<String>()
                        val onlineMemberIds = mutableSetOf<Long>()
                        var liveDeviceCount = 0

                        for (doc in snapshot.documents) {
                            val lastActive = doc.getLong("lastActive") ?: 0L
                            if (lastActive >= cutoff) {
                                liveDeviceCount++
                                val mId = doc.getLong("memberId") ?: 0L
                                if (mId > 0L) {
                                    onlineMemberIds.add(mId)
                                }
                                val name = doc.getString("userName") ?: ""
                                if (name.isNotBlank()) {
                                    activeUsers.add(name)
                                }
                            }
                        }
                        val onlineCount = maxOf(1, liveDeviceCount)
                        onUpdate(onlineCount, onlineMemberIds, activeUsers)
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
            val alertId = java.util.UUID.randomUUID().toString()
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

                        // Only fire if it's a new alert AND issued within the last 15 minutes
                        val isFresh = (System.currentTimeMillis() - timestamp) < (15 * 60 * 1000L)
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
}
