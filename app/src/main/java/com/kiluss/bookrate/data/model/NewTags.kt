package com.kiluss.bookrate.data.model

import com.google.gson.annotations.SerializedName

data class NewTags(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("nameNewTag") var nameNewTag: String? = null,
    @SerializedName("iD_Propose") var iDPropose: Int? = null,
    @SerializedName("propose") var propose: String? = null
)
