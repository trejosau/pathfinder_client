package com.example.pathfinder_client.features.sensors.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.R
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import com.example.pathfinder_client.features.login.view.LoginActivity
import com.example.pathfinder_client.features.sensors.adapter.SensorDataAdapter
import com.example.pathfinder_client.features.sensors.viewmodel.SensorsViewModel

class SensorsActivity : AppCompatActivity() {
    private val viewModel: SensorsViewModel by viewModels()
    private lateinit var sensorRecyclerView: RecyclerView
    private lateinit var connectionStatusText: TextView
    private lateinit var sensorAdapter: SensorDataAdapter
    private lateinit var btnCerrarSesion: Button
    private lateinit var preferencesManager: PreferencesManager

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

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(this)

        setupViews()
        setupObservers()

        // Start periodic updates
        handler.post(updateRunnable)

        // Setup the "Cerrar Sesión" button
        btnCerrarSesion.setOnClickListener {
            // Clear session data
            preferencesManager.clearSession()

            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupViews() {
        sensorRecyclerView = findViewById(R.id.sensorRecyclerView)
        connectionStatusText = findViewById(R.id.connectionStatusText)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

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
