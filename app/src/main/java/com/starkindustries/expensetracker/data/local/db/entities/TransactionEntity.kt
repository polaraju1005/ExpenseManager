package com.starkindustries.expensetracker.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String = "",
    val type: String = "",
    val amount: Double = 0.0,
    val description: String? = null,
    var isSynced: Boolean = false
)