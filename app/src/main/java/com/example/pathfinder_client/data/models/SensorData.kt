package com.example.pathfinder_client.data.models

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class SensorData(
    val type: String,
    val value: Double,  // Usado para la latitud o un valor de sensor
    val unit: String,   // Usado para la longitud o la unidad
    val timestamp: Date,
    val latitude: Double,  // Nueva propiedad para almacenar latitud
    val longitude: Double  // Nueva propiedad para almacenar longitud
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
                // For simplicity, we assume that "lat" and "lng" exist in JSON
                val latitude = if (json.has("lat")) json.getDouble("lat") else 0.0
                val longitude = if (json.has("lng")) json.getDouble("lng") else 0.0
                SensorData(
                    type = sensorType,
                    value = latitude,  // Store lat in value for consistency with your logic
                    unit = unit,
                    timestamp = Date(),
                    latitude = latitude,  // Store lat separately
                    longitude = longitude  // Store lng separately
                )
            } else {
                // Handle non-GPS sensor data
                val sensorValue = if (json.has("value")) json.getDouble("value") else 0.0
                SensorData(
                    type = sensorType,
                    value = sensorValue,
                    unit = unit,
                    timestamp = Date(),
                    latitude = 0.0,  // No GPS data
                    longitude = 0.0  // No GPS data
                )
            }

            return value  // This is the required return statement
        }
    }

    override fun toString(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return "$type: $value$unit (${dateFormat.format(timestamp)})"
    }

    // For GPS data specifically
    fun toGpsString(): String {
        return "  Lat $latitude, Long $longitude"
    }
}
