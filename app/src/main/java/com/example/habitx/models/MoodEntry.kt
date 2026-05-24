package com.example.habitx.models

import java.text.SimpleDateFormat
import java.util.*


data class MoodEntry(
    val id: String = "",
    val emoji: String = "😊",
    val note: String = "",
    val dateTime: Long = System.currentTimeMillis(),
    val moodValue: Int = 3
) {

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(dateTime))
    }
    

    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(dateTime))
    }
    

    fun getFormattedDateTime(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(dateTime))
    }
}

