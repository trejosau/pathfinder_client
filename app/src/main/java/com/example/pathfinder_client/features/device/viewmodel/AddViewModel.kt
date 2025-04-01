package com.example.pathfinder_client.features.device.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pathfinder_client.core.utils.WifiConnectionManager
import com.example.pathfinder_client.features.device.view.WifiActivity
import kotlinx.coroutines.launch

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiManager = WifiConnectionManager(application.applicationContext)

    // LiveData para la lista de redes disponibles
    private val _wifiNetworks = MutableLiveData<List<String>>()
    val wifiNetworks: LiveData<List<String>> get() = _wifiNetworks

    fun connectToWifi(wifiName: String, password: String) {
        if (wifiName.isEmpty() || password.isEmpty()) {
            // Aquí ya no gestionamos un estado de error, solo se puede manejar la lógica
            return
        }

        viewModelScope.launch {
            val success = wifiManager.connectToWifi(wifiName, password)
            if (success) {
                // Si la conexión es exitosa, iniciamos la actividad WifiActivity
                val intent = Intent(getApplication<Application>().applicationContext, WifiActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                getApplication<Application>().applicationContext.startActivity(intent)
            }
            // Aquí ya no se maneja el error, simplemente no hace nada si no hay éxito
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
