package com.example.pathfinder_client.features.device.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder_client.data.remote.dto.device.Device
import com.example.pathfinder_client.data.repositories.device.DeviceRepository
import kotlinx.coroutines.launch

class DeviceViewModel(private val repository: DeviceRepository) : ViewModel() {
    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Nuevo: Para almacenar el ID del dispositivo seleccionado en el ViewModel
    private val _selectedDeviceId = MutableLiveData<String?>()
    val selectedDeviceId: LiveData<String?> = _selectedDeviceId

    fun loadDevices() {
        viewModelScope.launch {
            val result = repository.getDevices()
            result.onSuccess { deviceList ->
                _devices.value = deviceList
                _error.value = null
                // Limpiar el dispositivo seleccionado cuando se carga la lista
                clearSelectedDevice()
            }.onFailure { exception ->
                _devices.value = emptyList()
                _error.value = exception.message ?: "Unknown error occurred"
            }
        }
    }

    // Nuevo: Función para seleccionar un dispositivo
    fun selectDevice(deviceId: String) {
        _selectedDeviceId.value = deviceId
    }

    // Nuevo: Función para limpiar el dispositivo seleccionado
    fun clearSelectedDevice() {
        _selectedDeviceId.value = null
    }
}