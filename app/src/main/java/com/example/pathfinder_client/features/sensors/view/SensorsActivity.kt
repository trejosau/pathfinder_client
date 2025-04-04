package com.example.pathfinder_client.features.sensors.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.R
import com.example.pathfinder_client.features.sensors.adapter.SensorDataAdapter
import com.example.pathfinder_client.features.sensors.viewmodel.SensorsViewModel
import android.widget.TextView

class SensorsActivity : AppCompatActivity() {
    private val viewModel: SensorsViewModel by viewModels()
    private lateinit var sensorRecyclerView: RecyclerView
    private lateinit var connectionStatusText: TextView
    private lateinit var sensorAdapter: SensorDataAdapter

    // Handler and Runnable for periodic updates
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            viewModel.requestLatestData() // Request the latest data
            handler.postDelayed(this, 3000) // Update every 3 seconds (adjust as needed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors)

        setupViews()
        setupObservers()

        // Start periodic updates
        handler.post(updateRunnable)
    }

    private fun setupViews() {
        sensorRecyclerView = findViewById(R.id.sensorRecyclerView)
        connectionStatusText = findViewById(R.id.connectionStatusText)

        // Setup RecyclerView
        sensorAdapter = SensorDataAdapter()
        sensorRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SensorsActivity)
            adapter = sensorAdapter
        }
    }

    private fun setupObservers() {
        viewModel.sensorDataMap.observe(this) { dataMap ->
            // Update connection status
            connectionStatusText.text = "Status: Connected"

            // Convert map to list for adapter
            val sensorList = dataMap.values.toList()
            sensorAdapter.updateData(sensorList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable) // Remove callbacks to prevent memory leaks
    }
}
