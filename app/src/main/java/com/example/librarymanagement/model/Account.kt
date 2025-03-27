package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("maTaiKhoan") val maTaiKhoan: Int,
    @SerializedName("tenDangNhap") val tenDangNhap: String,
    @SerializedName("matKhau") val matKhau: String,
    @SerializedName("email") val email: String,
    @SerializedName("vaiTro") val vaiTro: String,
    @SerializedName("trangThai") val trangThai: String,
    @SerializedName("lockAccount") val lockAccount: Boolean?
)