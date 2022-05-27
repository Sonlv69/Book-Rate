package com.kiluss.bookrate.network.api

import android.content.Context
import com.kiluss.bookrate.utils.Const.Companion.API_URL
import com.kiluss.bookrate.utils.Const.Companion.BEARER
import com.kiluss.bookrate.utils.SingletonHolder
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class RetrofitClient private constructor(private val context: Context) {
    companion object : SingletonHolder<RetrofitClient, Context>(::RetrofitClient)
    fun getClientUnAuthorize(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(unSafeOkHttpClient().build())
            .build()
    }

    fun getClientAuthorized(token: String): Retrofit {
        val protocols: ArrayList<Protocol?> = object : ArrayList<Protocol?>() {
            init {
                add(Protocol.HTTP_1_1) // <-- The only protocol used
                //add(Protocol.HTTP_2)
            }
        }
        return Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                unSafeOkHttpClient()
//                    .connectTimeout(20L, TimeUnit.SECONDS)
//                    .writeTimeout  (20L, TimeUnit.SECONDS)
//                    .readTimeout   (20L, TimeUnit.SECONDS)
//                    .protocols(protocols)
                    .authenticator(TokenAuthenticator(BEARER, token))
                    .addInterceptor(ResponseHeaderInterceptor(context)).build()
            )
            .build()
    }

    private fun unSafeOkHttpClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts.first() as X509TrustManager
                )
                okHttpClient.hostnameVerifier { _, _ -> true }
            }

            return okHttpClient
        } catch (e: Exception) {
            return okHttpClient
        }
    }
}
