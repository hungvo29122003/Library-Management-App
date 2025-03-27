package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class TheLoai(
    @SerializedName("maTheLoai") val maTheLoai: Int,
    @SerializedName("tenTheLoai") val tenTheLoai: String
)
