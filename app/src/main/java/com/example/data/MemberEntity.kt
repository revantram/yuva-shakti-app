package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val monthlyAmount: Int = 0,
    val status: String = "Pending", // "Paid" or "Pending"
    val paidDate: String = "",       // e.g. "2026-10-28"
    val paymentMode: String = "",    // "UPI", "Cash", "Bank Transfer"
    val notes: String = "",
    val sortOrder: Int = 0,
    val instagramId: String = "",
    val facebookId: String = "",
    val whatsappNumber: String = "",
    val villageCity: String = "",
    val memberCode: String = ""
)

@Entity(tableName = "cycle_history")
data class CycleHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cycleName: String,
    val dateRecorded: Long = System.currentTimeMillis(),
    val totalMembers: Int,
    val paidMembers: Int,
    val pendingMembers: Int,
    val totalCollected: Int,
    val targetAmount: Int
)

@Entity(tableName = "deposit_notifications")
data class DepositNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val amount: Int,
    val paymentMode: String,
    val paymentDate: String,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String,
    val isRead: Boolean = false,
    val isUrgent: Boolean = false
)

@Entity(tableName = "group_messages")
data class GroupMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderRole: String = "सदस्य", // "मुख्य व्यवस्थापक", "सदस्य"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystemAlert: Boolean = false,
    val isWhatsAppMessage: Boolean = false,
    val isUrgentAlert: Boolean = false
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemName: String,
    val amount: Int,
    val category: String = "व्यवस्था", // "सामग्री", "प्रसाद/भोजन", "व्यवस्था", "अन्य"
    val date: String,
    val receiptSlipUri: String = "",
    val recordedBy: String = "व्यवस्थापक",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_requests")
data class PaymentRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val amount: Int,
    val screenshotUri: String = "",
    val note: String = "मैंने भुगतान कर दिया है, कृपया मेरा भुगतान जमा दर्ज करें।",
    val utrOrRef: String = "",
    val requestDate: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "event_gallery")
data class EventGalleryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val eventDate: String,
    val description: String = "",
    val driveDownloadLink: String = "",
    val photosJson: String = "", // Pipe separated photo URIs
    val createdBy: String = "मुख्य व्यवस्थापक",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "loan_requests")
data class LoanRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long = 0,
    val memberName: String,
    val memberPhone: String = "",
    val amountRequested: Int,
    val purpose: String,
    val returnPromiseDays: Int,
    val requestDate: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected", "Repaid"
    val adminNote: String = "",
    val upVotes: Int = 0,
    val downVotes: Int = 0,
    val votedMemberIds: String = "", // e.g., "1:up,2:down"
    val timestamp: Long = System.currentTimeMillis()
)

