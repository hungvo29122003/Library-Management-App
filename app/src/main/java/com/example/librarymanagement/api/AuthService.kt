package com.example.librarymanagement.api

import com.example.librarymanagement.model.BookRequest
import com.example.librarymanagement.model.BookResponse
import com.example.librarymanagement.model.ChiTietPhieuMuon
import com.example.librarymanagement.model.CreatePhieuMuonRequest
import com.example.librarymanagement.model.CreatePhieuMuonResponse
import com.example.librarymanagement.model.DeleteAccountRequest
import com.example.librarymanagement.model.DeleteAccountResponse
import com.example.librarymanagement.model.DocGia
import com.example.librarymanagement.model.GetAccountsResponse
import com.example.librarymanagement.model.GetBooksResponse
import com.example.librarymanagement.model.GetThuThuByTaiKhoanResponse
import com.example.librarymanagement.model.Khu
import com.example.librarymanagement.model.LoginRequest
import com.example.librarymanagement.model.LoginResponse
import com.example.librarymanagement.model.LogoutResponse
import com.example.librarymanagement.model.PhieuMuon
import com.example.librarymanagement.model.PhieuTra
import com.example.librarymanagement.model.QuanLyInfo
import com.example.librarymanagement.model.QuanLyResponse
import com.example.librarymanagement.model.RegisterRequest
import com.example.librarymanagement.model.RegisterResponse
import com.example.librarymanagement.model.Sach
import com.example.librarymanagement.model.TheLoai
import com.example.librarymanagement.model.ThuThuInfo
import com.example.librarymanagement.model.TraSachRequest
import com.example.librarymanagement.model.TraSachResponse
import com.example.librarymanagement.model.UpdateLockStatusRequest
import com.example.librarymanagement.model.UpdateLockStatusResponse
import com.example.librarymanagement.model.UpdateQuanLyRequest
import com.example.librarymanagement.model.UpdateThuThuRequest
import com.example.librarymanagement.model.UpdateThuThuResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthService {
    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/phieuMuon")
    suspend fun getAllPhieuMuon(): Response<List<PhieuMuon>>

    @GET("phieuMuon/chiTietPhieuMuon/{id}")
    suspend fun getChiTietPhieuMuon(@Path("id") id: String): Response<List<Sach>>
    @GET("/phieuTra")
    suspend fun getAllPhieuTra(): Response<List<PhieuTra>>
    @GET("/book")
    suspend fun getAllBooks(): Response<List<Sach>>
    @GET("/category")
    suspend fun getAllCategories(): Response<List<TheLoai>>
    @GET("/khu")
    suspend fun getAllZones(): Response<List<Khu>>
//    @POST("book")
//    suspend fun addBook(@Body book: BookRequest): Response<Map<String, Any>>
@Multipart
@POST("book")
suspend fun addBook(
    @Part("tenSach") tenSach: RequestBody,
    @Part("tacGia") tacGia: RequestBody,
    @Part("maTheLoai") maTheLoai: RequestBody,
    @Part("ngayXuatBan") ngayXuatBan: RequestBody,
    @Part("soLuong") soLuong: RequestBody,
    @Part("gia") gia: RequestBody,
    @Part("maKhu") maKhu: RequestBody,
    @Part image: MultipartBody.Part?
): Response<Map<String, Any>>

    @GET("thuThu/tai-khoan/{maTaiKhoan}")
    suspend fun getThuThuByTaiKhoan(
        @Path("maTaiKhoan") maTaiKhoan: Int
    ): Response<GetThuThuByTaiKhoanResponse>
    @PUT("thuThu/thongTin/{id}")
    suspend fun updateThuThu(
        @Path("id") id: Int,
        @Body request: UpdateThuThuRequest
    ): Response<UpdateThuThuResponse>
    @GET("thuThu/thongTin/{id}")
    suspend fun getInfomationThuThu(
        @Path("id") id: Int
    ): Response<ThuThuInfo>
    @GET("/auth/accounts")
    suspend fun getAccountsByRole(
        @Query("role") role: String
    ): Response<GetAccountsResponse>
    @PUT("/docGia/lock")
    suspend fun updateLockStatus(
        @Body request: UpdateLockStatusRequest
    ): Response<UpdateLockStatusResponse>
    @DELETE("auth/account/delete/{tenDangNhap}")
    suspend fun deleteAccount(
        @Path("tenDangNhap") tenDangNhap: String
    ): Response<DeleteAccountResponse>
    @POST("/auth/logout")
    suspend fun logout(): Response<LogoutResponse>
    @GET("docGia/tenDocGia")
    suspend fun getAllNameDocGia(): Response<List<DocGia>>

    @GET("book/tenSach")
    suspend fun getAllNameBooks(): Response<GetBooksResponse>

    @POST("/phieuMuon")
    suspend fun createPhieuMuon(@Body request: CreatePhieuMuonRequest): Response<CreatePhieuMuonResponse>
    @GET("docGia/maDocGia")
    suspend fun getMaDocGia(@Query("tenDocGia") tenDocGia: String): Response<DocGia>

    @GET("phieuMuon/chiTietPhieuMuon/{id}")
    suspend fun getChiTietPhieuMuonn(@Path("id") maPhieu: String): Response<List<ChiTietPhieuMuon>>
    @POST("/phieutra")
    suspend fun createPhieuTra(@Body request: TraSachRequest): Response<TraSachResponse>
    @GET("book/name")
    suspend fun findBookByName(@Query("tenSach") tenSach: String): Response<BookResponse>
    @GET("quanLy/tai-khoan/{maTaiKhoan}")
    suspend fun getQuanLyByTaiKhoan(@Path("maTaiKhoan") maTaiKhoan: Int): Response<QuanLyResponse>

    @PUT("/quanLy/thongTin/{id}")
    suspend fun updateManager(
        @Path("id") id: Int,
        @Body request: UpdateQuanLyRequest
    ): Response<Map<String, String>>

    @GET("/quanLy/thongTin/{id}")
    suspend fun getInfomationQuanLy(
        @Path("id") id: Int
    ): Response<QuanLyInfo>
    @DELETE("book/delete/{tenSach}")
    suspend fun deleteBook(
        @Path("tenSach") tenSach: String
    ): Response<DeleteAccountResponse>
//    @GET("/phieuTra")  // Đường dẫn API của bạn
//    suspend fun getAllPhieuTra(): Response<List<PhieuTra>>
}