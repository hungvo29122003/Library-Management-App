package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class SachMuon(
    @SerializedName("maSach") val maSach: Int,
    @SerializedName("soLuong") val soLuong: Int
)
