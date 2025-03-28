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

        // Llamamos a onVideoCompleted() después de 4 segundos
        videoView.postDelayed({
            viewModel.onVideoCompleted()
        }, 4000)

        // Si el video termina antes o si el usuario salta el video, también se llama a onVideoCompleted()
        videoView.setOnCompletionListener {
            viewModel.onVideoCompleted()
        }

        // Observamos la señal para navegar a la siguiente pantalla
        viewModel.navigateToNextScreen.observe(this, Observer { navigate ->
            if (navigate == true) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        // Permitir que el usuario salte el video tocando startText
        startText.setOnClickListener {
            viewModel.onVideoCompleted()
        }
    }
}
