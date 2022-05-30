package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyFollowers(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Following") var iDFollowing: Int? = null,
    @SerializedName("iD_Follower") var iDFollower: Int? = null
) : Parcelable
