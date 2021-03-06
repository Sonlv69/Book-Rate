package com.kiluss.bookrate.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("id_Role") var idRole: Int? = null,
    @SerializedName("token") var token: String? = null
)
