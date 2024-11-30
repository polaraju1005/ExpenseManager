package com.starkindustries.expensetracker.data.repository

import com.starkindustries.expensetracker.data.local.db.dao.TransactionDao
import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import com.starkindustries.expensetracker.data.remote.api.FirebaseApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val firebaseApi: FirebaseApi
) {

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.deleteTransaction(transactionId)
    }

    fun filterTransactionsByDescription(description: String): Flow<List<TransactionEntity>> {
        return transactionDao.filterTransactionsByDescription(description)
    }

    fun filterTransactionsByType(type: String): Flow<List<TransactionEntity>> {
        return transactionDao.filterTransactionsByType(type)
    }

    suspend fun addTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
        firebaseApi.addTransactionToFirebase(transaction)
    }

    suspend fun syncTransactions() {
        val transactions = firebaseApi.getAllTransactionsFromFirebase()
        transactions.forEach {
            transactionDao.insertTransaction(it)
        }
    }

    fun getTransactionById(id: Long): Flow<TransactionEntity?> = flow {
        emit(transactionDao.getTransactionById(id))
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }


}