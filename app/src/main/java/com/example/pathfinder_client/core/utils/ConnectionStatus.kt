package com.example.pathfinder_client.core.utils

sealed class ConnectionStatus {
    data class Success(val message: String) : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
    object Loading : ConnectionStatus()
}
