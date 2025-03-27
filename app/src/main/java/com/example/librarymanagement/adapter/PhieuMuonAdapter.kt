package com.example.librarymanagement.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.Details_ReceiptActivity
import com.example.librarymanagement.R
import com.example.librarymanagement.model.PhieuMuon
import java.text.SimpleDateFormat
import java.util.Locale

class PhieuMuonAdapter(private val phieuMuonList: List<PhieuMuon>) :
    RecyclerView.Adapter<PhieuMuonAdapter.PhieuMuonViewHolder>() {

    class PhieuMuonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMaPhieu: TextView = itemView.findViewById(R.id.tv_maPhieu)
        val tvTenDocGia: TextView = itemView.findViewById(R.id.tv_tenDocGia)
        val tvNgayMuon: TextView = itemView.findViewById(R.id.tv_ngayMuon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhieuMuonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_rcv_receipt, parent, false)
        return PhieuMuonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhieuMuonViewHolder, position: Int) {
        val phieuMuon = phieuMuonList[position]
        holder.tvMaPhieu.text = "Mã Phiếu: ${phieuMuon.maPhieu}"
        holder.tvTenDocGia.text = "Tên Độc Giả: ${phieuMuon.tenDocGia ?: "N/A"}"

        // Định dạng ngày mượn
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = try {
            val date = inputFormat.parse(phieuMuon.ngayMuon)
            outputFormat.format(date)
        } catch (e: Exception) {
            phieuMuon.ngayMuon
        }
        holder.tvNgayMuon.text = "Ngày Mượn: $formattedDate"

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, Details_ReceiptActivity::class.java)
            intent.putExtra("MA_PHIEU", phieuMuon.maPhieu.toString())
            intent.putExtra("TEN_DOC_GIA", phieuMuon.tenDocGia)
            intent.putExtra("TEN_THU_THU", phieuMuon.tenThuThu)
            intent.putExtra("NGAY_MUON", formattedDate)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = phieuMuonList.size
}