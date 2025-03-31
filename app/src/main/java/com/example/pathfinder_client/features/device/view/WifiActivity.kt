package com.example.pathfinder_client.features.device.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pathfinder_client.databinding.ActivityWifiBinding

class WifiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWifiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar listener para el botón de conexión
        binding.connectButton.setOnClickListener {
            val ssid = binding.ssidDropdown.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val deviceName = binding.deviceNameInput.text.toString().trim()

            if (ssid.isEmpty() || password.isEmpty() || deviceName.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarCredenciales(ssid, password, deviceName)
        }
    }

    private fun enviarCredenciales(ssid: String, password: String, deviceName: String) {
        // Aquí implementa la lógica real para enviar las credenciales al punto de acceso.
        // Por ejemplo, podrías realizar una petición HTTP o utilizar sockets.
        Toast.makeText(
            this,
            "Enviando: SSID=$ssid, Contraseña=$password, Nombre dispositivo=$deviceName",
            Toast.LENGTH_SHORT
        ).show()

        // Ejemplo: Una vez enviados los datos, podrías redirigir a otra actividad:
        // startActivity(Intent(this, OtraActivity::class.java))
        // finish()
    }
}
