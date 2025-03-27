package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class Khu(
    @SerializedName("maKhu") val maKhu: Int,
    @SerializedName("tenKhu") val tenKhu: String
)
