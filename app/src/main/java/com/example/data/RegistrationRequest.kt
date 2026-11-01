package com.example.data

data class RegistrationRequest(
    val requestId: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val memberCode: String = "",
    val monthlyAmount: Int = 100,
    val instagramId: String = "",
    val photoUri: String = "",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val timestamp: Long = 0L,
    val deviceId: String = "",
    val assignedMemberId: Long = 0L,
    val rejectionReason: String = ""
)
