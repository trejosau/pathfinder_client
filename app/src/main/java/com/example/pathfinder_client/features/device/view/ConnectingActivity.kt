package com.example.pathfinder_client.features.device.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pathfinder_client.databinding.ActivityConnectingBinding
import com.example.pathfinder_client.core.utils.WifiConnectionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConnectingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener los datos del Intent
        val ssid = intent.getStringExtra("SSID") ?: ""
        val password = intent.getStringExtra("PASSWORD") ?: ""

        // Verificar que se hayan pasado datos válidos
        if (ssid.isEmpty() || password.isEmpty()) {
            binding.loadingText.text = "Datos de conexión no válidos."
            finish()
            return
        }

        val wifiConnectionManager = WifiConnectionManager(applicationContext)

        // Iniciar el proceso de conexión real
        lifecycleScope.launch {
            // Paso 1: Buscar la red WiFi
            binding.loadingText.text = "Buscando red..."
            val networks = wifiConnectionManager.loadAvailableWifiNetworks()
            if (!networks.contains(ssid)) {
                binding.loadingText.text = "Red \"$ssid\" no encontrada."
                delay(2000)
                finish() // Cierra la actividad si no se encuentra la red
                return@launch
            }

            // Paso 2: Validar la contraseña (por ejemplo, longitud mínima)
            binding.loadingText.text = "Validando contraseña..."
            delay(1000)
            if (password.length < 8) {
                binding.loadingText.text = "Contraseña inválida."
                delay(2000)
                finish()
                return@launch
            }

            // Paso 3: Conectar a la red
            binding.loadingText.text = "Conectando a la red..."
            val success = wifiConnectionManager.connectToWifi(ssid, password)
            if (success) {
                binding.loadingText.text = "Conexión exitosa"
                delay(1000)
                // Redirigir a la siguiente actividad, por ejemplo MainActivity
                val intent = Intent(this@ConnectingActivity, WifiActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.loadingText.text = "Error al conectar. Verifica la contraseña."
                delay(2000)
                finish()
            }
        }
    }
}
