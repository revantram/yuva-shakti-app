package com.example.util

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
    private const val DOC_GENERAL = "general"

    val isConfigured: Boolean = true

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
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
            val batch = firestore.batch()
            for (member in members) {
                val docRef = firestore.collection(COLLECTION_MEMBERS).document(member.id.toString())
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
            val docRef = firestore.collection(COLLECTION_MEMBERS).document(member.id.toString())
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
            firestore.collection(COLLECTION_MEMBERS).document(memberId.toString()).delete().awaitTask()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting member $memberId from Firestore", e)
            false
        }
    }

    suspend fun fetchMembersFromFirestore(): List<MemberEntity> {
        return try {
            val snapshot = firestore.collection(COLLECTION_MEMBERS).get().awaitTask()
            val list = mutableListOf<MemberEntity>()
            for (doc in snapshot.documents) {
                val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
                val name = doc.getString("name") ?: ""
                val phone = doc.getString("phone") ?: ""
                val monthlyAmount = doc.getLong("monthlyAmount")?.toInt() ?: 0
                val status = doc.getString("status") ?: "Pending"
                val paidDate = doc.getString("paidDate") ?: ""
                val paymentMode = doc.getString("paymentMode") ?: ""
                val notes = doc.getString("notes") ?: ""
                val sortOrder = doc.getLong("sortOrder")?.toInt() ?: 0
                val instagramId = doc.getString("instagramId") ?: ""
                val facebookId = doc.getString("facebookId") ?: ""
                val whatsappNumber = doc.getString("whatsappNumber") ?: ""
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
            firestore.collection(COLLECTION_MEMBERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Realtime member listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = mutableListOf<MemberEntity>()
                        for (doc in snapshot.documents) {
                            val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
                            val name = doc.getString("name") ?: ""
                            val phone = doc.getString("phone") ?: ""
                            val monthlyAmount = doc.getLong("monthlyAmount")?.toInt() ?: 0
                            val status = doc.getString("status") ?: "Pending"
                            val paidDate = doc.getString("paidDate") ?: ""
                            val paymentMode = doc.getString("paymentMode") ?: ""
                            val notes = doc.getString("notes") ?: ""
                            val sortOrder = doc.getLong("sortOrder")?.toInt() ?: 0
                            val instagramId = doc.getString("instagramId") ?: ""
                            val facebookId = doc.getString("facebookId") ?: ""
                            val whatsappNumber = doc.getString("whatsappNumber") ?: ""
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
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up realtime listener", e)
            null
        }
    }

    /**
     * Heartbeat Presence: Records that a device/member is actively using the app.
     */
    fun recordPresence(deviceId: String, userName: String, memberId: Long = 0L) {
        if (deviceId.isBlank()) return
        try {
            val docRef = firestore.collection(COLLECTION_PRESENCE).document(deviceId)
            val data = mapOf(
                "deviceId" to deviceId,
                "userName" to userName.ifBlank { "परिवार सदस्य" },
                "memberId" to memberId,
                "lastActive" to System.currentTimeMillis()
            )
            docRef.set(data, SetOptions.merge())
        } catch (e: Exception) {
            Log.e(TAG, "Error recording presence", e)
        }
    }

    /**
     * Listens to live online presence in Firestore.
     * Members who sent heartbeat in the last 2.5 minutes are considered online.
     */
    fun listenToOnlinePresence(onUpdate: (count: Int, activeUsers: List<String>) -> Unit): ListenerRegistration? {
        return try {
            firestore.collection(COLLECTION_PRESENCE)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Presence listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val cutoff = System.currentTimeMillis() - (150 * 1000L) // 2.5 minutes
                        val activeUsers = mutableListOf<String>()
                        for (doc in snapshot.documents) {
                            val lastActive = doc.getLong("lastActive") ?: 0L
                            if (lastActive >= cutoff) {
                                val name = doc.getString("userName") ?: "सदस्य"
                                if (name.isNotBlank() && !activeUsers.contains(name)) {
                                    activeUsers.add(name)
                                }
                            }
                        }
                        val onlineCount = if (activeUsers.size > 0) activeUsers.size else 1
                        onUpdate(onlineCount, activeUsers)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error listening to presence", e)
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
            val docRef = firestore.collection(COLLECTION_SETTINGS).document(DOC_GENERAL)
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
        } catch (e: Exception) {
            Log.e(TAG, "Error saving settings to Firestore", e)
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
            firestore.collection(COLLECTION_SETTINGS).document(DOC_GENERAL)
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
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up settings listener", e)
            null
        }
    }
}
