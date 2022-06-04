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
    fun getAccountInfo(
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
    fun getAllBooks(@Query("page") page: Int): Call<ArrayList<BookModel?>>

    @GET("Book/{id}")
    fun getBookById(
        @Path("id") id: String
    ): Call<BookModel>

    @GET("Book/RateAvg/{id}")
    fun getBookRate(
        @Path("id") id: Int
    ): Call<BookRate>

    @POST("Book/Review")
    fun createReview(
        @Body params: RequestBody
    ) : Call<Any>

    @PUT("Book/Review/{id}")
    fun putReview(
        @Path("id") id: Int,
        @Body params: RequestBody
    ): Call<Any>

    @DELETE("Book/Review/{id}")
    fun deleteReview(
        @Path("id") id: Int
    ): Call<Unit>

    @POST("Book/Review/Reply")
    fun postReply(
        @Body params: RequestBody
    ): Call<Any>

    @PUT("Book/Review/Reply/{id}")
    fun putReply(
        @Path("id") id: Int,
        @Body params: RequestBody
    ): Call<Any>

    @DELETE("Book/Review/Reply/{id}")
    fun deleteReply(
        @Path("id") id: Int
    ): Call<Unit>

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
