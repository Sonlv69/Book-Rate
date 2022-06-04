package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("fullName") var fullName: String? = null,
    @SerializedName("birthday") var birthday: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("iD_Role") var iDRole: Int? = null,
    @SerializedName("role") var role: Role? = Role(),
    @SerializedName("myFollowings") var myFollowings: ArrayList<MyFollowings>? = arrayListOf(),
    @SerializedName("myFollowers") var myFollowers: ArrayList<MyFollowers>? = arrayListOf(),
    @SerializedName("myBooks") var myBooks: ArrayList<MyBook>? = arrayListOf(),
    @SerializedName("proposes") var proposes: ArrayList<Proposes>? = arrayListOf()
) : Parcelable