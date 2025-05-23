package com.example.pathfinder_client.features.login.view

import LoginViewModel
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pathfinder_client.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.content.Intent
import android.widget.TextView
import com.example.pathfinder_client.features.home.view.HomeActivity
import com.example.pathfinder_client.features.register.view.RegisterActivity
import com.example.pathfinder_client.features.sensors.view.SensorsActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val signupText = findViewById<TextView>(R.id.signupText)
        signupText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.loginResult.observe(this, Observer { result ->
            result.fold(
                onSuccess = { response ->
                    val username = response.data.user.username
                    Toast.makeText(this, "Bienvenido $username", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                },
                onFailure = { error ->
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            )
        })
    }
}
