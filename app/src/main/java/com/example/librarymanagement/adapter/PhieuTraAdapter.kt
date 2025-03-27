package com.example.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.R
import com.example.librarymanagement.model.PhieuTra
import java.text.SimpleDateFormat
import java.util.Locale

class PhieuTraAdapter(private val phieuTraList: List<PhieuTra>) :
    RecyclerView.Adapter<PhieuTraAdapter.PhieuTraViewHolder>() {

    class PhieuTraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMaPhieuTra: TextView = itemView.findViewById(R.id.tv_maPhieuTra)
        val tvMaPhieuMuon: TextView = itemView.findViewById(R.id.tv_maPhieuMuon)
        val tvTenThuThu: TextView = itemView.findViewById(R.id.tv_tenThuThu)
        val tvTenDocGia: TextView = itemView.findViewById(R.id.tv_tenDocGia) // Thêm TextView cho tenDocGia
        val tvNgayTra: TextView = itemView.findViewById(R.id.tv_ngayTra)
        val tvTongTien: TextView = itemView.findViewById(R.id.tv_tongTien)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhieuTraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_phieu_tra, parent, false)
        return PhieuTraViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhieuTraViewHolder, position: Int) {
        val phieuTra = phieuTraList[position]
        holder.tvMaPhieuTra.text = "Mã Phiếu Trả: ${phieuTra.maPhieuTra}"
        holder.tvMaPhieuMuon.text = "Mã Phiếu Mượn: ${phieuTra.maPhieuMuon}"
        holder.tvTenThuThu.text = "Thủ Thư: ${phieuTra.tenThuThu ?: "N/A"}"
        holder.tvTenDocGia.text = "Tên Độc Giả: ${phieuTra.tenDocGia ?: "N/A"}"

        // Định dạng ngày trả
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = try {
            val date = inputFormat.parse(phieuTra.ngayTra)
            outputFormat.format(date)
        } catch (e: Exception) {
            phieuTra.ngayTra
        }
        holder.tvNgayTra.text = "Ngày Trả: $formattedDate"

        holder.tvTongTien.text = "Tổng Tiền: ${phieuTra.tongTien}"
    }

    override fun getItemCount(): Int = phieuTraList.size
}