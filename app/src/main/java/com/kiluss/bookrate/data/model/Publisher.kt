package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Publisher(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("telephone") var telephone: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("website") var website: String? = null,
    @SerializedName("books") var books: ArrayList<BookModel>? = arrayListOf()
) : Parcelable
