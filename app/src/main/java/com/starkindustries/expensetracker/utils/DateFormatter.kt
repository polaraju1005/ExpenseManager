package com.starkindustries.expensetracker.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {
    @SuppressLint("ConstantLocale")
    private val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    @SuppressLint("ConstantLocale")
    private val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun formatDate(inputDate: String): String? {
        return try {
            val date = inputFormat.parse(inputDate)
            date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            null
        }
    }

}