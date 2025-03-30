package com.example.pathfinder_client.data.remote.dto.auth

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)