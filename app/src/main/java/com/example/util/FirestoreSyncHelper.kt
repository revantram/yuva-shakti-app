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
                        Log.e(TAG, "Realtime listener error", error)
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
}
