package com.example.pathfinder_client.data.remote.api

import com.example.pathfinder_client.data.remote.dto.device.ApiResponse
import com.example.pathfinder_client.data.remote.dto.device.TeamRequest
import com.example.pathfinder_client.data.remote.dto.device.TeamResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

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

    @POST("devices/team")
    suspend fun createTeam(
        @Header("Authorization") token: String,
        @Body teamRequest: TeamRequest
    ): Response<ApiResponse<TeamResponse>>

    @PUT("devices/linkTeam/{deviceId}/{teamId}")
    suspend fun linkTeam(
        @Path("deviceId") deviceId: String,
        @Path("teamId") teamId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Any>>
}
