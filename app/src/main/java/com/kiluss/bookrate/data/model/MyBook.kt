package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyBook(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Acc") var iDAcc: Int? = null,
    @SerializedName("iD_Book") var iDBook: Int? = null,
    @SerializedName("acc") var acc: Account? = Account(),
    @SerializedName("book") var book: String? = null,
    @SerializedName("status") var status: Boolean? = null
): Parcelable
