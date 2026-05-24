package com.example.habitx.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habitx.R
import com.example.habitx.models.MoodEntry

/**
 * Adapter for displaying mood entries in a RecyclerView
 * Handles mood display, editing, and deletion
 */
class MoodAdapter(
    private val onMoodClick: (MoodEntry) -> Unit,
    private val onMoodDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    
    private var moodEntries = mutableListOf<MoodEntry>()
    
    /**
     * Update the list of mood entries and notify adapter
     */
    fun updateMoodEntries(newMoodEntries: List<MoodEntry>) {
        moodEntries.clear()
        moodEntries.addAll(newMoodEntries)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }
    
    override fun getItemCount(): Int = moodEntries.size
    
    /**
     * ViewHolder for mood entry items
     */
    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moodEmoji: TextView = itemView.findViewById(R.id.moodEmoji)
        private val moodDate: TextView = itemView.findViewById(R.id.moodDate)
        private val moodNote: TextView = itemView.findViewById(R.id.moodNote)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        
        fun bind(moodEntry: MoodEntry) {
            // Set mood information
            moodEmoji.text = moodEntry.emoji
            moodDate.text = moodEntry.getFormattedDateTime()
            moodNote.text = moodEntry.note.ifEmpty { "No note" }
            
            // Set click listeners
            itemView.setOnClickListener {
                onMoodClick(moodEntry)
            }
            
            btnEdit.setOnClickListener {
                onMoodClick(moodEntry)
            }
            
            btnDelete.setOnClickListener {
                onMoodDelete(moodEntry)
            }
        }
    }
}


