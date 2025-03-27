package com.example.librarymanagement.model

data class BookRequest(
    val tenSach: String,
    val tacGia: String,
    val maTheLoai: Int,
    val ngayXuatBan: String,
    val soLuong: Int,
    val gia: Double,
    val maKhu: Int,
    val image: String? // Chuỗi Base64 của hình ảnh, có thể null
)
