package com.example.librarymanagement.model

data class LoginResponse(
    val message: String,
    val accessToken: String,
    val User: User
)
