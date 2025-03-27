package com.example.librarymanagement

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.CreatePhieuMuonRequest
import com.example.librarymanagement.model.DocGia
import com.example.librarymanagement.model.Sach
import com.example.librarymanagement.model.SachMuon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class receiptActivity : AppCompatActivity() {
    private lateinit var spinnerDocGia: Spinner
    private lateinit var edtMaDocGia: EditText
    private lateinit var spinnerSach: Spinner
    private lateinit var btnThemSach: Button
    private lateinit var listSachDaChon: ListView
    private lateinit var btnXoaSach: Button
    private lateinit var btnXacNhanMuon: Button
    private lateinit var sharedPreferences: SharedPreferences
    private var docGiaList: List<DocGia> = emptyList()
    private var sachList: List<Sach> = emptyList()
    private val sachDaChonList = mutableListOf<SachMuon>()
    private lateinit var sachDaChonAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_receipt)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerDocGia = findViewById(R.id.spinner_docGia)
        edtMaDocGia = findViewById(R.id.edt_maDocGia)
        spinnerSach = findViewById(R.id.spinner_sach)
        btnThemSach = findViewById(R.id.btn_themSach)
        listSachDaChon = findViewById(R.id.list_sachDaChon)
        btnXoaSach = findViewById(R.id.btn_xoaSach)
        btnXacNhanMuon = findViewById(R.id.btn_xacNhanMuon)

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Thiết lập ListView cho danh sách sách đã chọn
        sachDaChonAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, mutableListOf<String>())
        listSachDaChon.adapter = sachDaChonAdapter
        listSachDaChon.choiceMode = ListView.CHOICE_MODE_SINGLE

        // Thêm sự kiện khi bấm vào một mục trong ListView
        listSachDaChon.setOnItemClickListener { parent, view, position, id ->
            listSachDaChon.setItemChecked(position, true) // Đánh dấu mục được chọn
            Toast.makeText(this, "Đã chọn: ${sachDaChonAdapter.getItem(position)}", Toast.LENGTH_SHORT).show()
        }

        // Lấy danh sách độc giả
        fetchDocGia()

        // Lấy danh sách sách
        fetchBooks()

        // Xử lý sự kiện bấm nút "Thêm Sách"
        btnThemSach.setOnClickListener {
            themSach()
        }

        // Xử lý sự kiện bấm nút "Xóa Sách"
        btnXoaSach.setOnClickListener {
            xoaSach()
        }

        // Xử lý sự kiện bấm nút "Xác Nhận Mượn"
        btnXacNhanMuon.setOnClickListener {
            xacNhanMuon()
        }
    }

    private fun fetchDocGia() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllNameDocGia()
                if (response.isSuccessful) {
                    docGiaList = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        if (docGiaList.isEmpty()) {
                            Toast.makeText(this@receiptActivity, "Không có độc giả nào!", Toast.LENGTH_SHORT).show()
                            return@withContext
                        }

                        val docGiaNames = docGiaList.map { it.tenDocGia }
                        val adapter = ArrayAdapter(this@receiptActivity, android.R.layout.simple_spinner_item, docGiaNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerDocGia.adapter = adapter

                        // Xử lý khi chọn độc giả
                        spinnerDocGia.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                                if (position >= 0 && position < docGiaList.size) {
                                    val selectedDocGia = docGiaList[position]
                                    edtMaDocGia.setText(selectedDocGia.maDocGia.toString())
                                    Toast.makeText(
                                        this@receiptActivity,
                                        "Đã chọn: ${selectedDocGia.tenDocGia} - Mã: ${selectedDocGia.maDocGia}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    edtMaDocGia.setText("")
                                    Toast.makeText(this@receiptActivity, "Vị trí không hợp lệ!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                                edtMaDocGia.setText("")
                                Toast.makeText(this@receiptActivity, "Chưa chọn độc giả!", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@receiptActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@receiptActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchBooks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllNameBooks()
                if (response.isSuccessful) {
                    sachList = response.body()?.books ?: emptyList()
                    withContext(Dispatchers.Main) {
                        val sachNames = sachList.map { it.tenSach }
                        val adapter = ArrayAdapter(this@receiptActivity, android.R.layout.simple_spinner_item, sachNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerSach.adapter = adapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@receiptActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@receiptActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun themSach() {
        val selectedSachPosition = spinnerSach.selectedItemPosition
        if (selectedSachPosition < 0 || selectedSachPosition >= sachList.size) {
            Toast.makeText(this, "Vui lòng chọn sách!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedSach = sachList[selectedSachPosition]
        val sachMuon = SachMuon(maSach = selectedSach.maSach, soLuong = 1)

        // Kiểm tra xem sách đã có trong danh sách chưa
        val existingSach = sachDaChonList.find { it.maSach == sachMuon.maSach }
        if (existingSach != null) {
            Toast.makeText(this, "Sách đã có trong danh sách!", Toast.LENGTH_SHORT).show()
            return
        }

        // Thêm sách vào danh sách
        sachDaChonList.add(sachMuon)
        sachDaChonAdapter.clear()
        sachDaChonAdapter.addAll(sachDaChonList.map { "${sachList.find { s -> s.maSach == it.maSach }?.tenSach} - Số lượng: ${it.soLuong}" })
        sachDaChonAdapter.notifyDataSetChanged()
    }

    private fun xoaSach() {
        val selectedPosition = listSachDaChon.checkedItemPosition
        if (selectedPosition == ListView.INVALID_POSITION) {
            Toast.makeText(this, "Vui lòng chọn một sách trong danh sách để xóa!", Toast.LENGTH_SHORT).show()
            return
        }

        // Xóa sách khỏi danh sách
        val removedSach = sachDaChonList.removeAt(selectedPosition)
        sachDaChonAdapter.clear()
        sachDaChonAdapter.addAll(sachDaChonList.map { "${sachList.find { s -> s.maSach == it.maSach }?.tenSach} - Số lượng: ${it.soLuong}" })
        sachDaChonAdapter.notifyDataSetChanged()

        // Thông báo xóa thành công
        val tenSach = sachList.find { it.maSach == removedSach.maSach }?.tenSach
        Toast.makeText(this, "Đã xóa sách: $tenSach", Toast.LENGTH_SHORT).show()
    }

    private fun xacNhanMuon() {
        val maDocGiaStr = edtMaDocGia.text.toString()
        if (maDocGiaStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn độc giả!", Toast.LENGTH_SHORT).show()
            return
        }

        if (sachDaChonList.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất một sách!", Toast.LENGTH_SHORT).show()
            return
        }

        val maDocGia = maDocGiaStr.toIntOrNull()
        if (maDocGia == null) {
            Toast.makeText(this, "Mã độc giả không hợp lệ!", Toast.LENGTH_SHORT).show()
            return
        }

        val maThuThu = sharedPreferences.getInt("maThuThu", -1)
        if (maThuThu == -1) {
            Toast.makeText(this, "Không tìm thấy mã thủ thư!", Toast.LENGTH_SHORT).show()
            return
        }

        // Lấy ngày hiện tại
        val ngayMuon = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Tạo request
        val request = CreatePhieuMuonRequest(
            maThuThu = maThuThu,
            maDocGia = maDocGia,
            ngayMuon = ngayMuon,
            danhSachSach = sachDaChonList
        )

        // Gọi API createPhieuMuon
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createPhieuMuon(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val phieuMuonResponse = response.body()
                        Toast.makeText(
                            this@receiptActivity,
                            phieuMuonResponse?.message ?: "Tạo phiếu mượn thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: response.message()
                        Toast.makeText(
                            this@receiptActivity,
                            "Lỗi: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@receiptActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}