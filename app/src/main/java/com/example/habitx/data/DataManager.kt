package com.example.habitx.data

import android.content.Context
import android.content.SharedPreferences
import com.example.habitx.models.Habit
import com.example.habitx.models.MoodEntry
import com.example.habitx.models.UserSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("HabitX_Prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_HABITS = "habits"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_USER_SETTINGS = "user_settings"
        private const val KEY_DAILY_PROGRESS = "daily_progress"
    }
    

    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }
    
    fun getHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit.copy(id = System.currentTimeMillis().toString()))
        saveHabits(habits)
    }
    
    fun updateHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            saveHabits(habits)
        }
    }
    
    fun deleteHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
    }
    

    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val json = gson.toJson(moodEntries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, json).apply()
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        val json = prefs.getString(KEY_MOOD_ENTRIES, null) ?: return emptyList()
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun addMoodEntry(moodEntry: MoodEntry) {
        val moodEntries = getMoodEntries().toMutableList()
        moodEntries.add(moodEntry.copy(id = System.currentTimeMillis().toString()))
        saveMoodEntries(moodEntries)
    }
    
    fun getMoodEntriesForDate(date: Long): List<MoodEntry> {
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)
        return getMoodEntries().filter { 
            it.dateTime in startOfDay..endOfDay 
        }
    }
    
    fun deleteMoodEntry(moodId: String) {
        val moods = getMoodEntries().toMutableList()
        moods.removeAll { it.id == moodId }
        saveMoodEntries(moods)
    }
    
    fun updateMoodEntry(updatedMood: MoodEntry) {
        val moods = getMoodEntries().toMutableList()
        val index = moods.indexOfFirst { it.id == updatedMood.id }
        if (index != -1) {
            moods[index] = updatedMood
            saveMoodEntries(moods)
        }
    }
    

    fun saveUserSettings(settings: UserSettings) {
        val json = gson.toJson(settings)
        prefs.edit().putString(KEY_USER_SETTINGS, json).apply()
    }
    
    fun getUserSettings(): UserSettings {
        val json = prefs.getString(KEY_USER_SETTINGS, null)
        return if (json != null) {
            gson.fromJson(json, UserSettings::class.java)
        } else {
            UserSettings()
        }
    }
    

    fun updateDailyProgress(habitId: String, date: Long, progress: Int) {
        val key = "${KEY_DAILY_PROGRESS}_${habitId}_${date}"
        prefs.edit().putInt(key, progress).apply()
    }
    
    fun getDailyProgress(habitId: String, date: Long): Int {
        val key = "${KEY_DAILY_PROGRESS}_${habitId}_${date}"
        return prefs.getInt(key, 0)
    }
    
    fun resetDailyProgress() {
        val editor = prefs.edit()
        prefs.all.keys.filter { it.startsWith(KEY_DAILY_PROGRESS) }
            .forEach { editor.remove(it) }
        editor.apply()
    }
    

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getEndOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    

    fun getTodayCompletionPercentage(): Float {
        val habits = getHabits()
        if (habits.isEmpty()) return 0f
        
        val totalCompletion = habits.sumOf { it.currentCount }
        val totalTarget = habits.sumOf { it.targetCount }
        
        return if (totalTarget > 0) {
            (totalCompletion.toFloat() / totalTarget.toFloat() * 100).coerceAtMost(100f)
        } else 0f
    }
}
