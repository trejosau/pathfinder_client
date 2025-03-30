package com.example.pathfinder_client.features.forgot_password.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pathfinder_client.R
import com.example.pathfinder_client.features.forgot_password.viewmodel.ForgotPasswordViewModel
import com.example.pathfinder_client.features.login.view.LoginActivity

private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        forgotPasswordViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        val loginText = findViewById<TextView>(R.id.backToLoginText)
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
