package com.example.habitx.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.habitx.MainActivity
import com.example.habitx.R
import java.util.concurrent.TimeUnit


class HydrationReminderManager(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "hydration_reminders"
        private const val NOTIFICATION_ID = 1001
        private const val WORK_TAG = "hydration_reminder_work"
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    

    fun startReminders(intervalMinutes: Int) {
        stopReminders() // Stop existing reminders first
        
        // Show immediate notification for testing
        showReminderNotification()
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .build()
        
        // For testing with 1 minute, use OneTimeWorkRequest with delay
        if (intervalMinutes == 1) {
            val inputData = Data.Builder()
                .putInt("interval_minutes", intervalMinutes)
                .build()
                
            val oneTimeWork = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setInputData(inputData)
                .addTag(WORK_TAG)
                .build()
            
            WorkManager.getInstance(context).enqueue(oneTimeWork)
        } else {
            // For longer intervals, use PeriodicWorkRequest
            val hydrationWork = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
                intervalMinutes.toLong(), TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .addTag(WORK_TAG)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                hydrationWork
            )
        }
    }
    

    fun scheduleNextReminder(intervalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .build()
        
        val inputData = Data.Builder()
            .putInt("interval_minutes", intervalMinutes)
            .build()
        
        val nextWork = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
            .setConstraints(constraints)
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag(WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueue(nextWork)
    }
    

    fun stopReminders() {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
    }
    

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_description)
                enableVibration(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    

    fun showReminderNotification() {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Changed to HIGH
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Added defaults
                .build()
            
            notificationManager.notify(NOTIFICATION_ID, notification)
            android.util.Log.d("HydrationReminder", context.getString(R.string.log_notification_sent))
        } catch (e: Exception) {
            android.util.Log.e("HydrationReminder", context.getString(R.string.log_notification_failed, e.message ?: ""))
        }
    }
}


class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        return try {
            val reminderManager = HydrationReminderManager(applicationContext)
            reminderManager.showReminderNotification()
            
            // For 1-minute intervals, schedule the next reminder
            val intervalMinutes = inputData.getInt("interval_minutes", 15)
            if (intervalMinutes == 1) {
                reminderManager.scheduleNextReminder(intervalMinutes)
            }
            
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("HydrationReminderWorker", applicationContext.getString(R.string.log_worker_failed, e.message ?: ""))
            Result.failure()
        }
    }
}


