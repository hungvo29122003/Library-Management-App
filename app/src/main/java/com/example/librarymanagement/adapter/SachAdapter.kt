package com.example.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.R
import com.example.librarymanagement.model.Sach

class SachAdapter(private val sachList: List<Sach>) :
    RecyclerView.Adapter<SachAdapter.SachViewHolder>() {

    class SachViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTenSach: TextView = itemView.findViewById(R.id.tv_tenSach)
        val tvSoLuong: TextView = itemView.findViewById(R.id.tv_soLuong)
        val tvTrangThai: TextView = itemView.findViewById(R.id.tv_trangThai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SachViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_chi_tiet_sach, parent, false)
        return SachViewHolder(view)
    }

    override fun onBindViewHolder(holder: SachViewHolder, position: Int) {
        val sach = sachList[position]
        // Thêm log để kiểm tra dữ liệu
        android.util.Log.d("SachAdapter", "Binding item at position $position: $sach")
        holder.tvTenSach.text = "Tên Sách: ${sach.tenSach}"
        holder.tvSoLuong.text = "Số Lượng: ${sach.soLuong}"
        holder.tvTrangThai.text = "Trạng Thái: ${sach.trangThai ?: "Chưa trả"}"
    }

    override fun getItemCount(): Int {
        // Thêm log để kiểm tra số lượng item
        android.util.Log.d("SachAdapter", "getItemCount: ${sachList.size}")
        return sachList.size
    }
}