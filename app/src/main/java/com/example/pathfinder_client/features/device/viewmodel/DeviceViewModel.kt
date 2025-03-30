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

    fun loadDevices() {
        viewModelScope.launch {
            val result = repository.getDevices()
            result.onSuccess { deviceList ->
                _devices.value = deviceList
                _error.value = null
            }.onFailure { exception ->
                _devices.value = emptyList()
                _error.value = exception.message ?: "Unknown error occurred"
            }
        }
    }
}