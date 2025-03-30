package com.example.pathfinder_client.features.register.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pathfinder_client.R
import com.example.pathfinder_client.features.login.view.LoginActivity
import com.example.pathfinder_client.features.register.viewmodel.RegisterViewModel
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar el ViewModel
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        // Redirigir a login si se hace clic en el texto de login
        val loginText = findViewById<TextView>(R.id.loginText)
        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val usernameEditText = findViewById<TextInputEditText>(R.id.nameEditText)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerViewModel.register(username, email, password)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Observar el resultado del registro
        registerViewModel.registerResult.observe(this) { result ->
            result.fold(
                onSuccess = { response ->
                    val username = response.data.user.username
                    Toast.makeText(this, "Registro exitoso. Bienvenido $username", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error.message?.contains("User with this username or email already exists", true) == true ->
                            "El usuario o correo ya está registrado. Intenta con otro."
                        error.message?.contains("Invalid email", true) == true ->
                            "El correo electrónico no es válido."
                        error.message?.contains("Weak password", true) == true ->
                            "La contraseña es demasiado débil. Usa una más segura."
                        else -> "Error en el registro: ${error.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }

    }
}
