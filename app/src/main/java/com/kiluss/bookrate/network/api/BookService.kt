package com.kiluss.bookrate.network.api

import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface BookService {
    @POST("User/authenticate")
    @FormUrlEncoded
    fun login(
        @Field("UserName") username: String,
        @Field("Password") password: String
    ): Call<LoginResponse>

    @POST("User/Register")
    @FormUrlEncoded
    fun register(
        @Field("UserName") username: String,
        @Field("Password") password: String,
        @Field("ConfirmPassword") confirmPassword: String
    ): Call<RegisterResponse>
}
