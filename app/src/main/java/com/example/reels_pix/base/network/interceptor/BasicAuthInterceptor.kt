package com.example.reels_pix.base.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Basic YOUR_TOKEN_HERE")
            .build()
        return chain.proceed(request)
    }
}
