package com.example.util

import com.example.data.MemberEntity
import java.util.concurrent.ConcurrentHashMap

object OnlinePresenceHelper {
    // In-memory registry of online member IDs synced via Firestore
    private val activeMemberIds = ConcurrentHashMap.newKeySet<Long>()
    private val activeDeviceNames = java.util.concurrent.CopyOnWriteArrayList<String>()
    @Volatile private var firestoreLiveCount: Int = 1

    // Local heartbeat timestamps (fallback)
    private val localHeartbeats = ConcurrentHashMap<Long, Long>()

    /**
     * Called whenever Firestore presence listener updates.
     */
    fun updateOnlinePresence(count: Int, memberIds: Set<Long>, deviceNames: List<String>) {
        firestoreLiveCount = maxOf(1, count)
        activeMemberIds.clear()
        activeMemberIds.addAll(memberIds)
        activeDeviceNames.clear()
        activeDeviceNames.addAll(deviceNames)
    }

    /**
     * Mark a member as actively online right now locally.
     */
    fun recordHeartbeat(memberId: Long) {
        if (memberId > 0) {
            localHeartbeats[memberId] = System.currentTimeMillis()
            activeMemberIds.add(memberId)
        }
    }

    /**
     * Determines whether a member is currently online across all devices.
     */
    fun isOnline(memberId: Long, activeMemberId: Long, totalMembers: Int): Boolean {
        if (memberId == activeMemberId && activeMemberId > 0L) return true
        if (activeMemberIds.contains(memberId)) return true

        val lastSeen = localHeartbeats[memberId]
        if (lastSeen != null) {
            val diffMinutes = (System.currentTimeMillis() - lastSeen) / (1000 * 60)
            if (diffMinutes <= 5) return true
        }
        return false
    }

    fun getOnlineCount(members: List<MemberEntity>, activeMemberId: Long): Int {
        val countFromMembers = members.count { isOnline(it.id, activeMemberId, members.size) }
        return maxOf(countFromMembers, firestoreLiveCount, if (activeMemberId > 0) 1 else 1)
    }

    fun getOnlineMembers(members: List<MemberEntity>, activeMemberId: Long): List<MemberEntity> {
        val list = members.filter { isOnline(it.id, activeMemberId, members.size) }
        return if (list.isNotEmpty()) list else members.take(1)
    }

    fun getActiveDeviceNames(): List<String> = activeDeviceNames.toList()

    fun getStatusLabel(memberId: Long, activeMemberId: Long, totalMembers: Int): String {
        return if (isOnline(memberId, activeMemberId, totalMembers)) {
            "🟢 अभी ऑनलाइन"
        } else {
            "⚪ ऑफ़लाइन"
        }
    }
}
