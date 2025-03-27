package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class DeleteAccountResponse(
    @SerializedName("message") val message: String
)
