package com.example.habitx.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habitx.R
import com.example.habitx.models.Habit
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator


class HabitAdapter(
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitComplete: (Habit) -> Unit,
    private val onHabitDelete: (Habit) -> Unit,
    private val onAddStep: (Habit) -> Unit,
    private val onRemoveStep: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {
    
    private var habits = mutableListOf<Habit>()
    

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }
    
    override fun getItemCount(): Int = habits.size
    

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val habitIcon: ImageView = itemView.findViewById(R.id.habitIcon)
        private val habitName: TextView = itemView.findViewById(R.id.habitName)
        private val habitDescription: TextView = itemView.findViewById(R.id.habitDescription)
        private val progressText: TextView = itemView.findViewById(R.id.progressText)
        private val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.progressBar)
        private val btnAddStep: MaterialButton = itemView.findViewById(R.id.btnAddStep)
        private val btnRemoveStep: MaterialButton = itemView.findViewById(R.id.btnRemoveStep)
        private val btnComplete: MaterialButton = itemView.findViewById(R.id.btnComplete)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
        
        fun bind(habit: Habit) {

            habitName.text = habit.name
            habitDescription.text = habit.description
            progressText.text = "${habit.currentCount}/${habit.targetCount}"
            

            setCategoryIcon(habit.category)
            

            val progress = if (habit.targetCount > 0) {
                (habit.currentCount.toFloat() / habit.targetCount.toFloat() * 100).toInt()
            } else 0
            progressBar.progress = progress
            

            if (habit.isFullyCompleted()) {
                btnComplete.text = "✓"
                btnComplete.backgroundTintList = itemView.context.getColorStateList(R.color.success_green)
                habitName.setTextColor(itemView.context.getColor(R.color.text_secondary))
                habitDescription.setTextColor(itemView.context.getColor(R.color.text_hint))
            } else {
                btnComplete.text = "○"
                btnComplete.backgroundTintList = itemView.context.getColorStateList(R.color.primary_green)
                habitName.setTextColor(itemView.context.getColor(R.color.text_primary))
                habitDescription.setTextColor(itemView.context.getColor(R.color.text_secondary))
            }
            

            itemView.setOnClickListener {
                onHabitClick(habit)
            }
            
            btnAddStep.setOnClickListener {
                onAddStep(habit)
            }
            
            btnRemoveStep.setOnClickListener {
                onRemoveStep(habit)
            }
            
            btnComplete.setOnClickListener {
                onHabitComplete(habit)
            }
            
            btnDelete.setOnClickListener {
                onHabitDelete(habit)
            }
        }
        

        private fun setCategoryIcon(category: String?) {
            val iconRes = when (category) {
                "Health & Fitness" -> R.drawable.fitness
                "Productivity" -> R.drawable.productivity
                "Learning" -> R.drawable.learning
                "Mindfulness" -> R.drawable.mindfullness
                "Creative" -> R.drawable.creative
                "Work" -> R.drawable.work
                "Other" -> R.drawable.other
                else -> R.drawable.ic_habits_white
            }
            
            try {
                habitIcon.setImageResource(iconRes)

                habitIcon.clearColorFilter()
            } catch (e: Exception) {

                habitIcon.setImageResource(R.drawable.ic_habits_white)
            }
        }
    }
}
