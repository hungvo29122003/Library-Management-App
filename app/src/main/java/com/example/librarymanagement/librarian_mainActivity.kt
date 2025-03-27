package com.example.librarymanagement
import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.adapter.PhieuMuonAdapter
import com.example.librarymanagement.adapter.PhieuTraAdapter
import com.example.librarymanagement.api.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class librarian_mainActivity : AppCompatActivity() {
    private lateinit var imageViewOptions: ImageView
    private lateinit var rcvReceipt: RecyclerView
    private lateinit var phieuMuonAdapter: PhieuMuonAdapter
    private lateinit var rcvLoanSlip: RecyclerView
    private lateinit var phieuTraAdapter: PhieuTraAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_librarian_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rcvReceipt = findViewById(R.id.rcv_receipt)
        val fragmentContainer = findViewById<FragmentContainerView>(R.id.fragment_container)
        val dashboardContainer = findViewById<View>(R.id.dashboard_container)
        // Ánh xạ ImageView
        imageViewOptions = findViewById(R.id.imageView_options)

        // Xử lý sự kiện bấm vào ImageView để hiển thị PopupMenu
        imageViewOptions.setOnClickListener { view ->
            showPopupMenu(view)
        }
        rcvReceipt.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcvLoanSlip = findViewById(R.id.rcv_loan_slip)
        rcvLoanSlip.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        rcvLoanSlip.clearItemDecorations() // Xóa ItemDecoration cũ (nếu có)
//        rcvLoanSlip.addItemDecoration(VerticalSpaceItemDecoration(4))
        bottomNavigationView = findViewById(R.id.bottomNView1)
        // Đặt menu item "Home" được chọn mặc định
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
                        .replace(R.id.fragment_container, SearchBookragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    // Hiển thị ProfileFragment
                    dashboardContainer.visibility = View.GONE
                    fragmentContainer.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }


        // Gọi API để lấy danh sách phiếu mượn
        fetchPhieuMuon()
        fetchPhieuTra()
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.options_lib, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Loan_slip -> {
                    // Mở LoanSlipActivity khi chọn "Mượn sách"
                    val intent = Intent(this, receiptActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.report -> {
                    // Mở ReportActivity khi chọn "Báo cáo"
                    val intent = Intent(this, Lib_reportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.traSach ->{
                    // Mở TraSachActivity khi chọn "Trả sách"
                    val intent = Intent(this, Return_booksActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    private fun fetchPhieuMuon() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllPhieuMuon()
                if (response.isSuccessful) {
                    val phieuMuonList = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        // Cập nhật RecyclerView
                        phieuMuonAdapter = PhieuMuonAdapter(phieuMuonList)
                        rcvReceipt.adapter = phieuMuonAdapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@librarian_mainActivity,
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@librarian_mainActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun fetchPhieuTra() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllPhieuTra()
                if (response.isSuccessful) {
                    val phieuTraList = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        // Cập nhật RecyclerView
                        phieuTraAdapter = PhieuTraAdapter(phieuTraList)
                        rcvLoanSlip.adapter = phieuTraAdapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@librarian_mainActivity,
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@librarian_mainActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchPhieuTra()
        fetchPhieuMuon()
    }
}