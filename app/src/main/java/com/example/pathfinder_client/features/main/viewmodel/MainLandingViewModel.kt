package com.example.pathfinder_client.features.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pathfinder_client.data.local.preferences.PreferencesManager

class MainLandingViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application.applicationContext)

    private val _navigateToNextScreen = MutableLiveData<Boolean>()
    val navigateToNextScreen: LiveData<Boolean> = _navigateToNextScreen

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    init {
        checkIfUserIsLoggedIn()
    }

    private fun checkIfUserIsLoggedIn() {
        val token = preferencesManager.getToken()
        _isUserLoggedIn.value = !token.isNullOrEmpty()
    }

    fun onVideoCompleted() {
        _navigateToNextScreen.value = true
    }
}
