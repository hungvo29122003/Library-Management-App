package com.example.librarymanagement.model

data class RegisterRequest(
    val tenDangNhap: String,
    val matKhau: String,
    val email: String,
    val vaiTro: String
)
