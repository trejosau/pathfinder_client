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
import com.example.pathfinder_client.features.login.view.LoginActivity
import com.example.pathfinder_client.features.main.viewmodel.MainLandingViewModel

class MainLandingActivity : AppCompatActivity() {

    private val viewModel: MainLandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val videoView = findViewById<VideoView>(R.id.videoView)

        // Carga el video desde res/raw/mina.mp4
        val videoUri: Uri = "android.resource://$packageName/${R.raw.mina}".toUri()
        videoView.setVideoURI(videoUri)
        videoView.start()

        // Reproduce el video durante 4 segundos y luego pasa a la pantalla de inicio de sesión
        videoView.postDelayed({
            viewModel.onVideoCompleted()
        }, 4000)

        // Observa la señal para navegar a la siguiente pantalla
        viewModel.navigateToNextScreen.observe(this, Observer { navigate ->
            if (navigate == true) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}
