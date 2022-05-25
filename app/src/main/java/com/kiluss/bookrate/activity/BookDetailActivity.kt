package com.kiluss.bookrate.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.CommentAdapter
import com.kiluss.bookrate.data.model.CommentModel
import com.kiluss.bookrate.utils.Const.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity(), CommentAdapter.CommentAdapterAdapterInterface {
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var listComments: List<CommentModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listComments = arrayListOf(
            CommentModel(
                "",
                "Son Llee",
                "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                25,
                true,
                arrayListOf()
            ),
            CommentModel(
                "",
                "kiluskfdjksjf dfhjkjsfhk",
                "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                15,
                false,
                arrayListOf()
            ),
            CommentModel(
                "",
                "Son Llee",
                "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                25,
                true,
                arrayListOf()
            ),
            CommentModel(
                "",
                "kiluskfdjksjf dfhjkjsfhk",
                "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                15,
                false,
                arrayListOf(
                    CommentModel(
                        "",
                        "Son Llee",
                        "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                        25,
                        true,
                        arrayListOf()
                    ),
                    CommentModel(
                        "",
                        "kiluskfdjksjf dfhjkjsfhk",
                        "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                        15,
                        false,
                        arrayListOf()
                    ),
                )
            ),CommentModel(
                "",
                "Son Llee",
                "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                25,
                true,
                arrayListOf()
            ),
            CommentModel(
                "",
                "kiluskfdjksjf dfhjkjsfhk",
                "Next up, let's create the ChildViewHolder and GroupViewHolder. These are both wrappers around regular ol' RecyclerView.ViewHolders so implement any view inflation and binding methods you may need.",
                15,
                false,
                arrayListOf()
            ),
        )
        Glide
            .with(applicationContext)
            .load("https://cdn.animenewsnetwork.com/thumbnails/fit600x1000/cms/feature/132523/hello.jpg")
            .override(700, 900)
            .placeholder(R.drawable.book_cover_default)
            .override(700, 900)
            .into(binding.ivCover)
        commentAdapter = CommentAdapter(this, listComments, this)
        binding.rcvComment.adapter = commentAdapter
        binding.rcvComment.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val message = intent.getStringExtra(EXTRA_MESSAGE)
        binding.tvBookTitle.text = message
        supportActionBar?.title = message
        binding.detailsBackButton.setOnClickListener {
            this.finish()
        }
    }

    override fun onReplyClick(adapterPosition: Int, category: CommentModel) {

    }

    override fun onLikeOnClick(adapterPosition: Int, category: CommentModel) {

    }

    override fun onLikeOffOnClick(adapterPosition: Int, category: CommentModel) {

    }

    override fun onSendReplyClick(adapterPosition: Int, category: CommentModel, comment: String) {

    }
}
