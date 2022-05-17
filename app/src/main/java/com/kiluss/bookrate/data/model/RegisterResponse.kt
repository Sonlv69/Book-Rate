package com.kiluss.bookrate.data.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("isActive") var isActive: Boolean? = null,
    @SerializedName("fullName") var fullName: String? = null,
    @SerializedName("birthday") var birthday: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("iD_Role") var iDRole: Int? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("myFollowings") var myFollowings: ArrayList<String> = arrayListOf(),
    @SerializedName("myFollowers") var myFollowers: ArrayList<String> = arrayListOf(),
    @SerializedName("myBooks") var myBooks: ArrayList<String> = arrayListOf(),
    @SerializedName("reviews") var reviews: ArrayList<String> = arrayListOf(),
    @SerializedName("proposes") var proposes: ArrayList<String> = arrayListOf()
)
