package com.kiluss.bookrate.data.model

import com.google.gson.annotations.SerializedName

data class BookRate(
    @SerializedName("iD_Book" ) var iDBook  : Int?    = null,
    @SerializedName("rateAvg" ) var rateAvg : Double? = null,
    @SerializedName("time"    ) var time    : String? = null
)
