package com.starkindustries.expensetracker.data.repository

import android.content.Context
import android.util.Log
import com.starkindustries.expensetracker.data.local.db.dao.TransactionDao
import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import com.starkindustries.expensetracker.data.remote.api.FirebaseApi
import com.starkindustries.expensetracker.utils.NetworkUtils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao, private val firebaseApi: FirebaseApi
) {

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    suspend fun deleteTransaction(transactionId: Long, context: Context) {
        if (isNetworkAvailable(context)) {
            transactionDao.deleteTransaction(transactionId)
            firebaseApi.deleteTransactionFromFirebase(transactionId)
        } else {
            transactionDao.markAsDeleted(transactionId)
            getAllTransactions()
        }
    }

    suspend fun addTransaction(transaction: TransactionEntity, context: Context) {
        withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction)
        }
    }

    suspend fun syncTransactions() {
        try {
            val transactions = firebaseApi.getAllTransactionsFromFirebase()

            transactions.forEach { transaction ->
                if (!transactionDao.exists(transaction.id)) {
                    transactionDao.insertTransaction(transaction)
                }
            }
        } catch (e: Exception) {
            throw Exception("Error syncing transactions from Firebase: ${e.message}")
        }
    }

    suspend fun syncTransactionsToFirebase() {
        try {
            val offlineTransactions =
                transactionDao.getAllTransactions().first().filter { !it.isSynced }

            if (offlineTransactions.isNotEmpty()) {
                firebaseApi.syncOfflineTransactions(offlineTransactions)
                offlineTransactions.forEach {
                    it.isSynced = true
                    transactionDao.updateTransaction(it)
                }
            }
        } catch (e: Exception) {
            throw Exception("Error syncing transactions to Firebase: ${e.message}")
        }
    }

    fun getTransactionById(id: Long): Flow<TransactionEntity?> = flow {
        emit(transactionDao.getTransactionById(id))
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun syncPendingDeletions(context: Context) {
        if (isNetworkAvailable(context)) {
            val transactionsToDelete = transactionDao.getPendingDeletions()
            for (transaction in transactionsToDelete) {
                try {
                    firebaseApi.deleteTransactionFromFirebase(transaction.id)
                    transactionDao.deleteTransaction(transaction.id)
                    Log.d(
                        "Sync",
                        "Transaction with id ${transaction.id} synced and deleted from Firebase."
                    )
                } catch (e: Exception) {
                    Log.e(
                        "Sync",
                        "Error deleting transaction with id ${transaction.id}: ${e.message}"
                    )
                }
            }
        } else {
            Log.d("Sync", "No network available. Cannot sync pending deletions.")
        }
    }

}