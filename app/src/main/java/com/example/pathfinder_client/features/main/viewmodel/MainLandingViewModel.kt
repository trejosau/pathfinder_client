package com.example.pathfinder_client.features.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainLandingViewModel : ViewModel() {

    // LiveData para indicar que se debe navegar a la siguiente pantalla
    private val _navigateToNextScreen = MutableLiveData<Boolean>()
    val navigateToNextScreen: LiveData<Boolean> get() = _navigateToNextScreen

    // Método que se llama cuando el video ha terminado
    fun onVideoCompleted() {
        _navigateToNextScreen.value = true
    }

    // Si quisieras reiniciar la navegación o controlar el estado en algún momento
    fun resetNavigation() {
        _navigateToNextScreen.value = false
    }
}
