package com.example.pathfinder_client.data.repositories.auth

import android.content.Context
import android.util.Log
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import com.example.pathfinder_client.data.network.service.RetrofitClient
import com.example.pathfinder_client.data.remote.dto.auth.TokenRequest
import com.example.pathfinder_client.data.remote.dto.auth.TokenVerificationResponse

class AuthRepository(private val context: Context) {
    private val TAG = "AuthRepository"
    private val preferencesManager = PreferencesManager(context)

    suspend fun verifyToken(): Result<TokenVerificationResponse> {
        return try {
            val token = preferencesManager.getToken() ?: return Result.failure(Exception("Token no encontrado"))
            Log.d(TAG, "Verificando token: $token")

            val response = RetrofitClient.authApiService.verifyToken(TokenRequest(token))

            if (response.isSuccessful && response.body() != null) {
                val verificationResponse = response.body()!!
                Log.d(TAG, "Respuesta de verificación: $verificationResponse")

                if (verificationResponse.success) {
                    Result.success(verificationResponse)
                } else {
                    Log.e(TAG, "Error en verificación: ${verificationResponse.message}")
                    Result.failure(Exception(verificationResponse.message))
                }
            } else {
                val errorMsg = "Error al verificar token: ${response.errorBody()?.string()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al verificar token: ${e.message}", e)
            Result.failure(e)
        }
    }
}