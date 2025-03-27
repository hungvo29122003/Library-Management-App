package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class PhieuTra(
    @SerializedName("maPhieuTra") val maPhieuTra: Int,
    @SerializedName("maPhieuMuon") val maPhieuMuon: Int,
    @SerializedName("tenThuThu") val tenThuThu: String? = null,
    @SerializedName("tenDocGia") val tenDocGia: String? = null, // Thêm trường tenDocGia
    @SerializedName("ngayTra") val ngayTra: String,
    @SerializedName("tongTien") val tongTien: String
)