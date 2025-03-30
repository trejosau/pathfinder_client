package com.example.pathfinder_client.features.register.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder_client.data.remote.dto.auth.RegisterResponse
import com.example.pathfinder_client.data.repositories.auth.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = RegisterRepository()

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> get() = _registerResult

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(username, email, password)
            _registerResult.value = result
        }
    }
}
