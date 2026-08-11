package com.example.taskorium.data.remote

import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.requestClasses.AuthRequest
import com.example.taskorium.data.remote.dto.RefreshTokenRequest
import com.example.taskorium.data.remote.dto.SupabaseAuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST(Constants.REGISTER_ENDPOINT)
    suspend fun register(
        @Body request: AuthRequest
    ): Response<SupabaseAuthResponse>

    @POST(Constants.LOGIN_ENDPOINT)
    suspend fun login(
        @Body request: AuthRequest
    ): Response<SupabaseAuthResponse>

    @POST(Constants.REFRESH_TOKEN_ENDPOINT)
    suspend fun refresh(
        @Body request: RefreshTokenRequest
    ): Response<SupabaseAuthResponse>
}

