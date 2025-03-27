package com.example.librarymanagement

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import com.example.librarymanagement.api.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainAdminister : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_administer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val fragmentContainer = findViewById<FragmentContainerView>(R.id.fragment_container)
        val dashboardContainer = findViewById<View>(R.id.dashboard_container)
        // Ánh xạ ImageView
        bottomNavigationView = findViewById(R.id.bottomNView1)
        if (savedInstanceState == null) {
            dashboardContainer.visibility = View.VISIBLE
            fragmentContainer.visibility = View.GONE
            bottomNavigationView.selectedItemId = R.id.nav_home // Đặt mặc định là Home
        }
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Hiển thị dashboard
                    dashboardContainer.visibility = View.VISIBLE
                    fragmentContainer.visibility = View.GONE
                    true
                }
                R.id.nav_person -> {
                    // Hiển thị PersonFragment
                    dashboardContainer.visibility = View.GONE
                    fragmentContainer.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PersonFragment())
                        .commit()
                    true
                }
                R.id.nav_book -> {
                    // Hiển thị BookFragment
                    dashboardContainer.visibility = View.GONE
                    fragmentContainer.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, BookFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    // Hiển thị ProfileFragment
                    dashboardContainer.visibility = View.GONE
                    fragmentContainer.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragmentManager())
                        .commit()
                    true
                }
                else -> false
            }
        }
        fetchMaQuanLy()
    }
    private fun fetchMaQuanLy() {
        // Lấy SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Lấy maTaiKhoan từ SharedPreferences
        val maTaiKhoan = sharedPreferences.getInt("maTaiKhoan", -1)

        if (maTaiKhoan == -1) {
            Toast.makeText(this, "Không tìm thấy mã tài khoản!", Toast.LENGTH_SHORT).show()
            return
        }

        // Gọi API để lấy maQuanLy
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getQuanLyByTaiKhoan(maTaiKhoan)
                if (response.isSuccessful) {
                    val quanLyResponse = response.body()
                    withContext(Dispatchers.Main) {
                        if (quanLyResponse != null && quanLyResponse.maQuanLy != null) {
                            val maQuanLy = quanLyResponse.maQuanLy
                            // Lưu maQuanLy vào SharedPreferences
                            editor.putInt("maQuanLy", maQuanLy)
                            editor.apply()

                            // Hiển thị thông báo Toast
                            Toast.makeText(this@MainAdminister, "Mã Quản Lý: $maQuanLy", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainAdminister, "Không tìm thấy quản lý!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainAdminister, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainAdminister, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}