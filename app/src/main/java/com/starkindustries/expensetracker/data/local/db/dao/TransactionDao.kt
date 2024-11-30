package com.starkindustries.expensetracker.data.local.db.dao

import androidx.room.*
import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE id = :transactionId")
    suspend fun exists(transactionId: Long): Boolean

    @Query("SELECT * FROM transactions WHERE description LIKE :description ORDER BY date DESC")
    fun filterTransactionsByDescription(description: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun filterTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: Long): TransactionEntity?

    @Query("UPDATE transactions SET isDeleted = 1 WHERE id = :transactionId")
    suspend fun markAsDeleted(transactionId: Long)

    @Query("SELECT * FROM transactions WHERE isDeleted = 1")
    suspend fun getPendingDeletions(): List<TransactionEntity>
}