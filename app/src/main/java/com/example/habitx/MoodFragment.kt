package com.example.habitx

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habitx.R
import com.example.habitx.data.DataManager
import com.example.habitx.adapters.MoodAdapter
import com.example.habitx.dialogs.AddMoodDialog
import com.example.habitx.models.MoodEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


class MoodFragment : Fragment() {
    
    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: MaterialTextView
    private lateinit var fabAdd: ExtendedFloatingActionButton
    private lateinit var moodChart: LineChart
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_mood, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupViews(view)
        setupRecyclerView()
        setupFab()
        setupChart()
        loadMoodEntries()
    }
    

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewMood)
        emptyStateText = view.findViewById(R.id.emptyStateText)
        fabAdd = view.findViewById(R.id.fabAddMood)
        moodChart = view.findViewById(R.id.moodChart)
    }
    

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            onMoodClick = { mood -> editMood(mood) },
            onMoodDelete = { mood -> deleteMood(mood) }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }
    }
    

    private fun setupFab() {
        fabAdd.setOnClickListener {
            showAddMoodDialog()
        }
    }

    private fun setupChart() {
        moodChart.description.isEnabled = false
        moodChart.setTouchEnabled(true)
        moodChart.isDragEnabled = true
        moodChart.setScaleEnabled(true)
        moodChart.setPinchZoom(true)
        

        val xAxis = moodChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        

        val leftAxis = moodChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 5f
        leftAxis.granularity = 1f
        
        val rightAxis = moodChart.axisRight
        rightAxis.isEnabled = false
        

        loadMoodChartData()
    }
    

    private fun loadMoodChartData() {
        val moodEntries = dataManager.getMoodEntries()
        val entries = mutableListOf<Entry>()
        

        for (i in 6 downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dayStart = calendar.timeInMillis
            
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = calendar.timeInMillis
            

            val dayMoods = moodEntries.filter { it.dateTime in dayStart until dayEnd }
            val avgMood = if (dayMoods.isNotEmpty()) {
                dayMoods.map { it.moodValue }.average().toFloat()
            } else {
                0f
            }
            
            entries.add(Entry((6 - i).toFloat(), avgMood))
        }
        
        val dataSet = LineDataSet(entries, "Mood Trend").apply {
            color = Color.parseColor("#4CAF50")
            setCircleColor(Color.parseColor("#4CAF50"))
            lineWidth = 3f
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = Color.parseColor("#4CAF50")
            fillAlpha = 50
        }
        
        val lineData = LineData(dataSet)
        moodChart.data = lineData
        moodChart.invalidate()
    }
    

    private fun loadMoodEntries() {
        val moodEntries = dataManager.getMoodEntries().sortedByDescending { it.dateTime }
        moodAdapter.updateMoodEntries(moodEntries)
        
        // Show/hide empty state
        if (moodEntries.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateText.visibility = View.GONE
        }
        
        // Update chart
        loadMoodChartData()
    }
    

    private fun showAddMoodDialog() {
        val dialog = AddMoodDialog { mood ->
            dataManager.addMoodEntry(mood)
            loadMoodEntries()
            Toast.makeText(requireContext(), "Mood logged successfully! 😊", Toast.LENGTH_SHORT).show()
        }
        dialog.show(parentFragmentManager, "AddMoodDialog")
    }
    

    private fun editMood(mood: MoodEntry) {
        val dialog = AddMoodDialog(
            onMoodSaved = { updatedMood ->
                dataManager.updateMoodEntry(updatedMood)
                loadMoodEntries()
                loadMoodChartData()
                Toast.makeText(requireContext(), "Mood updated successfully!", Toast.LENGTH_SHORT).show()
            },
            existingMood = mood
        )
        dialog.show(parentFragmentManager, "EditMoodDialog")
    }
    

    private fun deleteMood(mood: MoodEntry) {
        dataManager.deleteMoodEntry(mood.id)
        loadMoodEntries()
        loadMoodChartData()
        Toast.makeText(requireContext(), "Mood entry deleted", Toast.LENGTH_SHORT).show()
    }
}