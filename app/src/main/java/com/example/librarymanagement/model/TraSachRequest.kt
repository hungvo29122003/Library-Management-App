package com.example.librarymanagement.model

data class TraSachRequest(
    val maPhieuMuon: Int,
    val maThuThu: Int?, // Có thể null nếu không có thông tin thủ thư
    val ngayTra: String?, // Có thể null, backend sẽ dùng ngày hiện tại
    val danhSachSachTra: List<SachTra>,
    val tongTien: Double
)
