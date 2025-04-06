package com.example.pathfinder_client.features.home.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pathfinder_client.data.remote.dto.device.ApiResponse
import com.example.pathfinder_client.data.remote.dto.device.TeamResponse
import com.example.pathfinder_client.data.repositories.devices.DeviceRepository
import retrofit2.Response

class HomeViewModel(private val repository: DeviceRepository) : ViewModel() {

    suspend fun getEquipos(userId: String, token: String): Response<ApiResponse<List<TeamResponse>>> {
        return repository.getTeamsByUserId(userId, token)
    }
}
