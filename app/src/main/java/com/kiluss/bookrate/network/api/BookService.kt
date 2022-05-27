package com.kiluss.bookrate.network.api

import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.MyAccountInfo
import com.kiluss.bookrate.data.model.RegisterResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BookService {
    @Headers("Accept: application/json")
    @POST("User/Login")
    fun login(
        @Body params: RequestBody
    ): Call<LoginResponse>

    @Headers("Accept: text/plain")
    @POST("Account")
    fun register(
        @Body params: RequestBody
    ): Call<RegisterResponse>

    @GET("Account/noPassword/{id}")
    fun getMyAccountInfo(
        @Path("id") id: String
    ): Call<MyAccountInfo>

    @Headers("Accept: text/plain")
    @PUT("Account/information/{id}")
    fun changeMyAccountInfo(
        @Path("id") id: String,
        @Body params: RequestBody
    ): Call<MyAccountInfo>

    @FormUrlEncoded
    @PUT("Account/password/{id}")
    fun changePassword(
        @Path("id") id: String,
        @Body params: RequestBody
    ): Call<MyAccountInfo>
}
