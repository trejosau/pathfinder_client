package com.example.pathfinder_client.data.remote.api

import com.example.pathfinder_client.data.remote.dto.device.DeviceResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DeviceApiService {
    @GET("devices/{userId}")
    suspend fun getDevices(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): DeviceResponse


}