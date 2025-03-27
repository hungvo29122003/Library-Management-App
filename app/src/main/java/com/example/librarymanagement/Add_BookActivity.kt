package com.example.librarymanagement

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.Khu
import com.example.librarymanagement.model.Sach
import com.example.librarymanagement.model.TheLoai
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class Add_BookActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerZones: Spinner
    private lateinit var btnSelectImage: Button
    private lateinit var ivBookImage: ImageView
    private lateinit var etNameBook: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etPublishDate: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etPrice: EditText

    private var categoryList: List<TheLoai> = emptyList() // Lưu danh sách thể loại
    private var selectedMaTheLoai: Int? = null
    private var zoneList: List<Khu> = emptyList()
    private var selectedMaKhu: Int? = null
    private var selectedImageUri: Uri? = null // Lưu URI của hình ảnh được chọn

    // ActivityResultLauncher để yêu cầu quyền
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Quyền được cấp, mở thư viện ảnh
            openGallery()
        } else {
            // Quyền bị từ chối, hiển thị thông báo giải thích
            Toast.makeText(
                this,
                "Quyền truy cập hình ảnh bị từ chối! Vui lòng cấp quyền trong cài đặt để chọn ảnh.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ActivityResultLauncher để chọn ảnh từ thư viện
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it // Lưu URI của hình ảnh được chọn
            ivBookImage.setImageURI(it) // Gán hình ảnh vào ImageView
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_book)

        // Xử lý padding cho system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ các view
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerZones = findViewById(R.id.spinnerZones)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        ivBookImage = findViewById(R.id.ivBookImage)
        etNameBook = findViewById(R.id.etNameBook)
        etAuthor = findViewById(R.id.etAuthor)
        etPublishDate = findViewById(R.id.etPublishDate)
        etQuantity = findViewById(R.id.etQuantity)
        etPrice = findViewById(R.id.etPrice)

        val btnAddBook = findViewById<AppCompatButton>(R.id.btnAddBook)

        // Gọi API để lấy danh sách thể loại và khu
        fetchCategories()
        fetchZones()

        // Thiết lập sự kiện bấm nút "Select Image"
        btnSelectImage.setOnClickListener {
            // Kiểm tra quyền truy cập hình ảnh
            checkAndRequestPermission()
        }

        // Thiết lập sự kiện bấm nút "Add Book"
        btnAddBook.setOnClickListener {
            val nameBook = etNameBook.text.toString().trim()
            val author = etAuthor.text.toString().trim()
            val publishDate = etPublishDate.text.toString().trim()
            val quantityStr = etQuantity.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()

            // Kiểm tra dữ liệu đầu vào
            if (nameBook.isEmpty() || author.isEmpty() || publishDate.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedMaTheLoai == null) {
                Toast.makeText(this, "Vui lòng chọn một thể loại!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedMaKhu == null) {
                Toast.makeText(this, "Vui lòng chọn một khu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Vui lòng chọn một hình ảnh!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển đổi số lượng và giá thành kiểu số
            val quantity = quantityStr.toIntOrNull()
            val price = priceStr.toDoubleOrNull()

            if (quantity == null || price == null) {
                Toast.makeText(this, "Số lượng và giá phải là số hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gọi API để thêm sách
            addBookToServer(
                nameBook,
                author,
                publishDate,
                selectedMaTheLoai!!,
                quantity,
                price,
                selectedMaKhu!!,
                selectedImageUri!!
            )
        }
    }

    // Hàm chuyển Uri thành File để gửi dưới dạng Multipart
    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Hàm gọi API để thêm sách
    private fun addBookToServer(
        tenSach: String,
        tacGia: String,
        ngayXuatBan: String,
        maTheLoai: Int,
        soLuong: Int,
        gia: Double,
        maKhu: Int,
        imageUri: Uri
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chuyển các giá trị thành RequestBody
                val tenSachBody = tenSach.toRequestBody("text/plain".toMediaTypeOrNull())
                val tacGiaBody = tacGia.toRequestBody("text/plain".toMediaTypeOrNull())
                val maTheLoaiBody = maTheLoai.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val ngayXuatBanBody = ngayXuatBan.toRequestBody("text/plain".toMediaTypeOrNull())
                val soLuongBody = soLuong.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val giaBody = gia.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val maKhuBody = maKhu.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                // Chuyển Uri thành File và tạo MultipartBody.Part
                val imageFile = uriToFile(imageUri)
                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                // Gọi API
                val response = RetrofitClient.instance.addBook(
                    tenSachBody,
                    tacGiaBody,
                    maTheLoaiBody,
                    ngayXuatBanBody,
                    soLuongBody,
                    giaBody,
                    maKhuBody,
                    imagePart
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val message = responseBody?.get("message")?.toString() ?: "Thêm sách thành công!"
                        Toast.makeText(this@Add_BookActivity, message, Toast.LENGTH_LONG).show()
                        finish() // Đóng Activity sau khi thêm thành công
                    } else {
                        Toast.makeText(
                            this@Add_BookActivity,
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Add_BookActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Hàm kiểm tra và yêu cầu quyền phù hợp với phiên bản Android
    private fun checkAndRequestPermission() {
        val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33) trở lên: Yêu cầu quyền READ_MEDIA_IMAGES
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            // Android 12 trở xuống: Yêu cầu quyền READ_EXTERNAL_STORAGE
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permissionToRequest) == PackageManager.PERMISSION_GRANTED) {
            // Quyền đã được cấp, mở thư viện ảnh
            openGallery()
        } else {
            // Yêu cầu quyền
            requestPermissionLauncher.launch(permissionToRequest)
        }
    }

    // Hàm mở thư viện ảnh
    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun fetchCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllCategories()
                if (response.isSuccessful) {
                    categoryList = response.body() ?: emptyList() // Lưu danh sách thể loại
                    withContext(Dispatchers.Main) {
                        if (categoryList.isNotEmpty()) {
                            // Lấy danh sách tenTheLoai
                            val categoryNames = categoryList.map { it.tenTheLoai }
                            // Tạo ArrayAdapter để hiển thị danh sách tenTheLoai trong Spinner
                            val adapter = ArrayAdapter(
                                this@Add_BookActivity,
                                android.R.layout.simple_spinner_item,
                                categoryNames
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerCategory.adapter = adapter

                            // Thiết lập listener để lấy maTheLoai khi chọn tên thể loại
                            spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedCategoryName = categoryNames[position]
                                    val selectedCategory = categoryList.find { it.tenTheLoai == selectedCategoryName }
                                    selectedMaTheLoai = selectedCategory?.maTheLoai
                                    Toast.makeText(
                                        this@Add_BookActivity,
                                        "Mã Thể Loại: $selectedMaTheLoai",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    selectedMaTheLoai = null
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@Add_BookActivity,
                                "Không tìm thấy thể loại!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Add_BookActivity,
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Add_BookActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchZones() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllZones()
                if (response.isSuccessful) {
                    zoneList = response.body() ?: emptyList() // Lưu danh sách khu
                    withContext(Dispatchers.Main) {
                        if (zoneList.isNotEmpty()) {
                            // Lấy danh sách tenKhu
                            val zoneNames = zoneList.map { it.tenKhu }
                            // Tạo ArrayAdapter để hiển thị danh sách tenKhu trong Spinner
                            val adapter = ArrayAdapter(
                                this@Add_BookActivity,
                                android.R.layout.simple_spinner_item,
                                zoneNames
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerZones.adapter = adapter

                            // Thiết lập listener để lấy maKhu khi chọn tên khu
                            spinnerZones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedZoneName = zoneNames[position]
                                    val selectedZone = zoneList.find { it.tenKhu == selectedZoneName }
                                    selectedMaKhu = selectedZone?.maKhu
                                    Toast.makeText(
                                        this@Add_BookActivity,
                                        "Mã Khu: $selectedMaKhu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    selectedMaKhu = null
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@Add_BookActivity,
                                "Không tìm thấy khu!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Add_BookActivity,
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Add_BookActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Hàm để lấy maTheLoai của thể loại được chọn
    fun getSelectedMaTheLoai(): Int? {
        return selectedMaTheLoai
    }

    // Hàm để lấy maKhu của khu được chọn
    fun getSelectedMaKhu(): Int? {
        return selectedMaKhu
    }

    // Hàm để lấy URI của hình ảnh được chọn
    fun getSelectedImageUri(): Uri? {
        return selectedImageUri
    }
}