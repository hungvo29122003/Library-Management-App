import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.RegisterRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel : ViewModel() {
    fun registerUser(username: String, password: String, email: String, role: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.register(
                    RegisterRequest(username, password, email, role)
                )
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Đăng ký thành công!")
                } else {
                    onResult(false, "Lỗi: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                onResult(false, "Lỗi mạng!")
            } catch (e: Exception) {
                onResult(false, "Đã xảy ra lỗi: ${e.message}")
            }
        }
    }
}
