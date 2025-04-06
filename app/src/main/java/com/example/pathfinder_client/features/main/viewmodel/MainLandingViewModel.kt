package com.example.pathfinder_client.features.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import com.example.pathfinder_client.data.repositories.auth.AuthRepository
import kotlinx.coroutines.launch

class MainLandingViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "MainLandingViewModel"
    private val preferencesManager = PreferencesManager(application.applicationContext)
    private val authRepository = AuthRepository(application.applicationContext)

    private val _navigateToNextScreen = MutableLiveData<Boolean>()
    val navigateToNextScreen: LiveData<Boolean> = _navigateToNextScreen

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    init {
        checkIfUserIsLoggedIn()
    }

    private fun checkIfUserIsLoggedIn() {
        val token = preferencesManager.getToken()

        if (token.isNullOrEmpty()) {
            Log.d(TAG, "No se encontró token, usuario debe iniciar sesión")
            _isUserLoggedIn.value = false
            return
        }

        Log.d(TAG, "Token encontrado, verificando con el servidor")
        viewModelScope.launch {
            val result = authRepository.verifyToken()
            result.fold(
                onSuccess = { response ->
                    Log.d(TAG, "Verificación de token exitosa: ${response.message}")
                    _isUserLoggedIn.value = true
                },
                onFailure = { exception ->
                    Log.e(TAG, "Verificación de token fallida: ${exception.message}")
                    // Token inválido o expirado, limpiar sesión
                    preferencesManager.clearSession()
                    _isUserLoggedIn.value = false
                }
            )
        }
    }

    fun onVideoCompleted() {
        _navigateToNextScreen.value = true
    }
}