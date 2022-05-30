package com.kiluss.bookrate.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("iD_Aut") var iDAut: Int? = null,
    @SerializedName("iD_Pub") var iDPub: Int? = null,
    @SerializedName("publishedYear") var publishedYear: Int? = null,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("reviews") var reviews: ArrayList<Reviews>? = arrayListOf(),
    @SerializedName("author") var author: Author? = Author(),
    @SerializedName("publisher") var publisher: Publisher? = Publisher(),
    @SerializedName("tags") var tags: ArrayList<Tags>? = arrayListOf()
) : Parcelable