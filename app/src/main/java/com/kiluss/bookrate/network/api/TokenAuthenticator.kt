package com.kiluss.bookrate.network.api

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val tokenType: String, private val accessToken: String) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request().header("Authorization") != null) {
            return null
        }
        return response.request().newBuilder().header("Authorization", "$tokenType $accessToken").build()
    }
}