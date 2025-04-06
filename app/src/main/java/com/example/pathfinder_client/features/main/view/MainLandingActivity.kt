package com.example.pathfinder_client.features.main.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.example.pathfinder_client.R
import com.example.pathfinder_client.features.home.view.HomeActivity
import com.example.pathfinder_client.features.login.view.LoginActivity
import com.example.pathfinder_client.features.main.viewmodel.MainLandingViewModel

class MainLandingActivity : AppCompatActivity() {

    private val viewModel: MainLandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val videoView = findViewById<VideoView>(R.id.videoView)

        val videoUri: Uri = "android.resource://$packageName/${R.raw.mina}".toUri()
        videoView.setVideoURI(videoUri)
        videoView.start()

        // Observa si ya hay una sesión iniciada
        viewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                // Usuario ya está logueado, ir directamente a siguiente pantalla
                navigateToHome()
            } else {
                // Si no está logueado, espera 4 segundos
                videoView.postDelayed({
                    viewModel.onVideoCompleted()
                }, 4000)
            }
        }

        viewModel.navigateToNextScreen.observe(this) { navigate ->
            if (navigate) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

