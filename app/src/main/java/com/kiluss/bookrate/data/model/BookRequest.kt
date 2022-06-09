package com.kiluss.bookrate.data.model

import com.google.gson.annotations.SerializedName

data class BookRequest(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("bookName") var bookName: String? = null,
    @SerializedName("iD_Aut") var iDAut: String? = null,
    @SerializedName("iD_Pub") var iDPub: String? = null,
    @SerializedName("iD_Acc_Request") var iDAccRequest: Int? = null,
    @SerializedName("publishedYear") var publishedYear: Int? = null,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("newAut") var newAut: String? = null,
    @SerializedName("newPub") var newPub: String? = null,
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("author") var author: Author? = Author(),
    @SerializedName("publisher") var publisher: Publisher? = Publisher(),
    @SerializedName("accountRequest") var accountRequest: Account? = Account(),
    @SerializedName("tags") var tags: ArrayList<Tags> = arrayListOf(),
    @SerializedName("newTags") var newTags: ArrayList<NewTags> = arrayListOf()
)
