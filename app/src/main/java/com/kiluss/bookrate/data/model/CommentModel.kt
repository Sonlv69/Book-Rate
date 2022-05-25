package com.kiluss.bookrate.data.model

data class CommentModel(
    val avatarUrl: String,
    val commentName: String,
    val comment: String,
    var likeNumber: Int,
    var likeState: Boolean,
    val reply: List<CommentModel>
)
