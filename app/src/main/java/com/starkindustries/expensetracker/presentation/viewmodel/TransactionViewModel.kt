package com.starkindustries.expensetracker.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import com.starkindustries.expensetracker.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    fun addTransaction(transaction: TransactionEntity,context: Context) {
        viewModelScope.launch {
            transactionRepository.addTransaction(transaction,context)
        }
    }

    fun syncTransactionsFromFirebase() {
        viewModelScope.launch {
            try {
                transactionRepository.syncTransactions()
            } catch (e: Exception) {
                throw Exception("Error syncing transactions from Firebase: ${e.message}")
            }
        }
    }

    fun syncTransactionsToFirebase() {
        viewModelScope.launch {
            try {
                transactionRepository.syncTransactionsToFirebase()
            } catch (e: Exception) {
                throw Exception("Error syncing transactions to Firebase: ${e.message}")
            }
        }
    }

    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionRepository.getAllTransactions()

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId)
        }
    }

    fun getTransactionById(id: Long): Flow<TransactionEntity?> = transactionRepository.getTransactionById(id)

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }

}