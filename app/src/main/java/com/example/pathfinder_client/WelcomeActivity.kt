package com.example.pathfinder_client

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoPath = "android.resource://$packageName/${R.raw.background_video}"
        val uri = Uri.parse(videoPath)

        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0f, 0f)
            videoView.start()
        }
        videoView.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Finaliza la actividad actual
        }, 3000)  // Redirige después de 3 segundos
    }
    }

