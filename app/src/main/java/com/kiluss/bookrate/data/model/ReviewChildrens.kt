package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewChildrens(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Acc") var iDAcc: Int? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("id_parent") var idParent: Int? = null,
    @SerializedName("account") var account: Account? = Account()
) : Parcelable
