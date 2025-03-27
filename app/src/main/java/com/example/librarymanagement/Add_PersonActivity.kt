package com.example.librarymanagement

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Add_PersonActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var btnAddPerson: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_person)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        spinnerRole = findViewById(R.id.spiner_role)
        btnAddPerson = findViewById(R.id.btnAddPerson)
        setupSpinner()
        btnAddPerson.setOnClickListener {
            addPerson()
        }
    }
    private fun setupSpinner() {
        // Danh sách hiển thị và giá trị thực tế
        val displayRoles = arrayOf("Đọc giả")
        val actualRoles = arrayOf("DocGia")

        // Tạo adapter cho Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayRoles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        // Mặc định chọn "Đọc giả"
        spinnerRole.setSelection(0)

        // Lấy giá trị thực tế khi chọn
        spinnerRole.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Giá trị thực tế sẽ được lấy từ actualRoles khi gửi lên API
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
            }
        })
    }
    private fun addPerson() {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val selectedRolePosition = spinnerRole.selectedItemPosition
        val actualRoles = arrayOf("DocGia", "ThuThu", "QuanLy")
        val vaiTro = actualRoles[selectedRolePosition]

        // Kiểm tra dữ liệu đầu vào
        if (username.isEmpty()) {
            etUsername.error = "Vui lòng nhập tên đăng nhập!"
            return
        }
        if (email.isEmpty()) {
            etEmail.error = "Vui lòng nhập email!"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email không hợp lệ!"
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "Vui lòng nhập mật khẩu!"
            return
        }
        if (password.length < 6) {
            etPassword.error = "Mật khẩu phải có ít nhất 6 ký tự!"
            return
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Vui lòng xác nhận mật khẩu!"
            return
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Mật khẩu xác nhận không khớp!"
            return
        }

        // Tạo request để gửi lên API
        val request = RegisterRequest(
            tenDangNhap = username,
            matKhau = password,
            email = email,
            vaiTro = vaiTro
        )

        // Gọi API register
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.register(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        Toast.makeText(this@Add_PersonActivity, registerResponse?.message ?: "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        finish() // Quay lại PersonFragment
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: response.message()
                        Toast.makeText(
                            this@Add_PersonActivity,
                            "Lỗi: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Add_PersonActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}