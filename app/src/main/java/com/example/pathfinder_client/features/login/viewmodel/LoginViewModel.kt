import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pathfinder_client.data.remote.dto.auth.LoginResponse
import com.example.pathfinder_client.data.repositories.auth.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LoginRepository(application.applicationContext)

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _loginResult.value = result
        }
    }
}
