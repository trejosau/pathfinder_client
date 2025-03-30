package com.example.pathfinder_client.data.repositories.auth

import com.example.pathfinder_client.data.network.service.RetrofitClient
import com.example.pathfinder_client.data.remote.dto.auth.RegisterRequest
import com.example.pathfinder_client.data.remote.dto.auth.RegisterResponse

class RegisterRepository {

    suspend fun registerUser(username: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = RetrofitClient.authApiService.register(
                RegisterRequest(
                    username,
                    email,
                    password
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el registro: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}