package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class GetBooksResponse(
    @SerializedName("books") val books: List<Sach>
)
