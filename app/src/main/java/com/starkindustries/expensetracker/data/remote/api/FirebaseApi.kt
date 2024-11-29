package com.starkindustries.expensetracker.data.remote.api

import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseApi @Inject constructor() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    suspend fun addTransactionToFirebase(transaction: TransactionEntity) {
        val transactionRef = database.child("transactions").push()
        transactionRef.setValue(transaction).await()
    }

    suspend fun getAllTransactionsFromFirebase(): List<TransactionEntity> {
        val snapshot = database.child("transactions").get().await()
        return snapshot.children.map { it.getValue(TransactionEntity::class.java)!! }
    }
}