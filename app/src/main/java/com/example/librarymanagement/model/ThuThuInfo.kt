package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class ThuThuInfo(
    @SerializedName("tenThuThu") val tenThuThu: String,
    @SerializedName("cccd") val cccd: String,
    @SerializedName("diaChi") val diaChi: String,
    @SerializedName("sdt") val sdt: String,
    @SerializedName("namSinh") val namSinh: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("email") val email: String
)
