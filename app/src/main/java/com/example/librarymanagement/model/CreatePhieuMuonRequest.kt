package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class CreatePhieuMuonRequest(
    @SerializedName("maThuThu") val maThuThu: Int,
    @SerializedName("maDocGia") val maDocGia: Int,
    @SerializedName("ngayMuon") val ngayMuon: String,
    @SerializedName("danhSachSach") val danhSachSach: List<SachMuon>
)
