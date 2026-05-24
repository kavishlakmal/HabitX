package com.example.habitx.models

import java.util.Date


data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val createdDate: Long = System.currentTimeMillis(),
    val color: String = "#4CAF50" // Default light green
) {

    fun getCompletionPercentage(): Float {
        return if (targetCount > 0) {
            (currentCount.toFloat() / targetCount.toFloat() * 100).coerceAtMost(100f)
        } else 0f
    }
    

    fun isFullyCompleted(): Boolean {
        return currentCount >= targetCount
    }
}

