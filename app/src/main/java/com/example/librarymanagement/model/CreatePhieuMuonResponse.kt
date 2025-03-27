package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class CreatePhieuMuonResponse(
    @SerializedName("message") val message: String,
    @SerializedName("maPhieuMuon") val maPhieuMuon: Int
)
