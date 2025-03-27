package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class DocGia(
    @SerializedName("maDocGia") val maDocGia: Int,
    @SerializedName("tenDocGia") val tenDocGia: String
)
