package com.example.librarymanagement;
import LoginViewModel
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.librarymanagement.MainAdminister
import com.example.librarymanagement.R
import com.example.librarymanagement.librarian_mainActivity

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val edtUsername = findViewById<EditText>(R.id.edt_Username_txt)
        val edtPassword = findViewById<EditText>(R.id.edt_password_txt)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val strRegister = findViewById<TextView>(R.id.text_links_register)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginViewModel.loginUser(username, password) { success, message, role, maTaiKhoan ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success && role != null) {
                        navigateToRoleScreen(role)
                        Toast.makeText(this, "Mã tài khoản: $maTaiKhoan", Toast.LENGTH_SHORT).show()

                        // Lưu maTaiKhoan vào SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("vai Tro", role)
                        if (maTaiKhoan != -1) { // Kiểm tra maTaiKhoan hợp lệ
                            if (maTaiKhoan != null) {
                                editor.putInt("maTaiKhoan", maTaiKhoan)
                            }
                        } else {
                            Toast.makeText(this, "Không lấy được mã tài khoản!", Toast.LENGTH_SHORT).show()
                        }
                        editor.apply()
                    }
                }
            }
        }
        strRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToRoleScreen(role: String) {
        val intent = when (role) {
            "QuanLy" -> Intent(this, MainAdminister::class.java)
            "ThuThu" -> Intent(this, librarian_mainActivity::class.java)
//            "DocGia" -> Intent(this, DocGiaActivity::class.java)
            else -> null
        }

        intent?.let {
            startActivity(it)
            finish()
        }
    }
}
