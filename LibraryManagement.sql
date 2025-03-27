CREATE DATABASE LibraryManagement;
USE LibraryManagement;

CREATE TABLE TaiKhoan (
   maTaiKhoan INT AUTO_INCREMENT PRIMARY KEY,
   tenDangNhap VARCHAR(255) NOT NULL,
   matKhau VARCHAR(255) NOT NULL,
   email VARCHAR(255) UNIQUE NOT NULL,
   vaiTro ENUM('QuanLy', 'ThuThu', 'DocGia') NOT NULL DEFAULT 'DocGia'
);

CREATE TABLE QuanLy (
    maQuanLy INT AUTO_INCREMENT PRIMARY KEY,
    maTaiKhoan INT UNIQUE,
    tenQuanLy VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL,    
    cccd CHAR(12) UNIQUE NOT NULL,
    diaChi VARCHAR(255) CHARACTER SET UTF8MB4,
    sdt VARCHAR(15),
    email VARCHAR(255) UNIQUE NOT NULL,
    namSinh DATE,
    avatar VARCHAR(255),
    FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan) ON DELETE CASCADE
);

CREATE TABLE ThuThu (
    maThuThu INT AUTO_INCREMENT PRIMARY KEY,
    maTaiKhoan INT UNIQUE,
    tenThuThu VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL,
    cccd CHAR(12) UNIQUE NOT NULL,
    diaChi VARCHAR(255) CHARACTER SET UTF8MB4,
    sdt VARCHAR(15),
    email VARCHAR(255) UNIQUE NOT NULL,
    namSinh DATE,
    avatar VARCHAR(255),
    FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan) ON DELETE CASCADE
);

CREATE TABLE DocGia (
    maDocGia INT AUTO_INCREMENT PRIMARY KEY,
    maTaiKhoan INT UNIQUE,
    tenDocGia VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL,
    cccd CHAR(12) UNIQUE NOT NULL,
    namSinh DATE,
    sdt VARCHAR(15),
    email VARCHAR(255) UNIQUE NOT NULL,
    ngayGiaNhap DATE,
    avatar VARCHAR(255),
    lockAccount BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan) ON DELETE CASCADE
);

CREATE TABLE TheLoai (
    maTheLoai INT AUTO_INCREMENT PRIMARY KEY,
    tenTheLoai VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL
);

CREATE TABLE TrangThai (
    maTrangThai INT AUTO_INCREMENT PRIMARY KEY,
    tenTrangThai VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL
);

CREATE TABLE Khu (
    maKhu INT AUTO_INCREMENT PRIMARY KEY,
    tenKhu VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL,
    moTa TEXT CHARACTER SET UTF8MB4
);

CREATE TABLE Sach (
    maSach INT AUTO_INCREMENT PRIMARY KEY,
    tenSach VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL,
    tacGia VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL,
    maTheLoai INT,
    ngayXuatBan DATE,
    maTrangThai INT,
    soLuong INT DEFAULT 1,
    maKhu INT,
    image VARCHAR(255),
    FOREIGN KEY (maTheLoai) REFERENCES TheLoai(maTheLoai) ON DELETE CASCADE,
    FOREIGN KEY (maTrangThai) REFERENCES TrangThai(maTrangThai) ON DELETE SET NULL,
    FOREIGN KEY (maKhu) REFERENCES Khu(maKhu) ON DELETE SET NULL
);

CREATE TABLE BaoCaoThongKe (
    maBaoCao INT AUTO_INCREMENT PRIMARY KEY,
    tenBaoCao VARCHAR(255) CHARACTER SET UTF8MB4 NOT NULL,
    maQuanLy INT,
    noiDungThongKe TEXT CHARACTER SET UTF8MB4,
    ngayLap DATE,
    FOREIGN KEY (maQuanLy) REFERENCES QuanLy(maQuanLy) ON DELETE CASCADE
);

CREATE TABLE PhieuMuon (
    maPhieu INT AUTO_INCREMENT PRIMARY KEY,
    maThuThu INT,
    maDocGia INT,
    ngayMuon DATE NOT NULL,
    FOREIGN KEY (maThuThu) REFERENCES ThuThu(maThuThu) ON DELETE CASCADE,
    FOREIGN KEY (maDocGia) REFERENCES DocGia(maDocGia) ON DELETE CASCADE
);
CREATE TABLE PhieuTra (
    maPhieu INT AUTO_INCREMENT PRIMARY KEY,
    maPhieuMuon INT UNIQUE, -- Phiếu Trả gắn với một Phiếu Mượn
    maThuThu INT,
    maDocGia INT,
    ngayTra DATE NOT NULL,
    tongTienPhat DECIMAL(10,2) DEFAULT 0, -- Tổng tiền phạt nếu có
    FOREIGN KEY (maPhieuMuon) REFERENCES PhieuMuon(maPhieu) ON DELETE CASCADE,
    FOREIGN KEY (maThuThu) REFERENCES ThuThu(maThuThu) ON DELETE CASCADE,
    FOREIGN KEY (maDocGia) REFERENCES DocGia(maDocGia) ON DELETE CASCADE
);

CREATE TABLE ChiTietPhieuMuon (
    maChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    maPhieu INT,
    maSach INT,
    soLuong INT DEFAULT 1,
    FOREIGN KEY (maPhieu) REFERENCES PhieuMuon(maPhieu) ON DELETE CASCADE,
    FOREIGN KEY (maSach) REFERENCES Sach(maSach) ON DELETE CASCADE
);


CREATE TABLE ChiTietPhieuTra (
    maChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    maPhieu INT,
    maSach INT,
    soLuong INT DEFAULT 1,
    trangThaiSach ENUM('BinhThuong', 'MatSach', 'Hong', 'TreHan') DEFAULT 'BinhThuong',
    phiPhat DECIMAL(10,2) DEFAULT 0, -- Phí phạt nếu có
    FOREIGN KEY (maPhieu) REFERENCES PhieuTra(maPhieu) ON DELETE CASCADE,
    FOREIGN KEY (maSach) REFERENCES Sach(maSach) ON DELETE CASCADE
);

CREATE TABLE ViPham (
    maViPham INT AUTO_INCREMENT PRIMARY KEY,
    maDocGia INT,
    loaiViPham TEXT CHARACTER SET UTF8MB4 NOT NULL,
    ngayViPham DATE,
    FOREIGN KEY (maDocGia) REFERENCES DocGia(maDocGia) ON DELETE CASCADE
);

-- Thêm dữ liệu vào bảng TaiKhoan
INSERT INTO TaiKhoan (tenDangNhap, matKhau, email, vaiTro) VALUES
('admin', 'admin123', 'admin@gmail.com', 'QuanLy'),
('thu_thu1', 'thuThu123', 'thuthu1@gmail.com', 'ThuThu'),
('thu_thu2', 'thuThu456', 'thuthu2@gmail.com', 'ThuThu'),
('doc_gia1', 'docGia123', 'docgia1@gmail.com', 'DocGia'),
('doc_gia2', 'docGia456', 'docgia2@gmail.com', 'DocGia');


INSERT INTO QuanLy (maTaiKhoan, tenQuanLy, cccd, diaChi, sdt, email, namSinh, avatar) VALUES
(1, 'Nguyễn Văn A', '012345678901', 'Hà Nội', '0987654321', 'admin@gmail.com', '1985-05-12', 'avatar1.jpg');

INSERT INTO ThuThu (maTaiKhoan, tenThuThu, cccd, diaChi, sdt, email, namSinh, avatar) VALUES
(2, 'Lê Thị B', '123456789012', 'Hồ Chí Minh', '0912345678', 'thuthu1@gmail.com', '1990-02-14', 'avatar2.jpg'),
(3, 'Phạm Văn C', '234567890123', 'Đà Nẵng', '0934567890', 'thuthu2@gmail.com', '1992-06-20', 'avatar3.jpg');

INSERT INTO DocGia (maTaiKhoan, tenDocGia, cccd, namSinh, sdt, email, ngayGiaNhap, avatar, lockAccount) VALUES
(4, 'Trần Minh D', '345678901234', '2000-07-08', '0961234567', 'docgia1@gmail.com', '2023-01-10', 'avatar4.jpg', FALSE),
(5, 'Hoàng Anh E', '456789012345', '1998-09-15', '0976543210', 'docgia2@gmail.com', '2023-02-20', 'avatar5.jpg', FALSE);

INSERT INTO TheLoai (tenTheLoai) VALUES
('Khoa học viễn tưởng'),
('Tiểu thuyết'),
('Lịch sử'),
('Tâm lý học');

INSERT INTO TrangThai (tenTrangThai) VALUES
('Còn sách'),
('Hết sách'),
('Đang mượn'),
('Hư hỏng');

INSERT INTO Khu (tenKhu, moTa) VALUES
('A1', 'Khu vực sách khoa học viễn tưởng'),
('B2', 'Khu vực tiểu thuyết'),
('C3', 'Khu vực sách lịch sử'),
('D4', 'Khu vực sách tâm lý học');

INSERT INTO Sach (tenSach, tacGia, maTheLoai, ngayXuatBan, maTrangThai, soLuong, maKhu, image) VALUES
('Dune', 'Frank Herbert', 1, '1965-08-01', 1, 5, 1, 'dune.jpg'),
('1984', 'George Orwell', 2, '1949-06-08', 1, 3, 2, '1984.jpg'),
('Sapiens', 'Yuval Noah Harari', 3, '2011-01-01', 1, 4, 3, 'sapiens.jpg'),
('Tư duy nhanh và chậm', 'Daniel Kahneman', 4, '2011-10-25', 1, 2, 4, 'thinking_fast_and_slow.jpg');

-- Thêm dữ liệu vào bảng BaoCaoThongKe
INSERT INTO BaoCaoThongKe (tenBaoCao, maQuanLy, noiDungThongKe, ngayLap) VALUES
('Báo cáo sách mượn tháng 3', 1, 'Tổng số sách mượn: 10, Sách bị mất: 1, Trả trễ hạn: 2.', '2024-03-20'),
('Báo cáo tình trạng sách', 1, 'Sách hư hỏng: 3, Sách đang được mượn: 5.', '2024-03-22'),
('Báo cáo vi phạm độc giả', 1, 'Tổng số vi phạm: 2, Độc giả vi phạm: 2.', '2024-03-25');


INSERT INTO PhieuMuon (maThuThu, maDocGia, ngayMuon) VALUES
(1, 1, '2024-03-01'),
(2, 2, '2024-03-02');

INSERT INTO ChiTietPhieuMuon (maPhieu, maSach, soLuong) VALUES
(1, 1, 1),  -- DocGia 1 mượn sách Dune
(1, 2, 1),  -- DocGia 1 mượn sách 1984
(2, 3, 1),  -- DocGia 2 mượn sách Sapiens
(2, 4, 1);  -- DocGia 2 mượn sách Tư duy nhanh và chậm


INSERT INTO PhieuTra (maPhieuMuon, maThuThu, maDocGia, ngayTra, tongTienPhat) VALUES
(1, 1, 1, '2024-03-15', 50000),  -- DocGia 1 trả sách, có phạt 50k
(2, 2, 2, '2024-03-16', 20000);  -- DocGia 2 trả sách, có phạt 20k

INSERT INTO ChiTietPhieuTra (maPhieu, maSach, soLuong, trangThaiSach, phiPhat) VALUES
(1, 1, 1, 'Hong', 50000),  -- Sách Dune bị hỏng, phạt 50k
(1, 2, 1, 'BinhThuong', 0),  -- Sách 1984 bình thường
(2, 3, 1, 'TreHan', 20000),  -- Sách Sapiens bị trễ hạn, phạt 20k
(2, 4, 1, 'BinhThuong', 0);  -- Sách Tư duy nhanh và chậm bình thường


INSERT INTO ViPham (maDocGia, loaiViPham, ngayViPham) VALUES
(1, 'Làm hỏng sách Dune', '2024-03-15'),
(2, 'Trả sách Sapiens trễ hạn', '2024-03-16');

