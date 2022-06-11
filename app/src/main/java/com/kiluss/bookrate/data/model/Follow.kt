package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Follow(
    var id: Int? = null,
    var name: String? = null,
    var picture: String? = null,
    var followers: String? = null,
) : Parcelable
