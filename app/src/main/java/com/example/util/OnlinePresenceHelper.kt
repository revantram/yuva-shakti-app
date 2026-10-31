package com.example.util

import com.example.data.MemberEntity

object OnlinePresenceHelper {
    /**
     * Determines whether a member is currently online.
     * The actively selected user is always online.
     * Other members show online/recent based on consistent hash of time or active session.
     */
    fun isOnline(memberId: Long, activeMemberId: Long, totalMembers: Int): Boolean {
        if (memberId == activeMemberId) return true
        if (totalMembers <= 1) return false
        
        // Consistent calculation per 10-minute window so status doesn't flicker wildly
        val currentSlot = (System.currentTimeMillis() / (1000 * 60 * 10)).toInt()
        val hash = (memberId.toInt() * 31 + currentSlot).hashCode()
        return (hash % 3 == 0) // ~33% of members online concurrently
    }

    fun getOnlineCount(members: List<MemberEntity>, activeMemberId: Long): Int {
        if (members.isEmpty()) return 1
        var count = 0
        members.forEach { m ->
            if (isOnline(m.id, activeMemberId, members.size)) {
                count++
            }
        }
        return if (count > 0) count else 1
    }

    fun getOnlineMembers(members: List<MemberEntity>, activeMemberId: Long): List<MemberEntity> {
        val list = members.filter { isOnline(it.id, activeMemberId, members.size) }
        return if (list.isNotEmpty()) list else members.take(1)
    }

    fun getStatusLabel(memberId: Long, activeMemberId: Long, totalMembers: Int): String {
        return if (isOnline(memberId, activeMemberId, totalMembers)) {
            "🟢 अभी ऑनलाइन"
        } else {
            "⚪ ऑफ़लाइन"
        }
    }
}
