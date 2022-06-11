package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyTags(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Acc") var iDAcc: Int? = null,
    @SerializedName("iD_Tag") var iDTag: Int? = null,
    @SerializedName("account") var account: String? = null,
    @SerializedName("tag") var tag: Tag? = Tag()
): Parcelable
