package com.example.pathfinder_client

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class ConnectingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connecting)

        // Retraso de 3 segundos (3000 milisegundos) antes de pasar a la siguiente actividad
        Handler(Looper.getMainLooper()).postDelayed({
            // Crear un Intent para iniciar la nueva actividad
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Finalizar esta actividad para que no se pueda volver atrás
            finish()
        }, 3000) // 3000 ms = 3 segundos
    }
}