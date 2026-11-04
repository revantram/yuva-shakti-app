package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a pending deposit entry for a member for a specific month/year.
 * Admin can edit all fields: amount, paymentDate, notes, status.
 */
@Entity(tableName = "pending_deposits")
data class PendingDepositEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val month: Int, // 1-12
    val year: Int,
    val amount: Int = 0,
    val paymentDate: String = "", // e.g. "2026-11-30"
    val notes: String = "",
    val status: String = "Pending" // Pending, Paid, Rejected etc.
)
