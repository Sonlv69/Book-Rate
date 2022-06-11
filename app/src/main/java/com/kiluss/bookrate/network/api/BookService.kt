package com.kiluss.bookrate.network.api

import com.kiluss.bookrate.data.model.*
import com.kiluss.bookrate.data.model.Tag
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BookService {

    //account
    @Headers("Accept: text/plain")
    @POST("Account")
    fun register(
        @Body params: RequestBody
    ): Call<Account>

    @GET("Account/noPassword/{id}")
    fun getAccountInfo(
        @Path("id") id: String
    ): Call<Account>

    @GET("Account/MyInforHasMyTag")
    fun getMyAccountInfo(): Call<AccountHasMytag>

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

    @POST("Account/Follow")
    fun postFollow(
        @Body params: RequestBody
    ): Call<Any>

    @HTTP(method = "DELETE", path = "Account/Follow", hasBody = true)
    fun deleteFollow(
        @Body params: RequestBody
    ): Call<Unit>

    @POST("Account/MyTag/{id}")
    fun postMyTag(
        @Path("id") id: Int
    ): Call<Any>

    @DELETE("Account/MyTag/{id}")
    fun deleteMyTag(
        @Path("id") id: Int
    ): Call<Unit>

    //author
    @GET("Author")
    fun getAllAuthor(): Call<ArrayList<Author>>

    @GET("Author/{id}")
    fun getAuthorInfo(
        @Path("id") id: Int
    ): Call<Author>

    //book
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
    ): Call<Any>

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

    @POST("Book/Propose")
    fun postRequestBook(
        @Body params: RequestBody
    ): Call<BookRequest>

    //publisher
    @GET("Publisher")
    fun getAllPublisher(): Call<ArrayList<Publisher>>

    @GET("Publisher/{id}")
    fun getPublisherInfo(
        @Path("id") id: Int
    ): Call<Publisher>

    //tag
    @GET("Tag/{id}")
    fun getTagInfo(
        @Path("id") id: Int
    ): Call<Tag>

    @GET("Tag")
    fun getAllTag(): Call<ArrayList<Tag>>

    //user
    @Headers("Accept: application/json")
    @POST("User/Login")
    fun login(
        @Body params: RequestBody
    ): Call<LoginResponse>

    @GET("User/MyBook")
    fun getMyBook(): Call<ArrayList<MyBookState>>

    @POST("User/MyBook")
    fun postMyBook(
        @Body params: RequestBody
    ): Call<MyBookState>

    @PUT("User/MyBook")
    fun putMyBook(
        @Body params: RequestBody
    ): Call<MyBookState>

    @DELETE("User/MyBookByIdBook/{id}")
    fun deleteMyBookById(
        @Path("id") id: Int
    ): Call<Unit>

    //Statistical
    @GET("Statistical/Review/10")
    fun getStatisticalReview(): Call<ArrayList<TopReview>>

    @GET("Statistical/Rate/10")
    fun getStatisticalRate(): Call<ArrayList<TopRateAvg>>

    @GET("Statistical/Readed/10")
    fun getStatisticalRead(): Call<ArrayList<TopRead>>
}
