package com.kiluss.bookrate.network.api

import com.kiluss.bookrate.data.model.*
import com.kiluss.bookrate.data.model.Tag
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
    ): Call<Account>

    @GET("Account/noPassword/{id}")
    fun getMyAccountInfo(
        @Path("id") id: String
    ): Call<Account>

    @Headers("Accept: text/plain")
    @PUT("Account/information/{id}")
    fun changeMyAccountInfo(
        @Path("id") id: String,
        @Body params: RequestBody
    ): Call<Account>

    @PUT("Account/password/{id}")
    fun changePassword(
        @Path("id") id: String,
        @Body params: RequestBody
    ): Call<Account>

    @GET("Book")
    fun getAllBooks(): Call<ArrayList<BookModel>>

    @GET("Book/{id}")
    fun getBookById(
        @Path("id") id: String
    ): Call<BookModel>

    @GET("Book/RateAvg/{id}")
    fun getBookRate(
        @Path("id") id: Int
    ): Call<BookRate>

    @POST("Book/CreateOrUpdateRate")
    fun createOrUpdateRate(
        @Body params: RequestBody
    ) : Call<Any>

    @GET("Author/{id}")
    fun getAuthorInfo(
        @Path("id") id: Int
    ): Call<Author>

    @GET("Publisher/{id}")
    fun getPublisherInfo(
        @Path("id") id: Int
    ): Call<Publisher>

    @GET("Tag/{id}")
    fun getTagInfo(
        @Path("id") id: Int
    ): Call<Tag>

}
