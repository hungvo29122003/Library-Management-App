package com.example.librarymanagement

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.UpdateLockStatusRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnlockReaderActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var tvStatus: TextView
    private lateinit var btnUnLock: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_unlock_reader)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etUsername = findViewById(R.id.et_username)
        tvStatus = findViewById(R.id.tvStatus)
        btnUnLock = findViewById(R.id.btnUnlock)
        val tenDangNhap = intent.getStringExtra("TEN_DANG_NHAP")
        if (tenDangNhap == null) {
            Toast.makeText(this, "Không tìm thấy tên đăng nhập!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        etUsername.setText(tenDangNhap)
        etUsername.isEnabled = false

        btnUnLock.setOnClickListener {
            unLockAccount(tenDangNhap)
        }
    }
    private fun unLockAccount(tenDangNhap: String) {
        val request = UpdateLockStatusRequest(tenDangNhap = tenDangNhap, lockAccount = false)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateLockStatus(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val lockResponse = response.body()
                        Toast.makeText(this@UnlockReaderActivity, lockResponse?.message ?: "Khóa tài khoản thành công!", Toast.LENGTH_SHORT).show()
                        tvStatus.text = "Status: Locked"
                        finish()
                    } else {
                        Toast.makeText(
                            this@UnlockReaderActivity,
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UnlockReaderActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}