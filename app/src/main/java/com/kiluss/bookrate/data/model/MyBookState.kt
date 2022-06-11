package com.kiluss.bookrate.data.model

import com.google.gson.annotations.SerializedName

data class MyBookState(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("iD_Acc") var iDAcc: Int? = null,
    @SerializedName("iD_Book") var iDBook: Int? = null,
    @SerializedName("book") var book: BookModel? = null,
    @SerializedName("statusBook") var statusBook: Int? = null
)
