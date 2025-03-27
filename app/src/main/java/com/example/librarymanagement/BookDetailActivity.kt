package com.example.librarymanagement

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.librarymanagement.model.Sach

class BookDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var ivBookImage: ImageView
    private lateinit var tvBookName: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvPublishDate: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvZone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Ánh xạ các view bằng findViewById
        btnBack = findViewById(R.id.btnBack)
        ivBookImage = findViewById(R.id.ivBookImage)
        tvBookName = findViewById(R.id.tvBookName)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvPublishDate = findViewById(R.id.tvPublishDate)
        tvCategory = findViewById(R.id.tvCategory)
        tvQuantity = findViewById(R.id.tvQuantity)
        tvPrice = findViewById(R.id.tvPrice)
        tvZone = findViewById(R.id.tvZone)

        // Nhận dữ liệu sách từ Intent
        val book = intent.getParcelableExtra<Sach>("BOOK_KEY")

        // Hiển thị thông tin sách
        if (book != null) {
            tvBookName.text = book.tenSach
            tvAuthor.text = "Tác giả: ${book.tacGia}"
            tvPublishDate.text = "Ngày xuất bản: ${book.ngayXuatBan}"
            tvCategory.text = "Thể loại: ${book.tenTheLoai}"
            tvQuantity.text = "Số lượng: ${book.soLuong}"
            tvPrice.text = "Giá: ${book.gia} VND"
            tvZone.text = "Khu: ${book.tenKhu}"

            // Hiển thị hình ảnh từ URL bằng Glide
            if (!book.image.isNullOrEmpty()) {
                val imageUrl = "http://192.168.110.178:3000${book.image}"
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .into(ivBookImage)
            } else {
                ivBookImage.setImageResource(R.drawable.img)
            }
        }

        // Xử lý sự kiện click vào nút Quay lại
        btnBack.setOnClickListener {
            finish() // Kết thúc Activity và quay lại màn hình trước đó
        }
    }
}