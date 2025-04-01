package com.example.pathfinder_client.features.device.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pathfinder_client.databinding.ActivityAddBinding
import com.example.pathfinder_client.features.device.viewmodel.AddViewModel

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var viewModel: AddViewModel

    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all required permissions are granted
        val allGranted = permissions.entries.all { it.value }

        if (allGranted) {
            // Load WiFi networks once permissions are granted
            viewModel.loadAvailableWifiNetworks()
        } else {
            Toast.makeText(
                this,
                "Se requieren permisos para buscar redes WiFi",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usando View Binding
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[AddViewModel::class.java]

        // Configurar el listener para el botón de conectar
        binding.connectButton.setOnClickListener {
            val wifiName = binding.wifiSpinner.selectedItem.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.connectToWifi(wifiName, password)
        }

        // Observar los cambios en el ViewModel
        setupObservers()

        // Verificar y solicitar permisos antes de cargar las redes WiFi
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
            )
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            viewModel.loadAvailableWifiNetworks()
        } else {
            // Request permissions
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }

    private fun setupObservers() {

        viewModel.wifiNetworks.observe(this) { networks ->
            if (networks.isEmpty()) {
                Toast.makeText(
                    this,
                    "No se encontraron redes WiFi disponibles",
                    Toast.LENGTH_SHORT
                ).show()
                return@observe
            }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                networks
            )

            binding.wifiSpinner.adapter = adapter // Asigna el adaptador

            binding.wifiSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    binding.passwordTextInputLayout.visibility = View.VISIBLE
                    binding.connectButton.visibility = View.VISIBLE
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No hacer nada si no hay selección
                }
            }
        }
    }
}