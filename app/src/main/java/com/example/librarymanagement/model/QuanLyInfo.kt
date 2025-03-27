package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class QuanLyInfo(
    @SerializedName("maQuanLy") val maQuanLy: Int,
    @SerializedName("tenQuanLy") val tenQuanLy: String,
    @SerializedName("cccd") val cccd: String,
    @SerializedName("diaChi") val diaChi: String,
    @SerializedName("namSinh") val namSinh: String,
    @SerializedName("sdt") val sdt: String,
    @SerializedName("email") val email: String,
    @SerializedName("avatar") val avatar: String?
)
