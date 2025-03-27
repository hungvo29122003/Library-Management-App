package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class ChiTietPhieuMuon(
    val danhSachSach: List<Sach>,
    @SerializedName("maChiTiet") val maChiTiet: Int,
    @SerializedName("tenSach") val tenSach: String,
    val maSach: Int,
    @SerializedName("soLuong") val soLuong: Int,
    @SerializedName("gia") val gia: Double
)
