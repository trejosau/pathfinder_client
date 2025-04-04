package com.example.pathfinder_client.data.models

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class SensorData(
    val type: String,
    val value: Double,
    val unit: String,
    val timestamp: Date
) {
    companion object {
        fun fromJson(topic: String, json: JSONObject): SensorData {
            // Extract sensor type from topic (e.g., "devices/id/temperature" -> "temperature")
            val sensorType = topic.split("/").last()

            // Get unit based on sensor type
            val unit = when (sensorType) {
                "temperature" -> "°C"
                "humidity" -> "%"
                "voltage" -> "V"
                "inclination" -> "°"
                "mq4" -> "ppm"
                "mq7" -> "ppm"
                "gps" -> "coord"
                else -> ""
            }

            // Parse value (handling special case for GPS which might have lat/long)
            val value = if (sensorType == "gps") {
                // For simplicity, we're just taking the first value for GPS,
                // but you might want to handle lat/long differently
                if (json.has("latitude")) json.getDouble("latitude") else 0.0
            } else {
                if (json.has("value")) json.getDouble("value") else 0.0
            }

            // Parse timestamp if available, or use current time
            val timestamp = if (json.has("timestamp")) {
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    dateFormat.parse(json.getString("timestamp")) ?: Date()
                } catch (e: Exception) {
                    Date()
                }
            } else {
                Date()
            }

            return SensorData(sensorType, value, unit, timestamp)
        }
    }

    override fun toString(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return "$type: $value$unit (${dateFormat.format(timestamp)})"
    }

    // For GPS data specifically
    fun toGpsString(json: JSONObject): String {
        val latitude = if (json.has("latitude")) json.getDouble("latitude") else 0.0
        val longitude = if (json.has("longitude")) json.getDouble("longitude") else 0.0
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return "GPS: Lat $latitude, Long $longitude (${dateFormat.format(timestamp)})"
    }
}