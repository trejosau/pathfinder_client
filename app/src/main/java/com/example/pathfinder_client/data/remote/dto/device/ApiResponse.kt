package com.example.pathfinder_client.data.remote.dto.device

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)
