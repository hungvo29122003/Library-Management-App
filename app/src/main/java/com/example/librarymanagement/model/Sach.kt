package com.example.librarymanagement.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Sach(
    @SerializedName("maSach") val maSach: Int,
    @SerializedName("tenSach") val tenSach: String,
    @SerializedName("tacGia") val tacGia: String? = null,
    @SerializedName("tenTheLoai") val tenTheLoai: String? = null,
    @SerializedName("ngayXuatBan") val ngayXuatBan: String? = null,
    @SerializedName("soLuong") val soLuong: Int? = null,
    @SerializedName("gia") val gia: Double? = null,
    @SerializedName("tenKhu") val tenKhu: String? = null,
    @SerializedName("image") val image: String? = null,
    val trangThai: String? = "Chưa trả"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        maSach = parcel.readInt(),
        tenSach = parcel.readString() ?: "",
        tacGia = parcel.readString(),
        tenTheLoai = parcel.readString(),
        ngayXuatBan = parcel.readString(),
        soLuong = parcel.readInt().let { if (it == -1) null else it }, // -1 là giá trị flag cho null
        gia = parcel.readDouble().let { if (it == Double.MIN_VALUE) null else it }, // Giá trị đặc biệt cho null
        tenKhu = parcel.readString(),
        image = parcel.readString(),
        trangThai = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maSach)
        parcel.writeString(tenSach)
        parcel.writeString(tacGia)
        parcel.writeString(tenTheLoai)
        parcel.writeString(ngayXuatBan)
        parcel.writeInt(soLuong ?: -1) // Ghi -1 nếu null
        parcel.writeDouble(gia ?: Double.MIN_VALUE) // Ghi giá trị đặc biệt nếu null
        parcel.writeString(tenKhu)
        parcel.writeString(image)
        parcel.writeString(trangThai)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Sach> {
        override fun createFromParcel(parcel: Parcel): Sach {
            return Sach(parcel)
        }

        override fun newArray(size: Int): Array<Sach?> {
            return arrayOfNulls(size)
        }
    }
}