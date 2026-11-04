package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a monthly deposit record for a member.
 *
 * Fields:
 *  - memberId: foreign key reference to MemberEntity.id (not enforced here for simplicity)
 *  - month: 1‑12
 *  - year: four‑digit year (e.g., 2026)
 *  - amount: expected monthly contribution amount (₹)
 *  - status: "Pending" or "Paid"
 *  - paymentDate: actual date when payment was recorded (yyyy‑MM‑dd), empty if pending
 *  - notes: free‑form notes entered by admin/member
 */
@Entity(tableName = "monthly_deposits")
 data class MonthlyDepositEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val month: Int,
    val year: Int,
    val amount: Int = 0,
    val status: String = "Pending", // or "Paid"
    val paymentDate: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
 )
