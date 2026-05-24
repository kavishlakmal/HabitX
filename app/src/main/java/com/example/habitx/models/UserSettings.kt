package com.example.habitx.models


data class UserSettings(
    val hydrationReminderEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 60, // 1 hour default
    val reminderStartTime: String = "08:00",
    val reminderEndTime: String = "22:00",
    val themeColor: String = "#4CAF50", // Light green
    val notificationsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val currentWaterIntake: Int = 0, // Current glasses consumed today
    val targetWaterIntake: Int = 8, // Daily target glasses
    val lastWaterUpdateDate: Long = System.currentTimeMillis() // Last update date for daily reset
)

