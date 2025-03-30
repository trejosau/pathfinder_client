package com.example.pathfinder_client.data.remote.dto.auth

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: UserData
)

data class UserData(
    val user: User
)

data class User(
    val id: String,
    val username: String,
    val email: String,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)
