package com.example.pathfinder_client.features.device.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pathfinder_client.core.utils.WifiConnectionManager
import com.example.pathfinder_client.data.network.service.ESP32RetrofitClient
import com.example.pathfinder_client.data.remote.dto.esp32.WifiCredentials
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.delay


class WifiViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiManager = WifiConnectionManager(application.applicationContext)

    // LiveData para la lista de redes disponibles
    private val _wifiNetworks = MutableLiveData<List<String>>()
    val wifiNetworks: LiveData<List<String>> get() = _wifiNetworks

    // LiveData para el resultado de la conexión/envío de credenciales
    private val _connectionResult = MutableLiveData<Boolean>()
    val connectionResult: LiveData<Boolean> get() = _connectionResult

    /**
     * Envía las credenciales vía Retrofit y, si el envío es exitoso,
     * intenta conectar a la red WiFi seleccionada.
     */
    fun sendCredentialsAndConnect(wifiName: String, password: String) {
        if (wifiName.isEmpty() || password.isEmpty()) {
            _connectionResult.postValue(false)
            return
        }

        viewModelScope.launch {
            try {
                // Primero, asegúrate de que estás conectado a la red del ESP32
                val esp32Connected = connectToESP32Network()
                if (!esp32Connected) {
                    Log.e("DEBUG", "No se pudo conectar a la red del ESP32")
                    _connectionResult.postValue(false)
                    return@launch
                }

                // Espera un momento para que la conexión se establezca completamente
                delay(2000)

                // Ahora envía las credenciales al ESP32
                Log.d("DEBUG", "Enviando credenciales a ESP32")
                val credentials = WifiCredentials(wifiName, password)
                val response = ESP32RetrofitClient.wifiApiService.sendWifiCredentials(credentials)
                Log.d("DEBUG", "Respuesta recibida: ${response.code()} - ${response.message()}")

                if (response.isSuccessful) {
                    // El ESP32 recibió las credenciales correctamente
                    Log.d("DEBUG", "Credenciales enviadas correctamente al ESP32")
                    _connectionResult.postValue(true)
                } else {
                    Log.e("DEBUG", "Fallo al enviar credenciales: ${response.errorBody()?.string()}")
                    _connectionResult.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Excepción: ${e.message}", e)
                _connectionResult.postValue(false)
            }
        }
    }

    private suspend fun connectToESP32Network(): Boolean {
        // Intenta conectarse a la red del ESP32 usando tu WifiConnectionManager
        try {
            Log.d("DEBUG", "Intentando conectar a ESP32_Config...")
            return wifiManager.connectToWifi("ESP32_Config", "12345678")
        } catch (e: Exception) {
            Log.e("ERROR", "Error al conectar a ESP32_Config: ${e.message}", e)
            return false
        }
    }

    fun loadAvailableWifiNetworks() {
        viewModelScope.launch {
            try {
                val networks = wifiManager.loadAvailableWifiNetworks()
                _wifiNetworks.postValue(networks)
            } catch (e: Exception) {
                _wifiNetworks.postValue(emptyList())
            }
        }
    }
}
