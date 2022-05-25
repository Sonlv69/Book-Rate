package com.kiluss.bookrate.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollowModel(
    val avatarUrl: String,
    var name: String,
    var follower: Int,
    var isFollowing: Boolean,
) : Parcelable
