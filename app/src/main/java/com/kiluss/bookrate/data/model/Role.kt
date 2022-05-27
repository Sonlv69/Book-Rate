package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Role(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("nameRole") var nameRole: String? = null,
    @SerializedName("accounts") var accounts: ArrayList<String> = arrayListOf()

) : Parcelable
