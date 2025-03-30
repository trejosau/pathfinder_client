package com.example.pathfinder_client.features.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainLandingViewModel : ViewModel() {
    private val _navigateToNextScreen = MutableLiveData<Boolean>()
    val navigateToNextScreen: LiveData<Boolean> = _navigateToNextScreen

    fun onVideoCompleted() {
        _navigateToNextScreen.value = true
    }
}