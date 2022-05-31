package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tags(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Book") var iDBook: Int? = null,
    @SerializedName("iD_Tag") var iDTag: Int? = null,
    @SerializedName("book") var book: BookModel? = BookModel(),
    @SerializedName("tag") var tag: Tag? = Tag()
): Parcelable
