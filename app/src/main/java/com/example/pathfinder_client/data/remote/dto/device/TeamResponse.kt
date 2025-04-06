package com.example.pathfinder_client.data.remote.dto.device

import com.example.pathfinder_client.data.remote.api.HelmetResponse

data class TeamResponse(
    val id: String,
    val name: String,
    val created_at: String,
    val user_id: String,
    val helmets: List<HelmetResponse>
)
