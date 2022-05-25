package com.kiluss.bookrate.network.api

import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.RegisterResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BookService {
    @Headers("Accept: application/json")
    @POST("UserLoginAndRegister/Login")
    fun login(
        @Body params: RequestBody
    ): Call<String>

    @POST("UserLoginAndRegister/Register")
    @FormUrlEncoded
    fun register(
        @Field("UserName") username: String,
        @Field("Password") password: String,
        @Field("ConfirmPassword") confirmPassword: String
    ): Call<RegisterResponse>
}
