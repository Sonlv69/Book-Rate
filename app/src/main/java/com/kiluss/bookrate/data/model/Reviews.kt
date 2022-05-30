package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reviews(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Acc") var iDAcc: Int? = null,
    @SerializedName("iD_Book") var iDBook: Int? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("book") var book: String? = null,
    @SerializedName("reviewChildrens") var reviewChildrens: ArrayList<ReviewChildrens>? = arrayListOf()
) : Parcelable
