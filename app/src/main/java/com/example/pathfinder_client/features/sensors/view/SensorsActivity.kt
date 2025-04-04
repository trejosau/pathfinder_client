package com.example.pathfinder_client.features.sensors.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.R
import com.example.pathfinder_client.data.models.SensorData
import com.example.pathfinder_client.features.sensors.adapter.SensorDataAdapter
import com.example.pathfinder_client.features.sensors.viewmodel.SensorsViewModel

class SensorsActivity : AppCompatActivity() {
    private val viewModel: SensorsViewModel by viewModels()
    private lateinit var sensorRecyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private lateinit var connectionStatusText: TextView
    private lateinit var sensorAdapter: SensorDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        sensorRecyclerView = findViewById(R.id.sensorRecyclerView)
        refreshButton = findViewById(R.id.refreshButton)
        connectionStatusText = findViewById(R.id.connectionStatusText)

        // Setup RecyclerView
        sensorAdapter = SensorDataAdapter()
        sensorRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SensorsActivity)
            adapter = sensorAdapter
        }

        refreshButton.setOnClickListener {
            viewModel.requestLatestData()
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
}