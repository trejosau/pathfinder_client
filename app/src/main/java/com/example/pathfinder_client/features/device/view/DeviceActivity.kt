package com.example.pathfinder_client.features.device.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathfinder_client.common.adapters.DeviceAdapter
import com.example.pathfinder_client.data.repositories.device.DeviceRepository
import com.example.pathfinder_client.databinding.ActivityDeviceBinding
import com.example.pathfinder_client.features.device.viewmodel.DeviceViewModel
import com.example.pathfinder_client.features.device.viewmodel.DeviceViewModelFactory
import com.example.pathfinder_client.features.device.view.AddActivity

class DeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceBinding
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var viewModel: DeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pre-view loading initialization
        preInitialize()

        // Inflate binding
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup ViewModel
        setupViewModel()

        // Setup Observers
        setupObservers()

        // Setup Button Listeners
        setupButtonListeners()

        // Load initial devices
        viewModel.loadDevices()
    }

    private fun preInitialize() {
        println("Initializing DeviceActivity before view loading")
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter()
        binding.rvDispositivos.apply {
            adapter = deviceAdapter
            layoutManager = LinearLayoutManager(this@DeviceActivity)
        }
    }

    private fun setupViewModel() {
        val repository = DeviceRepository(this)
        val factory = DeviceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DeviceViewModel::class.java]
    }

    private fun setupObservers() {
        // Devices Observer
        viewModel.devices.observe(this) { devices ->
            // Update visibility of no devices text
            binding.tvNoDevices.visibility = if (devices.isEmpty()) View.VISIBLE else View.GONE

            // Update RecyclerView
            deviceAdapter.submitList(devices)

            // Optionally, you can add a check to clear the list if no devices
            binding.rvDispositivos.visibility = if (devices.isEmpty()) View.GONE else View.VISIBLE
        }

        // Error Observer
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtonListeners() {
        // Listener for the "Vincular Dispositivo" button
        binding.btnVincular.setOnClickListener {
            openDevicePairingScreen()
        }
    }

    private fun openDevicePairingScreen() {
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }
}