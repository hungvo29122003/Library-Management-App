package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class PhieuMuon(
    @SerializedName("maPhieuMuon") val maPhieu: Int,
    @SerializedName("tenThuThu") val tenThuThu: String,
    @SerializedName("tenDocGia") val tenDocGia: String,
    @SerializedName("ngayMuon") val ngayMuon: String
)