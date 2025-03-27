import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.LoginRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel : ViewModel() {
    fun loginUser(username: String, password: String, onResult: (Boolean, String, String?, Int?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    onResult(true, body?.message ?: "Đăng nhập thành công!", body?.User?.vaiTro, body?.User?.id)
                } else {
                    onResult(false, "Sai tài khoản hoặc mật khẩu!", null, -1)
                }
            } catch (e: HttpException) {
                onResult(false, "Lỗi server!", null, -1)
            } catch (e: Exception) {
                onResult(false, "Lỗi: ${e.message}", null, -1)
            }
        }
    }
}
