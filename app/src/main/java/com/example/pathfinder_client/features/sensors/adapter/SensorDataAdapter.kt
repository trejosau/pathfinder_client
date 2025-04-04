package com.example.pathfinder_client.features.sensors.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.R
import com.example.pathfinder_client.data.models.SensorData
import java.text.SimpleDateFormat
import java.util.*

class SensorDataAdapter : RecyclerView.Adapter<SensorDataAdapter.SensorViewHolder>() {

    private var sensorList: List<SensorData> = emptyList()

    fun updateData(newList: List<SensorData>) {
        sensorList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sensor_data, parent, false)
        return SensorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        holder.bind(sensorList[position])
    }

    override fun getItemCount(): Int = sensorList.size

    class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sensorTypeText: TextView = itemView.findViewById(R.id.sensorTypeText)
        private val sensorValueText: TextView = itemView.findViewById(R.id.sensorValueText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)

        fun bind(sensor: SensorData) {
            // Format the sensor type to be more readable
            val formattedType = sensor.type.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            sensorTypeText.text = formattedType

            // Format the value based on sensor type
            when (sensor.type) {
                "temperature" -> sensorValueText.text = String.format("%.1f°C", sensor.value)
                "humidity" -> sensorValueText.text = String.format("%.1f%%", sensor.value)
                "gps" -> sensorValueText.text = "Lat/Long" // In a real app, you'd show both values
                else -> sensorValueText.text = String.format("%.2f %s", sensor.value, sensor.unit)
            }

            // Format timestamp
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            timestampText.text = "Updated: ${dateFormat.format(sensor.timestamp)}"
        }
    }
}