package com.starkindustries.expensetracker.data.remote.api

import com.google.firebase.auth.FirebaseAuth
import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseApi @Inject constructor() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User is not authenticated")

    private val userTransactions: DatabaseReference
        get() = database.child("users").child(userId).child("transactions")

    suspend fun addTransactionToFirebase(transaction: TransactionEntity) {
        val transactionRef = userTransactions.push()
        transactionRef.setValue(transaction).await()
    }

    suspend fun getAllTransactionsFromFirebase(): List<TransactionEntity> {
        try {
            val snapshot = userTransactions.get().await()
            return snapshot.children.mapNotNull { it.getValue(TransactionEntity::class.java) }
        } catch (e: Exception) {
            throw Exception("Error fetching transactions from Firebase: ${e.message}")
        }
    }

    suspend fun syncOfflineTransactions(transactions: List<TransactionEntity>) {
        for (transaction in transactions) {
            if (!transaction.isSynced) {
                addTransactionToFirebase(transaction.copy(isSynced = true))
            }
        }
    }
}