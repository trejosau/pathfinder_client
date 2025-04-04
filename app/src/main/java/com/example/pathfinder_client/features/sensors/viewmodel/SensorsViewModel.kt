package com.example.pathfinder_client.features.sensors.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pathfinder_client.data.models.SensorData
import com.example.pathfinder_client.data.network.socket.SocketManager
import com.example.pathfinder_client.data.network.socket.SocketService
import org.json.JSONObject

class SensorsViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SensorsViewModel"

    private val _sensorDataMap = MutableLiveData<Map<String, SensorData>>()
    val sensorDataMap: LiveData<Map<String, SensorData>> = _sensorDataMap

    private val currentSensorData = mutableMapOf<String, SensorData>()

    private var socketService: SocketService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SocketService.LocalBinder
            socketService = binder.getService()
            isBound = true

            // Register for socket events
            setupSocketListeners()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            socketService = null
            isBound = false
        }
    }

    init {
        // Start and bind to the socket service
        val intent = Intent(application, SocketService::class.java)
        application.startService(intent)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setupSocketListeners() {
        // Subscribe to all sensor topics
        SocketManager.getSensorTopics().forEach { topic ->
            socketService?.addListener<JSONObject>(topic) { data ->
                Log.d(TAG, "Received data for topic: $topic - $data")

                val sensorData = SensorData.fromJson(topic, data)
                currentSensorData[sensorData.type] = sensorData
                _sensorDataMap.postValue(currentSensorData.toMap())
            }
        }
    }

    // Function to request latest data for all sensors (if your server supports this)
    fun requestLatestData() {
        if (isBound && socketService?.isSocketConnected() == true) {
            socketService?.emitEvent("request_sensor_data", JSONObject())
        } else {
            Log.e(TAG, "Cannot request sensor data: socket not connected")
        }
    }

    override fun onCleared() {
        // Unbind from the service when ViewModel is cleared
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
        super.onCleared()
    }
}