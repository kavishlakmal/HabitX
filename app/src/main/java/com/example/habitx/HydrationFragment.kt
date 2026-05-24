package com.example.habitx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.util.Calendar
import androidx.fragment.app.Fragment
import com.example.habitx.R
import com.example.habitx.data.DataManager
import com.example.habitx.models.UserSettings
import com.example.habitx.utils.HydrationReminderManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.textfield.TextInputEditText


class HydrationFragment : Fragment() {
    
    private lateinit var dataManager: DataManager
    private lateinit var reminderManager: HydrationReminderManager
    

    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var progressText: MaterialTextView
    private lateinit var progressPercentage: MaterialTextView
    private lateinit var waterIntakeText: MaterialTextView
    private lateinit var etTargetGlasses: TextInputEditText
    private lateinit var etInterval: TextInputEditText
    private lateinit var btnSaveSettings: MaterialButton
    private lateinit var btnToggleReminders: MaterialButton
    private lateinit var btnAddWater: MaterialButton
    
    private var currentWaterIntake = 0
    private var targetWaterIntake = 8
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_hydration, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        reminderManager = HydrationReminderManager(requireContext())
        
        setupViews(view)
        setupListeners()
        loadSettings()
        updateWaterIntake()
    }
    

    private fun setupViews(view: View) {
        progressIndicator = view.findViewById(R.id.progressIndicator)
        progressText = view.findViewById(R.id.progressText)
        progressPercentage = view.findViewById(R.id.progressPercentage)
        waterIntakeText = view.findViewById(R.id.waterIntakeText)
        etTargetGlasses = view.findViewById(R.id.etTargetGlasses)
        etInterval = view.findViewById(R.id.etInterval)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)
        btnToggleReminders = view.findViewById(R.id.btnToggleReminders)
        btnAddWater = view.findViewById(R.id.btnAddWater)
    }
    

    private fun setupListeners() {
        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
        
        btnToggleReminders.setOnClickListener {
            toggleReminders()
        }
        
        btnAddWater.setOnClickListener {
            addWaterIntake()
        }
    }
    

    private fun loadSettings() {
        val settings = dataManager.getUserSettings()
        
        // Check if it's a new day and reset water intake if needed
        val today = Calendar.getInstance()
        val lastUpdate = Calendar.getInstance().apply {
            timeInMillis = settings.lastWaterUpdateDate
        }
        
        if (!isSameDay(today, lastUpdate)) {
            // New day - reset water intake
            val resetSettings = settings.copy(
                currentWaterIntake = 0,
                lastWaterUpdateDate = System.currentTimeMillis()
            )
            dataManager.saveUserSettings(resetSettings)
            currentWaterIntake = 0
        } else {
            // Same day - load existing data
            currentWaterIntake = settings.currentWaterIntake
        }
        
        targetWaterIntake = settings.targetWaterIntake
        etTargetGlasses.setText(targetWaterIntake.toString())
        etInterval.setText(settings.reminderIntervalMinutes.toString())
        
        updateReminderButton(settings.hydrationReminderEnabled)
        updateWaterIntake()
    }
    

    private fun saveSettings() {
        val targetText = etTargetGlasses.text.toString().trim()
        val intervalText = etInterval.text.toString().trim()
        
        if (targetText.isEmpty()) {
            etTargetGlasses.error = getString(R.string.error_target_required)
            return
        }
        
        val target = targetText.toIntOrNull()
        if (target == null || target <= 0) {
            etTargetGlasses.error = getString(R.string.error_target_positive)
            return
        }
        
        if (intervalText.isEmpty()) {
            etInterval.error = getString(R.string.error_interval_required)
            return
        }
        
        val interval = intervalText.toIntOrNull()
        if (interval == null || interval <= 0) {
            etInterval.error = getString(R.string.error_interval_positive)
            return
        }
        
        targetWaterIntake = target
        val settings = dataManager.getUserSettings().copy(
            reminderIntervalMinutes = interval,
            targetWaterIntake = target
        )
        
        dataManager.saveUserSettings(settings)
        updateWaterIntake()
        Toast.makeText(requireContext(), getString(R.string.toast_settings_saved), Toast.LENGTH_SHORT).show()
    }
    

    private fun toggleReminders() {
        val settings = dataManager.getUserSettings()
        val newSettings = settings.copy(
            hydrationReminderEnabled = !settings.hydrationReminderEnabled
        )
        
        dataManager.saveUserSettings(newSettings)
        
        if (newSettings.hydrationReminderEnabled) {
            reminderManager.startReminders(newSettings.reminderIntervalMinutes)
            Toast.makeText(requireContext(), getString(R.string.toast_reminders_enabled), Toast.LENGTH_SHORT).show()
        } else {
            reminderManager.stopReminders()
            Toast.makeText(requireContext(), getString(R.string.toast_reminders_disabled), Toast.LENGTH_SHORT).show()
        }
        
        updateReminderButton(newSettings.hydrationReminderEnabled)
    }
    

    private fun addWaterIntake() {
        currentWaterIntake++
        updateWaterIntake()
        

        val settings = dataManager.getUserSettings()
        val updatedSettings = settings.copy(
            currentWaterIntake = currentWaterIntake,
            lastWaterUpdateDate = System.currentTimeMillis()
        )
        dataManager.saveUserSettings(updatedSettings)
        
        if (currentWaterIntake >= targetWaterIntake) {
            Toast.makeText(requireContext(), getString(R.string.toast_goal_reached), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.toast_keep_it_up), Toast.LENGTH_SHORT).show()
        }
    }
    

    private fun updateWaterIntake() {
        val progress = if (targetWaterIntake > 0) {
            (currentWaterIntake.toFloat() / targetWaterIntake.toFloat() * 100).toInt()
        } else 0
        
        progressIndicator.progress = progress
        progressPercentage.text = "$progress%"
        progressText.text = "$currentWaterIntake/$targetWaterIntake glasses"
        waterIntakeText.text = getString(R.string.hydration_water_intake_title)
    }
    

    private fun updateReminderButton(enabled: Boolean) {
        if (enabled) {
            btnToggleReminders.text = getString(R.string.hydration_disable_reminders)
            btnToggleReminders.backgroundTintList = requireContext().getColorStateList(R.color.error_red)
        } else {
            btnToggleReminders.text = getString(R.string.hydration_enable_reminders)
            btnToggleReminders.backgroundTintList = requireContext().getColorStateList(R.color.primary_green)
        }
    }
    

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}