package com.example.pathfinder_client.data.repositories.devices

import com.example.pathfinder_client.data.network.service.RetrofitClient
import com.example.pathfinder_client.data.remote.api.DeviceResponse
import com.example.pathfinder_client.data.remote.dto.device.TeamResponse
import com.example.pathfinder_client.data.remote.dto.device.ApiResponse
import com.example.pathfinder_client.data.remote.dto.device.TeamRequest
import retrofit2.Response

class DeviceRepository {

    suspend fun getTeamsByUserId(userId: String, token: String): Response<ApiResponse<List<TeamResponse>>> {
        return RetrofitClient.deviceApiService.getTeamsByUserId(userId, "Bearer $token")
    }

    suspend fun getDeviceByDeviceId(deviceId: String, token: String): Response<ApiResponse<DeviceResponse>> {
        return RetrofitClient.deviceApiService.getDeviceByDeviceId(deviceId, "Bearer $token")
    }

    suspend fun createTeam(name: String, userId: String, token: String): Response<ApiResponse<TeamResponse>> {
        val teamRequest = TeamRequest(name, userId)
        return RetrofitClient.deviceApiService.createTeam("Bearer $token", teamRequest)
    }

    suspend fun linkTeam(deviceId: String, teamId: String, token: String): Response<ApiResponse<Any>> {
        return RetrofitClient.deviceApiService.linkTeam(deviceId, teamId, "Bearer $token")
    }

}
