package com.example.pathfinder_client.data.remote.dto.auth

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: Data
)

{
    data class Data(
        val user: User,
        val token: String
    )

    data class User(
        val id: String,
        val username: String,
        val email: String,
        val isActive: Boolean,
        val createdAt: String,
        val updatedAt: String
    )
}
