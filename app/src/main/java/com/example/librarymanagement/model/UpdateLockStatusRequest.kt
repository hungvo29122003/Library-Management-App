package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class UpdateLockStatusRequest(
    @SerializedName("tenDangNhap") val tenDangNhap: String,
    @SerializedName("lockAccount") val lockAccount: Boolean
)
