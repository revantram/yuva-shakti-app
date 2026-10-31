package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY sortOrder ASC, id ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT COUNT(*) FROM members")
    suspend fun getMemberCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteMemberById(id: Long)

    @Query("DELETE FROM members")
    suspend fun deleteAllMembers()

    @Query("UPDATE members SET status = 'Pending', paidDate = '', paymentMode = ''")
    suspend fun resetAllToPending()

    @Query("UPDATE members SET monthlyAmount = :amount")
    suspend fun updateAllMembersAmount(amount: Int)

    @Query("SELECT * FROM cycle_history ORDER BY dateRecorded DESC")
    fun getCycleHistory(): Flow<List<CycleHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycleHistory(history: CycleHistoryEntity): Long

    @Query("SELECT * FROM deposit_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<DepositNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: DepositNotificationEntity): Long

    @Query("UPDATE deposit_notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM deposit_notifications")
    suspend fun clearAllNotifications()

    @Query("SELECT * FROM group_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<GroupMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: GroupMessageEntity): Long

    @Query("DELETE FROM group_messages")
    suspend fun clearAllMessages()

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses")
    fun getTotalExpenseAmount(): Flow<Int>

    @Query("SELECT * FROM payment_requests ORDER BY timestamp DESC")
    fun getAllPaymentRequests(): Flow<List<PaymentRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRequest(request: PaymentRequestEntity): Long

    @Update
    suspend fun updatePaymentRequest(request: PaymentRequestEntity)

    @Delete
    suspend fun deletePaymentRequest(request: PaymentRequestEntity)

    @Query("SELECT * FROM event_gallery ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<EventGalleryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventGalleryEntity): Long

    @Update
    suspend fun updateEvent(event: EventGalleryEntity)

    @Delete
    suspend fun deleteEvent(event: EventGalleryEntity)

    @Query("SELECT * FROM loan_requests ORDER BY timestamp DESC")
    fun getAllLoanRequests(): Flow<List<LoanRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanRequest(request: LoanRequestEntity): Long

    @Update
    suspend fun updateLoanRequest(request: LoanRequestEntity)

    @Delete
    suspend fun deleteLoanRequest(request: LoanRequestEntity)

    @Query("SELECT * FROM members ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllMembersSnapshot(): List<MemberEntity>

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    suspend fun getAllExpensesSnapshot(): List<ExpenseEntity>

    @Query("SELECT * FROM loan_requests ORDER BY timestamp DESC")
    suspend fun getAllLoanRequestsSnapshot(): List<LoanRequestEntity>

    @Query("SELECT * FROM group_messages ORDER BY timestamp ASC")
    suspend fun getAllMessagesSnapshot(): List<GroupMessageEntity>

    @Query("SELECT * FROM cycle_history ORDER BY dateRecorded DESC")
    suspend fun getCycleHistorySnapshot(): List<CycleHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanRequests(requests: List<LoanRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<GroupMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycleHistories(histories: List<CycleHistoryEntity>)
}

