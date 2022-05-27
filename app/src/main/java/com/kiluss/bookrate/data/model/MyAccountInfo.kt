package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyAccountInfo(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("isActive") var isActive: Boolean? = null,
    @SerializedName("fullName") var fullName: String? = null,
    @SerializedName("birthday") var birthday: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("iD_Role") var iDRole: Int? = null,
    @SerializedName("role") var role: Role = Role(),
    @SerializedName("myFollowings") var myFollowings: ArrayList<String> = arrayListOf(),
    @SerializedName("myFollowers") var myFollowers: ArrayList<String> = arrayListOf(),
    @SerializedName("myBooks") var myBooks: ArrayList<String> = arrayListOf(),
    @SerializedName("reviews") var reviews: ArrayList<String> = arrayListOf(),
    @SerializedName("proposes") var proposes: ArrayList<String> = arrayListOf()
) : Parcelable