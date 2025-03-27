package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class UpdateThuThuRequest(
    @SerializedName("tenThuThu") val tenThuThu: String,
    @SerializedName("cccd") val cccd: String,
    @SerializedName("diaChi") val diaChi: String,
    @SerializedName("namSinh") val namSinh: String,
    @SerializedName("sdt") val sdt: String,
    @SerializedName("avatar") val avatar: String? = null // avatar
)
