package com.example.pathfinder_client.data.remote.dto.device

data class DeviceResponse(
    val success: Boolean,
    val message: String,
    val data: List<Device>
)

data class Device(
    val id: String,
    val name: String,
    val device_type: String,
    val last_location: String,
    val user_id: String,
    val created_at: String,
    val updated_at: String
)
