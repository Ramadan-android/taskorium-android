package com.example.taskorium.di


import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.token
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("apikey", Constants.SUPABASE_ANON_KEY)
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader(
            "Prefer",
            "resolution=merge-duplicates"
        )

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}




