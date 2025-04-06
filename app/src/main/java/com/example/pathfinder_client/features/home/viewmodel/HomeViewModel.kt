package com.example.pathfinder_client.features.home.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pathfinder_client.data.remote.dto.device.ApiResponse
import com.example.pathfinder_client.data.remote.dto.device.TeamResponse
import com.example.pathfinder_client.data.repositories.devices.DeviceRepository
import retrofit2.Response
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

sealed class TeamsState {
    object Loading : TeamsState()
    data class Success(val teams: List<TeamResponse>) : TeamsState()
    data class Error(val message: String) : TeamsState()
}

class HomeViewModel(private val repository: DeviceRepository) : ViewModel() {
    private val _teamsState = MutableLiveData<TeamsState>()
    val teamsState: LiveData<TeamsState> get() = _teamsState

    suspend fun getEquipos(userId: String, token: String): Response<ApiResponse<List<TeamResponse>>> {
        return repository.getTeamsByUserId(userId, token)
    }
}
