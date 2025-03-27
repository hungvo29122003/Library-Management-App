package com.example.librarymanagement.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagement.BookDetailActivity
import com.example.librarymanagement.R
import com.example.librarymanagement.model.Sach

class BookSearchAdapter : RecyclerView.Adapter<BookSearchAdapter.BookViewHolder>() {
    private val bookList: MutableList<Sach> = mutableListOf()

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_str_name)
        val tvAuthor: TextView = itemView.findViewById(R.id.tv_str_author)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_str_price)
        val tvQuantity: TextView = itemView.findViewById(R.id.tv_str_quantity)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_str_category)
        val tvArea: TextView = itemView.findViewById(R.id.tv_str_area)
        val imageBook: ImageView = itemView.findViewById(R.id.image_book)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_search_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.tvName.text = book.tenSach
        holder.tvAuthor.text = book.tacGia
        holder.tvPrice.text = "${book.gia} VND"
        holder.tvQuantity.text = book.soLuong.toString()
        holder.tvCategory.text = book.tenTheLoai
        holder.tvArea.text = book.tenKhu

        // Hiển thị hình ảnh từ URL bằng Glide
        if (!book.image.isNullOrEmpty()) {
            val imageUrl = "http://10.11.88.167:3000${book.image}"
            Log.d("BookAdapter", "Loading image from URL: $imageUrl")
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.imageBook)
        } else {
            Log.d("BookAdapter", "Image URL is null or empty for book: ${book.tenSach}")
            holder.imageBook.setImageResource(R.drawable.img)
        }

        // Thêm sự kiện click vào item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("BOOK_KEY", book)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    // Hàm để cập nhật danh sách sách
    fun updateBooks(newBooks: List<Sach>) {
        bookList.clear()
        bookList.addAll(newBooks)
        notifyDataSetChanged()
    }
}