package com.example.habitx.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.habitx.R
import com.example.habitx.models.MoodEntry
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView


class AddMoodDialog(
    private val existingMood: MoodEntry? = null,
    private val onMoodSaved: (MoodEntry) -> Unit
) : DialogFragment() {
    
    private lateinit var etMoodNote: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    
    // Emoji selection buttons
    private lateinit var btnEmoji1: MaterialButton
    private lateinit var btnEmoji2: MaterialButton
    private lateinit var btnEmoji3: MaterialButton
    private lateinit var btnEmoji4: MaterialButton
    private lateinit var btnEmoji5: MaterialButton
    
    private var selectedEmoji = "😊"
    private val emojis = listOf("😢", "😔", "😐", "😊", "🤩")
    private val moodColors = listOf(
        R.color.error_red,
        R.color.warning_orange,
        R.color.text_secondary,
        R.color.primary_green,
        R.color.accent_green
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_mood, container, false)
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
        etMoodNote = view.findViewById(R.id.etMoodNote)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        
        btnEmoji1 = view.findViewById(R.id.btnEmoji1)
        btnEmoji2 = view.findViewById(R.id.btnEmoji2)
        btnEmoji3 = view.findViewById(R.id.btnEmoji3)
        btnEmoji4 = view.findViewById(R.id.btnEmoji4)
        btnEmoji5 = view.findViewById(R.id.btnEmoji5)
    }
    

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveMood()
        }
        
        btnCancel.setOnClickListener {
            dismiss()
        }
        
        // Emoji selection listeners
        btnEmoji1.setOnClickListener { selectEmoji(emojis[0]) }
        btnEmoji2.setOnClickListener { selectEmoji(emojis[1]) }
        btnEmoji3.setOnClickListener { selectEmoji(emojis[2]) }
        btnEmoji4.setOnClickListener { selectEmoji(emojis[3]) }
        btnEmoji5.setOnClickListener { selectEmoji(emojis[4]) }
    }
    

    private fun populateFields() {
        existingMood?.let { mood ->
            etMoodNote.setText(mood.note)
            selectEmoji(mood.emoji)
        }
    }
    

    private fun selectEmoji(emoji: String) {
        selectedEmoji = emoji
        

        val buttons = listOf(btnEmoji1, btnEmoji2, btnEmoji3, btnEmoji4, btnEmoji5)
        buttons.forEachIndexed { index, button ->
            if (emojis[index] == emoji) {

                button.backgroundTintList = requireContext().getColorStateList(moodColors[index])
                button.elevation = 8f
            } else {

                button.backgroundTintList = requireContext().getColorStateList(moodColors[index])
                button.alpha = 0.6f
                button.elevation = 2f
            }
        }
    }
    

    private fun saveMood() {
        val note = etMoodNote.text.toString().trim()
        

        val moodEntry = if (existingMood != null) {
            existingMood.copy(
                emoji = selectedEmoji,
                note = note,
                moodValue = getMoodValue(selectedEmoji)
            )
        } else {
            MoodEntry(
                emoji = selectedEmoji,
                note = note,
                moodValue = getMoodValue(selectedEmoji)
            )
        }
        
        onMoodSaved(moodEntry)
        dismiss()
    }
    

    private fun getMoodValue(emoji: String): Int {
        return when (emoji) {
            "😢" -> 1
            "😔" -> 2
            "😊" -> 3
            "😄" -> 4
            "🤩" -> 5
            else -> 3
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}
