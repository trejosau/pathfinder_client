package com.example.pathfinder_client.data.remote.api

import com.example.pathfinder_client.data.remote.dto.esp32.WifiCredentials
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface WifiApiService {

    @Headers("Content-Type: application/json")
    @POST("api/wifi")
    suspend fun sendWifiCredentials(@Body wifiCredentials: WifiCredentials): Response<Void>
}
