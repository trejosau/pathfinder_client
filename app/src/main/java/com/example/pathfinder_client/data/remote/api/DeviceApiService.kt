package com.example.pathfinder_client.data.remote.api

import com.example.pathfinder_client.data.remote.dto.device.ApiResponse
import com.example.pathfinder_client.data.remote.dto.device.TeamResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DeviceApiService {

    @GET("devices/teams/{userId}")
    suspend fun getTeamsByUserId(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<TeamResponse>>>

     @GET("devices/{deviceId}")
     suspend fun getDeviceByDeviceId(
         @Path("deviceId") deviceId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<DeviceResponse>>

}
