package com.example.pathfinder_client.data.remote.dto.auth

data class TokenVerificationResponse(
    val success: Boolean,
    val message: String,
    val data: TokenUserData
)

data class TokenUserData(
    val userId: String,
    val username: String,
    val email: String,
    val iat: Long,
    val exp: Long
)