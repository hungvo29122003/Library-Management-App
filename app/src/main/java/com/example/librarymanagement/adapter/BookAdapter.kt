package com.example.librarymanagement

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagement.model.Sach

class BookAdapter(
    private var books: List<Sach>,
    private val onEditClick: (Sach) -> Unit, // Lambda cho sự kiện Edit
    private val onDeleteClick: (Sach) -> Unit // Lambda cho sự kiện Delete
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBookImage: ImageView = itemView.findViewById(R.id.image_book)
        val tvBookName: TextView = itemView.findViewById(R.id.tv_str_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_str_price)
        val tvQuantity: TextView = itemView.findViewById(R.id.tv_str_quantity)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_str_category)
        val tvArea: TextView = itemView.findViewById(R.id.tv_str_area)
        val ivMenuIcon: ImageView = itemView.findViewById(R.id.tv_icon_3_cham)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_rcv_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        // Hiển thị thông tin sách
        holder.tvBookName.text = book.tenSach
        holder.tvPrice.text = "${book.gia} VND"
        holder.tvQuantity.text = book.soLuong.toString()
        holder.tvCategory.text = book.tenTheLoai
        holder.tvArea.text = book.tenKhu

        // Hiển thị hình ảnh từ URL bằng Glide
        if (!book.image.isNullOrEmpty()) {
            val imageUrl = "http://10.11.88.167:3000${book.image}"
            Log.d("BookAdapter", "Loading image from URL: $imageUrl") // Log URL để kiểm tra
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.ivBookImage)
        } else {
            Log.d("BookAdapter", "Image URL is null or empty for book: ${book.tenSach}")
            holder.ivBookImage.setImageResource(R.drawable.img)
        }

        // Thêm sự kiện click vào item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("BOOK_KEY", book)
            context.startActivity(intent)
        }

        // Thêm sự kiện click vào icon 3 chấm để hiển thị PopupMenu
        holder.ivMenuIcon.setOnClickListener { view ->
            showPopupMenu(view, book)
        }
    }

    override fun getItemCount(): Int = books.size

    // Hàm hiển thị PopupMenu
    private fun showPopupMenu(view: View, book: Sach) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_book_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    onEditClick(book) // Gọi lambda function khi bấm Edit
                    true
                }
                R.id.action_delete -> {
                    onDeleteClick(book) // Gọi lambda function khi bấm Delete
                    true
                }
                else -> false
            }
        }

        // Hiển thị PopupMenu
        popupMenu.show()
    }
    // Hàm cập nhật danh sách sách
    fun updateBooks(newBooks: List<Sach>) {
        books = newBooks
        notifyDataSetChanged()
    }
}