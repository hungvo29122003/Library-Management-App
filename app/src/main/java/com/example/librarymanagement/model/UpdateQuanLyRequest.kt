package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class UpdateQuanLyRequest(
    @SerializedName("tenQuanLy") val tenQuanLy: String,
    @SerializedName("cccd") val cccd: String,
    @SerializedName("diaChi") val diaChi: String,
    @SerializedName("namSinh") val namSinh: String,
    @SerializedName("sdt") val sdt: String,
    @SerializedName("avatar") val avatar: String
)
