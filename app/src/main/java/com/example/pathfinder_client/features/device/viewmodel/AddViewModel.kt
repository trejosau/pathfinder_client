package com.example.pathfinder_client.features.device.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pathfinder_client.core.utils.ConnectionStatus
import com.example.pathfinder_client.core.utils.WifiConnectionManager
import kotlinx.coroutines.launch

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiManager = WifiConnectionManager(application.applicationContext)

    // LiveData para la lista de redes disponibles
    private val _wifiNetworks = MutableLiveData<List<String>>()
    val wifiNetworks: LiveData<List<String>> get() = _wifiNetworks

    // LiveData para el estado de la conexión
    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus

    fun connectToWifi(wifiName: String, password: String) {
        if (wifiName.isEmpty() || password.isEmpty()) {
            _connectionStatus.value = ConnectionStatus.Error("Debes completar todos los campos")
            return
        }

        _connectionStatus.value = ConnectionStatus.Loading

        viewModelScope.launch {
            val success = wifiManager.connectToWifi(wifiName, password)
            if (success) {
                _connectionStatus.value = ConnectionStatus.Success("Conectado a $wifiName")
            } else {
                _connectionStatus.value = ConnectionStatus.Error("Error al conectar. Verifica la contraseña.")
            }
        }
    }

    fun loadAvailableWifiNetworks() {
        viewModelScope.launch {
            try {
                val networks = wifiManager.loadAvailableWifiNetworks()
                println("Redes cargadas en ViewModel: $networks") // Agrega esto para debug
                _wifiNetworks.postValue(networks) // Usa postValue para LiveData
            } catch (e: Exception) {
                _wifiNetworks.postValue(emptyList()) // En caso de error, no debe ser null
            }
        }
    }
}
