package com.example.librarymanagement

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.QuanLyInfo
import com.example.librarymanagement.model.UpdateQuanLyRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragmentManager : Fragment() {

    private lateinit var edtTen: EditText
    private lateinit var edtCCCD: EditText
    private lateinit var edtDiaChi: EditText
    private lateinit var edtSdt: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtNamSinh: EditText
    private lateinit var btnUpdateProfile: AppCompatButton
    private lateinit var btnLogout: AppCompatButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_manager, container, false)

        // Ánh xạ các view
        edtTen = view.findViewById(R.id.edtTen)
        edtCCCD = view.findViewById(R.id.edt_CCCD)
        edtDiaChi = view.findViewById(R.id.edt_DiaChi)
        edtSdt = view.findViewById(R.id.edt_Sdt)
        edtEmail = view.findViewById(R.id.edt_Email)
        edtNamSinh = view.findViewById(R.id.edt_NamSinh)
        btnUpdateProfile = view.findViewById(R.id.btn_UpdateProfile)
        btnLogout = view.findViewById(R.id.btn_Logout)

        // Khởi tạo SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Lấy maQuanLy từ SharedPreferences và hiển thị thông tin ban đầu
        fetchManagerInfo()

        // Xử lý sự kiện nút "Cập nhật thông tin"
        btnUpdateProfile.setOnClickListener {
            updateManagerProfile()
        }

        // Xử lý sự kiện nút "Đăng xuất"
        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun updateManagerProfile() {
        // Lấy maQuanLy từ SharedPreferences
        val maQuanLy = sharedPreferences.getInt("maQuanLy", -1)

        if (maQuanLy == -1) {
            Toast.makeText(requireContext(), "Không tìm thấy mã quản lý!", Toast.LENGTH_SHORT).show()
            return
        }

        // Lấy dữ liệu từ EditText
        val tenQuanLy = edtTen.text.toString().trim()
        val cccd = edtCCCD.text.toString().trim()
        val diaChi = edtDiaChi.text.toString().trim()
        val sdt = edtSdt.text.toString().trim()
        val namSinh = edtNamSinh.text.toString().trim()
        val avatar = "" // Hiện tại không xử lý avatar, để trống

        // Kiểm tra dữ liệu đầu vào
        if (tenQuanLy.isEmpty() || cccd.isEmpty() || diaChi.isEmpty() || sdt.isEmpty() || namSinh.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo request body
        val request = UpdateQuanLyRequest(
            tenQuanLy = tenQuanLy,
            cccd = cccd,
            diaChi = diaChi,
            namSinh = namSinh,
            sdt = sdt,
            avatar = avatar
        )

        // Gọi API để cập nhật thông tin
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateManager(maQuanLy, request)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                        // Sau khi cập nhật thành công, lấy thông tin mới và hiển thị
                        fetchManagerInfo()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchManagerInfo() {
        // Lấy maQuanLy từ SharedPreferences
        val maQuanLy = sharedPreferences.getInt("maQuanLy", -1)

        if (maQuanLy == -1) {
            Toast.makeText(requireContext(), "Không tìm thấy mã quản lý!", Toast.LENGTH_SHORT).show()
            return
        }

        // Gọi API để lấy thông tin quản lý
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getInfomationQuanLy(maQuanLy)
                if (response.isSuccessful) {
                    val quanLyInfo = response.body()
                    withContext(Dispatchers.Main) {
                        if (quanLyInfo != null) {
                            // Gán dữ liệu vào EditText
                            edtTen.setText(quanLyInfo.tenQuanLy)
                            edtCCCD.setText(quanLyInfo.cccd)
                            edtDiaChi.setText(quanLyInfo.diaChi)
                            edtSdt.setText(quanLyInfo.sdt)
                            edtEmail.setText(quanLyInfo.email)
                            edtNamSinh.setText(quanLyInfo.namSinh)
                        } else {
                            Toast.makeText(requireContext(), "Không tìm thấy thông tin quản lý!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.logout()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val logoutResponse = response.body()
                        Toast.makeText(
                            requireContext(),
                            logoutResponse?.message ?: "Đăng xuất thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Xóa thông tin đăng nhập trong SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.clear() // Xóa tất cả dữ liệu trong SharedPreferences
                        editor.apply()

                        // Chuyển về màn hình đăng nhập
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}