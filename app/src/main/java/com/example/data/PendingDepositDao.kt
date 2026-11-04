package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingDepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pendingDeposit: PendingDepositEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pendingDeposits: List<PendingDepositEntity>)

    @Update
    suspend fun update(pendingDeposit: PendingDepositEntity)

    @Query("SELECT * FROM pending_deposits WHERE memberId = :memberId AND month = :month AND year = :year")
    fun getPendingForMember(memberId: Long, month: Int, year: Int): Flow<List<PendingDepositEntity>>

    @Query("SELECT * FROM pending_deposits WHERE month = :month AND year = :year")
    fun getAllPendingForMonth(month: Int, year: Int): Flow<List<PendingDepositEntity>>

    @Query("DELETE FROM pending_deposits WHERE month = :month AND year = :year")
    suspend fun deleteAllForMonth(month: Int, year: Int)
}
