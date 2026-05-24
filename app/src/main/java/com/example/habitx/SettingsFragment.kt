package com.example.habitx

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.habitx.R
import com.example.habitx.data.DataManager
import com.example.habitx.models.UserSettings
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView


class SettingsFragment : Fragment() {
    
    private lateinit var dataManager: DataManager
    
    // Views
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var switchVibration: SwitchMaterial
    private lateinit var btnShareApp: MaterialButton
    private lateinit var btnResetData: MaterialButton
    private lateinit var btnTestNotification: MaterialButton
    private lateinit var tvAppVersion: MaterialTextView
    private lateinit var tvTotalHabits: MaterialTextView
    private lateinit var tvTotalMoods: MaterialTextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupViews(view)
        setupListeners()
        loadSettings()
        updateStats()
    }
    

    private fun setupViews(view: View) {
        switchNotifications = view.findViewById(R.id.switchNotifications)
        switchVibration = view.findViewById(R.id.switchVibration)
        btnShareApp = view.findViewById(R.id.btnShareApp)
        btnResetData = view.findViewById(R.id.btnResetData)
        btnTestNotification = view.findViewById(R.id.btnTestNotification)
        tvAppVersion = view.findViewById(R.id.tvAppVersion)
        tvTotalHabits = view.findViewById(R.id.tvTotalHabits)
        tvTotalMoods = view.findViewById(R.id.tvTotalMoods)
    }
    

    private fun setupListeners() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            updateNotificationSettings(isChecked, switchVibration.isChecked)
        }
        
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            updateNotificationSettings(switchNotifications.isChecked, isChecked)
        }
        
        btnShareApp.setOnClickListener {
            shareApp()
        }
        
        btnResetData.setOnClickListener {
            resetAllData()
        }
        
        btnTestNotification.setOnClickListener {
            testNotification()
        }
    }
    

    private fun loadSettings() {
        val settings = dataManager.getUserSettings()
        
        switchNotifications.isChecked = settings.notificationsEnabled
        switchVibration.isChecked = settings.vibrationEnabled
        
        tvAppVersion.text = getString(R.string.app_version)
    }
    

    private fun updateNotificationSettings(notificationsEnabled: Boolean, vibrationEnabled: Boolean) {
        val settings = dataManager.getUserSettings().copy(
            notificationsEnabled = notificationsEnabled,
            vibrationEnabled = vibrationEnabled
        )
        
        dataManager.saveUserSettings(settings)
        
        val message = if (notificationsEnabled) {
            getString(R.string.toast_notifications_enabled)
        } else {
            getString(R.string.toast_notifications_disabled)
        }
        
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
        }
        
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
    }
    

    private fun resetAllData() {

        dataManager.saveHabits(emptyList())
        dataManager.saveMoodEntries(emptyList())
        dataManager.resetDailyProgress()
        
        Toast.makeText(requireContext(), getString(R.string.toast_data_reset), Toast.LENGTH_SHORT).show()
        updateStats()
    }
    

    private fun updateStats() {
        val habits = dataManager.getHabits()
        val moods = dataManager.getMoodEntries()
        
        tvTotalHabits.text = habits.size.toString()
        tvTotalMoods.text = moods.size.toString()
    }
    

    private fun testNotification() {
        try {
            val reminderManager = com.example.habitx.utils.HydrationReminderManager(requireContext())
            reminderManager.showReminderNotification()
            Toast.makeText(requireContext(), getString(R.string.toast_notification_sent), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.toast_notification_failed, e.message ?: ""), Toast.LENGTH_SHORT).show()
        }
    }
}