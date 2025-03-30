package com.example.pathfinder_client.features.main.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.example.pathfinder_client.R
import com.example.pathfinder_client.features.login.view.LoginActivity
import com.example.pathfinder_client.features.main.viewmodel.MainLandingViewModel

class MainLandingActivity : AppCompatActivity() {

    private val viewModel: MainLandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val startText = findViewById<TextView>(R.id.startText)

        // Carga el video desde res/raw/background_video.mp4
        val videoUri: Uri = "android.resource://$packageName/${R.raw.background_video}".toUri()
        videoView.setVideoURI(videoUri)
        videoView.start()

        videoView.postDelayed({
            viewModel.onVideoCompleted()
        }, 5000)


        // Observamos la señal para navegar a la siguiente pantalla
        viewModel.navigateToNextScreen.observe(this, Observer { navigate ->
            if (navigate == true) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        // Permitir que el usuario salte el video tocando
        startText.setOnClickListener {
            viewModel.onVideoCompleted()
        }
    }
}