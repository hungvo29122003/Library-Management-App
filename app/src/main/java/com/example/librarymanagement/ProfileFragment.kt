package com.example.librarymanagement

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.UpdateThuThuRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var maTaiKhoan: Int = -1
    private var maThuThu: Int = -1
    private lateinit var edtTen: EditText
    private lateinit var edtCCCD: EditText
    private lateinit var edtDiaChi: EditText
    private lateinit var edtSdt: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtNamSinh: EditText
    private lateinit var sharedPreferences: SharedPreferences

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Lấy maTaiKhoan từ SharedPreferences
        maTaiKhoan = sharedPreferences.getInt("maTaiKhoan", -1)
        if (maTaiKhoan == -1) {
            Toast.makeText(requireContext(), "Không tìm thấy mã tài khoản!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }
        fetchMaThuThu(maTaiKhoan)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn_updateProfile = view.findViewById<AppCompatButton>(R.id.btn_UpdateProfile)
        val btn_logout = view.findViewById<AppCompatButton>(R.id.btn_Logout)
        edtTen = view.findViewById(R.id.edtTen)
        edtCCCD = view.findViewById(R.id.edt_CCCD)
        edtDiaChi = view.findViewById(R.id.edt_DiaChi)
        edtSdt = view.findViewById(R.id.edt_Sdt)
        edtEmail = view.findViewById(R.id.edt_Email)
        edtNamSinh = view.findViewById(R.id.edt_NamSinh)
        Toast.makeText(requireContext(), "Mã tài khoản: $maTaiKhoan", Toast.LENGTH_SHORT).show()
        btn_updateProfile.setOnClickListener {
            val tenThuThu = edtTen.text.toString().trim()
            val cccd = edtCCCD.text.toString().trim()
            val diaChi = edtDiaChi.text.toString().trim()
            val sdt = edtSdt.text.toString().trim()
            val namSinh = edtNamSinh.text.toString().trim()

            // Kiểm tra dữ liệu đầu vào
            if (tenThuThu.isEmpty() || cccd.isEmpty() || diaChi.isEmpty() || sdt.isEmpty() || namSinh.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo request để gửi lên API
            val request = UpdateThuThuRequest(
                tenThuThu = tenThuThu,
                cccd = cccd,
                diaChi = diaChi,
                namSinh = namSinh,
                sdt = sdt,
                avatar = null // Hiện tại chưa xử lý avatar, để null
            )

            // Gọi API updateThuThu
            updateThuThu(maThuThu, request)
            Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
        }
        btn_logout.setOnClickListener {
            logout()
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
    private fun updateThuThu(id: Int, request: UpdateThuThuRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateThuThu(id, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val updateResponse = response.body()
                        Toast.makeText(
                            requireContext(),
                            updateResponse?.message ?: "Cập nhật thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
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
    private fun fetchMaThuThu(maTaiKhoan: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getThuThuByTaiKhoan(maTaiKhoan)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val thuThuResponse = response.body()
                        if (thuThuResponse != null) {
                            maThuThu = thuThuResponse.maThuThu
                            // Lưu maThuThu vào SharedPreferences
                            val editor = sharedPreferences.edit()
                            editor.putInt("maThuThu", maThuThu)
                            editor.apply()
                            fetchThuThuInfo(maThuThu)
                            Toast.makeText(requireContext(), "Mã thủ thư: $maThuThu", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Không nhận được dữ liệu thủ thư!", Toast.LENGTH_SHORT).show()
                        }
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
    private fun fetchThuThuInfo(maThuThu: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getInfomationThuThu(maThuThu)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val thuThuInfo = response.body()
                        if (thuThuInfo != null) {
                            // Gán dữ liệu vào EditText
                            edtTen.setText(thuThuInfo.tenThuThu)
                            edtCCCD.setText(thuThuInfo.cccd)
                            edtDiaChi.setText(thuThuInfo.diaChi)
                            edtSdt.setText(thuThuInfo.sdt)
                            edtEmail.setText(thuThuInfo.email)
                            edtNamSinh.setText(thuThuInfo.namSinh.substring(0, 10)) // Chỉ lấy YYYY-MM-DD
                        } else {
                            Toast.makeText(requireContext(), "Không nhận được thông tin thủ thư!", Toast.LENGTH_SHORT).show()
                        }
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