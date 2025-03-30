package com.example.pathfinder_client.features.forgot_password.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel : ViewModel() {

    // LiveData para notificar el resultado del registro
    private val _forgotPasswordResult = MutableLiveData<Boolean>()
    val forgotPasswordResult: LiveData<Boolean> get() = _forgotPasswordResult

    // Función para procesar el registro
    fun forgotPassword(email: String) {
        // Validaciones básicas: en un escenario real se llamaría a un repositorio
        if (email.isNotEmpty()) {
            // Simulación de registro exitoso
            _forgotPasswordResult.value = true
        } else {
            _forgotPasswordResult.value = false
        }
    }
}