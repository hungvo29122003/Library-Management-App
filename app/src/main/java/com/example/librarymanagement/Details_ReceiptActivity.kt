package com.example.librarymanagement

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.adapter.SachAdapter
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.ChiTietPhieuMuon
import com.example.librarymanagement.model.Sach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Details_ReceiptActivity : AppCompatActivity() {

    private lateinit var tvMaPhieu: TextView
    private lateinit var tvTenDocGia: TextView
    private lateinit var tvTenThuThu: TextView
    private lateinit var tvNgayMuon: TextView
    private lateinit var rcvChiTietSach: RecyclerView
    private lateinit var btnTraSach: AppCompatButton
    private var sachList: List<Sach>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_receipt)
        // Ánh xạ các view
        tvMaPhieu = findViewById(R.id.tv_maPhieu)
        tvTenDocGia = findViewById(R.id.tv_tenDocGia)
        tvTenThuThu = findViewById(R.id.tv_tenThuThu)
        tvNgayMuon = findViewById(R.id.tv_ngayMuon)
        rcvChiTietSach = findViewById(R.id.rcv_chiTietSach)
        btnTraSach = findViewById(R.id.btn_traSach)

        // Khởi tạo RecyclerView
        rcvChiTietSach.layoutManager = LinearLayoutManager(this)

        // Lấy thông tin từ Intent
        val maPhieu = intent.getStringExtra("MA_PHIEU")
        val tenDocGia = intent.getStringExtra("TEN_DOC_GIA")
        val tenThuThu = intent.getStringExtra("TEN_THU_THU")
        val ngayMuon = intent.getStringExtra("NGAY_MUON")

        // Hiển thị thông tin phiếu mượn
        tvMaPhieu.text = "Mã Phiếu: ${maPhieu ?: "N/A"}"
        tvTenDocGia.text = "Tên Độc Giả: ${tenDocGia ?: "N/A"}"
        tvTenThuThu.text = "Thủ Thư: ${tenThuThu ?: "N/A"}"
        tvNgayMuon.text = "Ngày Mượn: ${ngayMuon ?: "N/A"}"

        // Gọi API để lấy danh sách sách
        if (maPhieu != null) {
            fetchChiTietPhieuMuon(maPhieu)
        } else {
            Toast.makeText(this, "Không tìm thấy mã phiếu!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Xử lý sự kiện nút "Trả Sách"
        btnTraSach.setOnClickListener {
//            if (sachList == null || sachList!!.isEmpty()) {
//                Toast.makeText(this, "Không có sách để trả!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            // Truyền dữ liệu sang Return_booksActivity
            val intent = Intent(this, Return_booksActivity::class.java)
            intent.putExtra("MA_PHIEU", maPhieu)
            intent.putExtra("TEN_DOC_GIA", tenDocGia)
            intent.putExtra("TEN_THU_THU", tenThuThu)
            intent.putExtra("NGAY_MUON", ngayMuon)
//            intent.putExtra("SACH_LIST", ArrayList(sachList)) // Truyền danh sách sách
            startActivity(intent)
        }
    }

    private fun fetchChiTietPhieuMuon(maPhieu: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getChiTietPhieuMuon(maPhieu)
                if (response.isSuccessful) {
                    val sachList: List<Sach>? = response.body()
                    // Thêm log để kiểm tra dữ liệu
                    android.util.Log.d("Details_ReceiptActivity", "sachList: $sachList")
                    withContext(Dispatchers.Main) {
                        if (sachList != null) {
                            // Hiển thị danh sách sách trong RecyclerView
                            val sachAdapter = SachAdapter(sachList)
                            rcvChiTietSach.adapter = sachAdapter
                        } else {
                            Toast.makeText(this@Details_ReceiptActivity, "Không tìm thấy chi tiết phiếu mượn!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Details_ReceiptActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Details_ReceiptActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}