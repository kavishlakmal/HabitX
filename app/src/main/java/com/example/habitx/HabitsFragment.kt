package com.example.habitx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habitx.R
import com.example.habitx.adapters.HabitAdapter
import com.example.habitx.data.DataManager
import com.example.habitx.models.Habit
import com.example.habitx.dialogs.AddHabitDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView


class HabitsFragment : Fragment() {
    
    private lateinit var dataManager: DataManager
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var progressText: MaterialTextView
    private lateinit var progressPercentage: MaterialTextView
    private lateinit var fabAdd: ExtendedFloatingActionButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_habits, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupViews(view)
        setupRecyclerView()
        setupFab()
        loadHabits()
    }
    

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewHabits)
        progressIndicator = view.findViewById(R.id.progressIndicator)
        progressText = view.findViewById(R.id.progressText)
        progressPercentage = view.findViewById(R.id.progressPercentage)
        fabAdd = view.findViewById(R.id.fabAddHabit)
    }
    

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            onHabitClick = { habit -> editHabit(habit) },
            onHabitComplete = { habit -> toggleHabitCompletion(habit) },
            onHabitDelete = { habit -> deleteHabit(habit) },
            onAddStep = { habit -> addHabitStep(habit) },
            onRemoveStep = { habit -> removeHabitStep(habit) }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }
    

    private fun setupFab() {
        fabAdd.setOnClickListener {
            showAddHabitDialog()
        }
    }
    

    private fun loadHabits() {
        val habits = dataManager.getHabits()
        habitAdapter.updateHabits(habits)
        updateProgress()
    }
    

    private fun updateProgress() {
        val habits = dataManager.getHabits()
        val totalHabits = habits.size
        val completedHabits = habits.count { it.isFullyCompleted() }
        
        if (totalHabits > 0) {
            val progress = (completedHabits.toFloat() / totalHabits.toFloat() * 100).toInt()
            progressIndicator.progress = progress
            progressText.text = "$completedHabits/$totalHabits habits completed"
            progressPercentage.text = "$progress%"
        } else {
            progressIndicator.progress = 0
            progressText.text = "No habits yet"
            progressPercentage.text = "0%"
        }
    }
    

    private fun showAddHabitDialog() {
        val dialog = AddHabitDialog(
            onHabitSaved = { habit ->
                dataManager.addHabit(habit)
                loadHabits()
                Toast.makeText(requireContext(), "Habit added successfully!", Toast.LENGTH_SHORT).show()
            }
        )
        dialog.show(parentFragmentManager, "AddHabitDialog")
    }
    

    private fun editHabit(habit: Habit) {
        val dialog = AddHabitDialog(
            onHabitSaved = { updatedHabit ->
                dataManager.updateHabit(updatedHabit)
                loadHabits()
                Toast.makeText(requireContext(), "Habit updated successfully!", Toast.LENGTH_SHORT).show()
            },
            existingHabit = habit
        )
        dialog.show(parentFragmentManager, "EditHabitDialog")
    }
    

    private fun toggleHabitCompletion(habit: Habit) {
        val updatedHabit = if (habit.isFullyCompleted()) {
            habit.copy(currentCount = 0, isCompleted = false)
        } else {
            habit.copy(
                currentCount = habit.targetCount,
                isCompleted = true
            )
        }
        
        dataManager.updateHabit(updatedHabit)
        loadHabits()
    }
    

    private fun deleteHabit(habit: Habit) {
        dataManager.deleteHabit(habit.id)
        loadHabits()
        Toast.makeText(requireContext(), "Habit deleted", Toast.LENGTH_SHORT).show()
    }
    

    private fun addHabitStep(habit: Habit) {
        if (habit.currentCount < habit.targetCount) {
            val updatedHabit = habit.copy(currentCount = habit.currentCount + 1)
            dataManager.updateHabit(updatedHabit)
            loadHabits()
            Toast.makeText(requireContext(), "Step added! Keep going! 💪", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Habit already completed! 🎉", Toast.LENGTH_SHORT).show()
        }
    }
    

    private fun removeHabitStep(habit: Habit) {
        if (habit.currentCount > 0) {
            val updatedHabit = habit.copy(currentCount = habit.currentCount - 1)
            dataManager.updateHabit(updatedHabit)
            loadHabits()
            Toast.makeText(requireContext(), "Step removed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No steps to remove", Toast.LENGTH_SHORT).show()
        }
    }
}