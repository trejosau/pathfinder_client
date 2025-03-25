package com.example.pathfinder_client

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: MaterialButton
    private lateinit var signupText: TextView
    private lateinit var forgotPasswordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)

        loginButton.setOnClickListener {
            val intent = Intent(this, DeviceActivity::class.java) // Redirigir a Mis Dispositivos
            startActivity(intent)
            finish()
        }

        signupText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java) // Redirigir a recuperación de contraseña
            startActivity(intent)
        }
    }
}
