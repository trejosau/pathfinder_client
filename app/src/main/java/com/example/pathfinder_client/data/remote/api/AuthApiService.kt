package com.example.pathfinder_client.data.remote.api

import com.example.pathfinder_client.data.remote.dto.auth.LoginRequest
import com.example.pathfinder_client.data.remote.dto.auth.LoginResponse
import com.example.pathfinder_client.data.remote.dto.auth.RegisterRequest
import com.example.pathfinder_client.data.remote.dto.auth.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
