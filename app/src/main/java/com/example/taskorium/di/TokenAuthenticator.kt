package com.example.taskorium.di

import android.util.Log
import com.example.taskorium.core.session.SessionManager
import com.example.taskorium.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepositoryProvider: Provider<AuthRepository>
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }
        synchronized(this){
            val currentToken = sessionManager.token
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")

            if (currentToken != null && currentToken != requestToken) {

                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = sessionManager.refreshToken ?: return null
            val accessToken =
                runBlocking {
                    authRepositoryProvider.get().refreshSession(refreshToken)
                } ?: return null

            return response.request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer $accessToken"
                )
                .build()
        }

    }
    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

}