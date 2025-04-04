package com.example.pathfinder_client.data.network.socket

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import org.json.JSONObject

class SocketService : Service() {
    private val TAG = "SocketService"
    private lateinit var socketManager: SocketManager
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): SocketService = this@SocketService
    }

    override fun onCreate() {
        super.onCreate()
        val preferencesManager = PreferencesManager(applicationContext)
        socketManager = SocketManager.getInstance(preferencesManager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        socketManager.connect()
        setupDefaultListeners()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        socketManager.disconnect()
        super.onDestroy()
    }

    private fun setupDefaultListeners() {
        // Example: Listen for sensor data
        socketManager.on<JSONObject>("sensor_data") { data ->
            Log.d(TAG, "Received sensor data: $data")
            // Process the data or broadcast it to activities
            val intent = Intent("SENSOR_DATA_RECEIVED")
            intent.putExtra("data", data.toString())
            sendBroadcast(intent)
        }

        // Add more default listeners as needed
    }

    // Methods to interact with the socket from activities
    fun emitEvent(event: String, data: Any) {
        socketManager.emit(event, data)
    }

    fun <T> addListener(event: String, listener: (T) -> Unit) {
        socketManager.on(event, listener)
    }

    fun removeListener(event: String) {
        socketManager.removeListener(event)
    }

    fun isSocketConnected(): Boolean {
        return socketManager.isConnected()
    }
}