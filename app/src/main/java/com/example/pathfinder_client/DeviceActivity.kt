package com.example.pathfinder_client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.adapters.DeviceAdapter
import com.example.pathfinder_client.models.Device
import kotlin.random.Random

class DeviceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        val recyclerView = findViewById<RecyclerView>(R.id.rvDispositivos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Generar lista simulada de dispositivos
        val dispositivos = generarDispositivosSimulados(10)

        recyclerView.adapter = DeviceAdapter(dispositivos)
    }

    private fun generarDispositivosSimulados(cantidad: Int): List<Device> {
        val nombres = listOf("Carro RC", "Drone", "Moto RC", "Robot", "Avión RC", "Barco RC")
        val tipos = listOf("Vehículo", "Aéreo", "Terrestre", "Navegación")

        return List(cantidad) {
            val nombre = nombres.random() + " " + Random.nextInt(100, 999)  // Ejemplo: "Carro RC 543"
            val tipo = tipos.random() // Ejemplo: "Aéreo"
            Device(nombre, tipo)
        }
    }
}
