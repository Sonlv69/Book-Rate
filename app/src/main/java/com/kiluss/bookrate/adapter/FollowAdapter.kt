package com.kiluss.bookrate.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.Account
import com.kiluss.bookrate.data.model.Follow
import com.kiluss.bookrate.data.model.FollowModel
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.databinding.ItemFollowBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.MessageFormat

class FollowAdapter(
    private val context: Context,
    private val followLists: List<FollowModel>,
    private val followAdapterAdapterInterface: FollowAdapterInterface,
    private val isFollowers: Boolean
) : RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    private val loginResponse = getLoginResponse(context)

    interface FollowAdapterInterface {
        fun onFollowClick(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.convertFollow(followLists[position])
    }

    override fun getItemCount(): Int {
        return followLists.size
    }

    private fun getLoginResponse(context: Context) : LoginResponse {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(context.getString(R.string.saved_login_account_key), "")
        return gson.fromJson(json, LoginResponse::class.java)
    }

    inner class ViewHolder(
        val binding: ItemFollowBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindView(person: Follow) {
            Log.e("followAdapter", person.toString())
            binding.ivAvatar.let {

            }
            binding.tvName.text = person.name.toString()
            person.picture?.let { binding.ivAvatar.setImageBitmap( base64ToBitmapDecode(it)) }
            binding.tvFollower.text = MessageFormat.format(
                context.getText(R.string.text_follower).toString(),
                person.followers?.toInt()
            )
            binding.tvName.setOnClickListener {
                person.id?.let { it1 -> followAdapterAdapterInterface.onFollowClick(it1) }
            }
            binding.ivAvatar.setOnClickListener {
                person.id?.let { it1 -> followAdapterAdapterInterface.onFollowClick(it1) }
            }
//            if (!person.isFollowing) {
//                binding.btnFollowState.background = context.getDrawable(R.drawable.button_bg_outlined)
//                binding.btnFollowState.text = "Follow"
//            } else {
//                binding.btnFollowState.background = context.getDrawable(R.drawable.button_bg)
//                binding.btnFollowState.text = "Following"
//            }
//            binding.btnFollowState.setOnClickListener {
//                followAdapterAdapterInterface.onFollowClick(adapterPosition, person)
//            }

        }

        fun convertFollow(person: FollowModel) {
            if (isFollowers) {
                person.iDFollower?.let { getAccountInfo(it) }
            } else {
                person.iDFollowing?.let { getAccountInfo(it) }
            }
        }

        private fun getAccountInfo(accountId: Int) {
            RetrofitClient.getInstance(context).getClientAuthorized(loginResponse.token.toString())
                .create(BookService::class.java).getAccountInfo(accountId.toString())
                .enqueue(object : Callback<Account?> {
                    override fun onResponse(
                        call: Call<Account?>,
                        response: Response<Account?>
                    ) {
                        when {
                            response.isSuccessful -> {
                                response.body()?. let {
                                    bindView(Follow(it.id, it.userName, it.picture, it.myFollowers?.size.toString()))
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<Account?>, t: Throwable) {

                    }
                })
        }

        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}
