package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class DeleteAccountRequest(
    @SerializedName("tenDangNhap") val tenDangNhap: String
)
