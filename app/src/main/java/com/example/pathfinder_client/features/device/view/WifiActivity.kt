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
import com.example.pathfinder_client.databinding.ActivityWifiBinding
import com.example.pathfinder_client.features.device.viewmodel.WifiViewModel

class WifiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWifiBinding
    private lateinit var viewModel: WifiViewModel

    // Lanzador para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Verifica que todos los permisos hayan sido concedidos
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
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
        binding = ActivityWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WifiViewModel::class.java]

        // Configurar el botón de conectar / continuar
        binding.connectButton.setOnClickListener {
            val wifiName = binding.MywifiSpinner.selectedItem.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.sendCredentialsAndConnect(wifiName, password)
        }

        setupObservers()
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
            viewModel.loadAvailableWifiNetworks()
        } else {
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
            binding.MywifiSpinner.adapter = adapter

            binding.MywifiSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    // Mostrar el campo de contraseña y el botón de conectar
                    binding.passwordTextInputLayout.visibility = View.VISIBLE
                    binding.connectButton.visibility = View.VISIBLE
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No se requiere acción
                }
            }
        }

        // Puedes observar otros LiveData del ViewModel para feedback, por ejemplo para la respuesta de la API
        viewModel.connectionResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Credenciales enviadas y conectado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fallo al enviar credenciales o conectar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
