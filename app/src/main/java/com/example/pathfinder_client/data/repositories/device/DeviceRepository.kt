package com.example.pathfinder_client.data.repositories.device

import android.content.Context
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import com.example.pathfinder_client.data.network.service.RetrofitClient
import com.example.pathfinder_client.data.remote.api.DeviceApiService
import com.example.pathfinder_client.data.remote.dto.device.Device

class DeviceRepository(
    private val context: Context,
    private val deviceApiService: DeviceApiService = RetrofitClient.deviceApiService
) {
    private val preferencesManager = PreferencesManager(context)

    suspend fun getDevices(): Result<List<Device>> {
        val userId = preferencesManager.getUserId()
        val token = preferencesManager.getToken()

        return if (userId != null && token != null) {
            try {
                val response = deviceApiService.getDevices(
                    userId,
                    "Bearer $token"
                )
                if (response.success) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("Usuario no autenticado. Por favor, inicia sesión."))
        }
    }
}
