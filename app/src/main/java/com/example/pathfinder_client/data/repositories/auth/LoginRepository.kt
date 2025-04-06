package com.example.pathfinder_client.data.repositories.auth

import android.content.Context
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import com.example.pathfinder_client.data.remote.dto.auth.LoginRequest
import com.example.pathfinder_client.data.remote.dto.auth.LoginResponse
import com.example.pathfinder_client.data.network.service.RetrofitClient
import com.example.pathfinder_client.data.remote.dto.auth.TokenRequest
import com.example.pathfinder_client.data.remote.dto.auth.TokenVerificationResponse

class LoginRepository(private val context: Context) {
    private val preferencesManager = PreferencesManager(context)

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = RetrofitClient.authApiService.login(
                LoginRequest(
                    email,
                    password
                )
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { loginResponse ->
                    if (loginResponse.success) {
                        // Save token from the nested data object
                        loginResponse.data.token.let { preferencesManager.saveToken(it) }
                        loginResponse.data.user.id.let { preferencesManager.saveUserId(it) }

                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(loginResponse.message))
                    }
                }
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el login: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyToken(token: String): Result<TokenVerificationResponse> {
        return try {
            val response = RetrofitClient.authApiService.verifyToken(TokenRequest(token))

            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { verifyResponse ->
                    if (verifyResponse.success) {
                        Result.success(verifyResponse)
                    } else {
                        Result.failure(Exception(verifyResponse.message))
                    }
                }
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error verificando token: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        preferencesManager.clearSession()
    }
}