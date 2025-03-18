package com.example.pathfinder_client

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.widget.TextView


class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: MaterialButton
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

        signupText = findViewById(R.id.signupText)

        signupText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}