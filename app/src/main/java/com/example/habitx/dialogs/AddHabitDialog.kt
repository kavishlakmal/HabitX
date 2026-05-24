package com.example.habitx.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.habitx.R
import com.example.habitx.models.Habit
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import android.widget.ArrayAdapter


class AddHabitDialog(
    private val onHabitSaved: (Habit) -> Unit,
    private val existingHabit: Habit? = null
) : DialogFragment() {
    
    private lateinit var etHabitName: TextInputEditText
    private lateinit var etHabitDescription: TextInputEditText
    private lateinit var etHabitCategory: MaterialAutoCompleteTextView
    private lateinit var etTargetCount: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_habit, container, false)
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupListeners()
        populateFields()
    }
    

    private fun setupViews(view: View) {
        etHabitName = view.findViewById(R.id.etHabitName)
        etHabitDescription = view.findViewById(R.id.etHabitDescription)
        etHabitCategory = view.findViewById(R.id.etHabitCategory)
        etTargetCount = view.findViewById(R.id.etTargetCount)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        
        setupCategoryDropdown()
    }
    

    private fun setupCategoryDropdown() {
        val categories = arrayOf(
            getString(R.string.category_health_fitness),
            getString(R.string.category_productivity),
            getString(R.string.category_learning),
            getString(R.string.category_mindfulness),
            getString(R.string.category_creative),
            getString(R.string.category_work),
            getString(R.string.category_other),
            getString(R.string.category_test)
        )
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        etHabitCategory.setAdapter(adapter)
    }
    

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveHabit()
        }
        
        btnCancel.setOnClickListener {
            dismiss()
        }
    }
    

    private fun populateFields() {
        existingHabit?.let { habit ->
            etHabitName.setText(habit.name)
            etHabitDescription.setText(habit.description)
            etHabitCategory.setText(habit.category ?: "")
            etTargetCount.setText(habit.targetCount.toString())
        }
    }
    

    private fun saveHabit() {
        val name = etHabitName.text.toString().trim()
        val description = etHabitDescription.text.toString().trim()
        val category = etHabitCategory.text.toString().trim()
        val targetCountText = etTargetCount.text.toString().trim()
        

        if (name.isEmpty()) {
            etHabitName.error = getString(R.string.error_habit_name_required)
            return
        }
        
        if (targetCountText.isEmpty()) {
            etTargetCount.error = getString(R.string.error_target_count_required)
            return
        }
        
        val targetCount = targetCountText.toIntOrNull()
        if (targetCount == null || targetCount <= 0) {
            etTargetCount.error = getString(R.string.error_target_count_positive)
            return
        }
        

        val habit = if (existingHabit != null) {
            existingHabit.copy(
                name = name,
                description = description,
                category = category,
                targetCount = targetCount
            )
        } else {
            Habit(
                id = System.currentTimeMillis().toString(),
                name = name,
                description = description,
                category = category,
                targetCount = targetCount,
                currentCount = 0,
                isCompleted = false,
                createdDate = System.currentTimeMillis()
            )
        }
        
        onHabitSaved(habit)
        dismiss()
    }
}
