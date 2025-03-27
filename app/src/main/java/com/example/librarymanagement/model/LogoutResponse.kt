package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("message") val message: String
)
