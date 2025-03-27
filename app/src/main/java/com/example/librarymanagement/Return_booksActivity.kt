package com.example.librarymanagement

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.ChiTietPhieuMuon
import com.example.librarymanagement.model.SachTra
import com.example.librarymanagement.model.TraSachRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Return_booksActivity : AppCompatActivity() {

    private lateinit var edtTenDocGia: EditText
    private lateinit var edtMaDocGia: EditText
    private lateinit var edtMaPhieuMuon: EditText
    private lateinit var listSachMuon: ListView
    private lateinit var spinnerTrangThai: Spinner
    private lateinit var edtTienPhat: EditText
    private lateinit var edtTongTien: EditText
    private lateinit var btnXacNhanTra: Button
    private var chiTietPhieuMuonList: List<ChiTietPhieuMuon> = emptyList()
    private var tongTienSach: Double = 0.0
    private var maThuThu: Int = -1 // Biến để lưu maThuThu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_books)

        // Ánh xạ các view
        edtTenDocGia = findViewById(R.id.edt_tenDocGia)
        edtMaDocGia = findViewById(R.id.edt_maDocGia)
        edtMaPhieuMuon = findViewById(R.id.edt_maPhieuMuon)
        listSachMuon = findViewById(R.id.list_sachMuon)
        spinnerTrangThai = findViewById(R.id.spinner_trangThai)
        edtTienPhat = findViewById(R.id.edt_tienPhat)
        edtTongTien = findViewById(R.id.edt_tongTien)
        btnXacNhanTra = findViewById(R.id.btn_xacNhanTra)

        // Lấy maThuThu từ SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        maThuThu = sharedPreferences.getInt("maThuThu", -1)
        if (maThuThu == -1) {
            Toast.makeText(this, "Không tìm thấy mã thủ thư!", Toast.LENGTH_SHORT).show()
        }

        // Lấy dữ liệu từ Intent
        val maPhieu = intent.getStringExtra("MA_PHIEU")
        val tenDocGia = intent.getStringExtra("TEN_DOC_GIA")

        // Gán giá trị vào EditText
        edtTenDocGia.setText(tenDocGia ?: "N/A")
        edtMaPhieuMuon.setText(maPhieu ?: "N/A")

        // Lấy mã độc giả từ API
        if (tenDocGia != null) {
            fetchMaDocGia(tenDocGia)
        } else {
            Toast.makeText(this, "Không tìm thấy tên độc giả!", Toast.LENGTH_SHORT).show()
        }

        // Lấy danh sách sách từ API
        if (maPhieu != null) {
            fetchChiTietPhieuMuon(maPhieu)
        } else {
            Toast.makeText(this, "Không tìm thấy mã phiếu mượn!", Toast.LENGTH_SHORT).show()
        }

        // Thiết lập Spinner trạng thái sách
        val trangThaiOptions = arrayOf("Tốt", "Hỏng", "Mất")
        val trangThaiAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, trangThaiOptions)
        trangThaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTrangThai.adapter = trangThaiAdapter

        // Xử lý sự kiện khi chọn trạng thái sách
        spinnerTrangThai.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateTienPhatAndTongTien()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                updateTienPhatAndTongTien()
            }
        })

        // Xử lý sự kiện nút "Xác Nhận Trả"
        btnXacNhanTra.setOnClickListener {
            if (chiTietPhieuMuonList.isEmpty()) {
                Toast.makeText(this, "Không có sách để trả!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val trangThai = spinnerTrangThai.selectedItem.toString()
            val tienPhat = edtTienPhat.text.toString().toDoubleOrNull() ?: 0.0
            val tongTien = edtTongTien.text.toString().toDoubleOrNull() ?: 0.0
            val maPhieuMuon = edtMaPhieuMuon.text.toString().toIntOrNull() ?: -1

            // Kiểm tra maThuThu
            if (maThuThu == -1) {
                Toast.makeText(this, "Mã thủ thư không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển chiTietPhieuMuonList thành danhSachSachTra
            val danhSachSachTra = chiTietPhieuMuonList.map { chiTiet ->
                SachTra(
                    maSach = chiTiet.maSach,
                    soLuong = chiTiet.soLuong,
                    trangThaiSach = trangThai,
                    phiPhat = if (trangThai == "Tốt") 0.0 else tienPhat / chiTietPhieuMuonList.size // Chia đều tiền phạt cho mỗi sách
                )
            }

            // Lấy ngày hiện tại
            val ngayTra = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Tạo request
            val request = TraSachRequest(
                maPhieuMuon = maPhieuMuon,
                maThuThu = maThuThu, // Sử dụng maThuThu từ SharedPreferences
                ngayTra = ngayTra, // Ngày hiện tại
                danhSachSachTra = danhSachSachTra,
                tongTien = tongTien
            )

            // Gọi API để tạo phiếu trả
            createPhieuTra(request)
        }
    }

    private fun fetchMaDocGia(tenDocGia: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getMaDocGia(tenDocGia)
                if (response.isSuccessful) {
                    val docGia = response.body()
                    withContext(Dispatchers.Main) {
                        if (docGia != null) {
                            edtMaDocGia.setText(docGia.maDocGia.toString())
                            Toast.makeText(this@Return_booksActivity, "mã độc giả: ${docGia.maDocGia}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Return_booksActivity, "Không tìm thấy mã độc giả!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Return_booksActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Return_booksActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchChiTietPhieuMuon(maPhieu: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getChiTietPhieuMuonn(maPhieu)
                if (response.isSuccessful) {
                    chiTietPhieuMuonList = (response.body() ?: emptyList()) as List<ChiTietPhieuMuon>
                    withContext(Dispatchers.Main) {
                        if (chiTietPhieuMuonList.isNotEmpty()) {
                            val sachAdapter = ArrayAdapter(
                                this@Return_booksActivity,
                                android.R.layout.simple_list_item_1,
                                chiTietPhieuMuonList.map { "${it.tenSach} - Số lượng: ${it.soLuong} - Giá: ${it.gia}" }
                            )
                            listSachMuon.adapter = sachAdapter
                            tongTienSach = chiTietPhieuMuonList.sumOf { it.gia * it.soLuong }
                            updateTienPhatAndTongTien()
                        } else {
                            Toast.makeText(this@Return_booksActivity, "Không tìm thấy sách trong phiếu mượn!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Return_booksActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Return_booksActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTienPhatAndTongTien() {
        val trangThai = spinnerTrangThai.selectedItem.toString()
        val tienPhat = when (trangThai) {
            "Hỏng" -> 10000.0
            "Mất" -> 50000.0
            else -> 0.0 // "Tốt"
        }

        edtTienPhat.setText(tienPhat.toString())
        val tongTien = tongTienSach + tienPhat
        edtTongTien.setText(tongTien.toString())
    }

    private fun createPhieuTra(request: TraSachRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createPhieuTra(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val traSachResponse = response.body()
                        Toast.makeText(
                            this@Return_booksActivity,
                            traSachResponse?.message ?: "Trả sách thành công!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@Return_booksActivity,
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Return_booksActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}