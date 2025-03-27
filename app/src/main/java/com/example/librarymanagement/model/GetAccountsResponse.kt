package com.example.librarymanagement.model

import com.google.gson.annotations.SerializedName

data class GetAccountsResponse(
    @SerializedName("accounts") val accounts: List<Account>
)