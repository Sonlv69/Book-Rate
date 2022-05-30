package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Author(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("stage_Name") var stageName: String? = null,
    @SerializedName("birthday") var birthday: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("books") var books: ArrayList<BookModel>? = arrayListOf(),
): Parcelable
