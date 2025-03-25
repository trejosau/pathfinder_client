package com.example.pathfinder_client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val resetPasswordButton: Button = findViewById(R.id.resetPasswordButton)
        val backToLoginText: TextView = findViewById(R.id.backToLoginText)

        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                Toast.makeText(this, "Se ha enviado un enlace a $email", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, ingresa tu correo", Toast.LENGTH_SHORT).show()
            }
        }

        backToLoginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
