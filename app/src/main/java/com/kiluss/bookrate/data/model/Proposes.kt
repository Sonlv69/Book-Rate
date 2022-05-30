package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Proposes(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Propose") var iDPropose: Int? = null,
    @SerializedName("iD_Tag") var iDTag: Int? = null,
    @SerializedName("propose") var propose: String? = null,
    @SerializedName("tag") var tag: String? = null
): Parcelable
