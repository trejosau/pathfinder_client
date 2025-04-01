package com.example.pathfinder_client.features.device.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import android.content.Context
import com.example.pathfinder_client.features.device.info.view.DeviceInfoActivity


class DeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceBinding
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var viewModel: DeviceViewModel
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pre-view loading initialization
        preInitialize()

        // Inflate binding
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nuevo: Inicializar SharedPreferences
        prefs = getSharedPreferences("MisDispositivos", Context.MODE_PRIVATE)

        // Nuevo: Limpiar el dispositivo actual al inicio
        limpiarDispositivoActual()

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
        // Modificado: Ahora el adapter necesita recibir el evento onClick
        deviceAdapter = DeviceAdapter { device ->
            // Nuevo: Guardar el ID del dispositivo seleccionado
            guardarDispositivo(device.id, device.name)

            // Nuevo: Navegar a la pantalla de detalle del dispositivo
            // Asumiendo que tienes una actividad para eso
            val intent = Intent(this, DeviceInfoActivity::class.java)
            startActivity(intent)
        }

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

    // Nuevo: Función para limpiar el dispositivo actual
    private fun limpiarDispositivoActual() {
        prefs.edit().remove("current_device_id").apply()
        Log.d("DeviceActivity", "ID de dispositivo limpiado")
    }

    // Nuevo: Función para guardar el ID del dispositivo seleccionado
    private fun guardarDispositivo(deviceId: String, deviceName: String) {
        prefs.edit().apply {
            putString("current_device_id", deviceId)
            putString("current_device_name", deviceName)
            apply()
        }
        Log.d("DeviceActivity", "Dispositivo guardado: $deviceId - $deviceName")
    }

    // Nuevo: Limpiar el dispositivo al volver a esta pantalla
    override fun onResume() {
        super.onResume()
        limpiarDispositivoActual()
    }
}