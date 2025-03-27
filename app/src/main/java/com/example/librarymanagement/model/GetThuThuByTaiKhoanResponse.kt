package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class GetThuThuByTaiKhoanResponse(
    @SerializedName("maThuThu") val maThuThu: Int
)
