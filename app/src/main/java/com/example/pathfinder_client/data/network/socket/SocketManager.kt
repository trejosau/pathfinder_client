package com.example.pathfinder_client.data.network.socket

import android.util.Log
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager private constructor(private val preferencesManager: PreferencesManager) {
    private var socket: Socket? = null
    private val TAG = "SocketManager"
    private val listeners = mutableMapOf<String, MutableList<(Any) -> Unit>>()

    companion object {
        private const val SERVER_URL = "http://3.148.112.227:6655"
        private val SENSOR_TYPES = listOf("gps", "temperature", "humidity", "mq4", "mq7", "voltage", "inclination")
        private const val DEVICE_ID = "db2596e8-fe29-409e-8039-4c22e9a47407"

        @Volatile
        private var instance: SocketManager? = null

        fun getInstance(preferencesManager: PreferencesManager): SocketManager {
            return instance ?: synchronized(this) {
                instance ?: SocketManager(preferencesManager).also { instance = it }
            }
        }

        fun getSensorTopics(): List<String> {
            return SENSOR_TYPES.map { "devices/$DEVICE_ID/$it" }
        }
    }

    fun connect() {
        if (socket?.connected() == true) return

        try {
            val options = IO.Options()

            // Add authentication token if available
            preferencesManager.getToken()?.let { token ->
                options.extraHeaders = mapOf(
                    "Authorization" to listOf("Bearer $token")
                )
            }

            socket = IO.socket(SERVER_URL, options)

            socket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket connected: ${socket?.id()}")
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "Socket disconnected")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "Connection error: ${args[0]}")
            }

            // Auto-subscribe to all sensor topics
            autoSubscribeToSensorTopics()

            socket?.connect()
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Socket initialization error", e)
        }
    }

    private fun autoSubscribeToSensorTopics() {
        getSensorTopics().forEach { topic ->
            socket?.on(topic) { args ->
                Log.d(TAG, "Received data for topic: $topic")
                args?.firstOrNull()?.let { data ->
                    listeners[topic]?.forEach { callback ->
                        callback(data)
                    }
                }
            }
        }
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }

    fun <T> on(event: String, listener: (T) -> Unit) {
        if (!listeners.containsKey(event)) {
            listeners[event] = mutableListOf()

            socket?.on(event, Emitter.Listener { args ->
                args?.firstOrNull()?.let { data ->
                    listeners[event]?.forEach { callback ->
                        callback(data)
                    }
                }
            })
        }

        @Suppress("UNCHECKED_CAST")
        listeners[event]?.add(listener as (Any) -> Unit)
    }

    fun emit(event: String, data: Any) {
        when (data) {
            is JSONObject -> socket?.emit(event, data)
            is String -> socket?.emit(event, data)
            else -> socket?.emit(event, JSONObject(data.toString()))
        }
    }

    fun removeAllListeners() {
        listeners.keys.forEach { event ->
            socket?.off(event)
        }
        listeners.clear()
    }

    fun removeListener(event: String) {
        socket?.off(event)
        listeners.remove(event)
    }
}