package com.example.taskorium.domain.repository

import com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.data.remote.dto.SupabaseAuthResponse
import com.example.taskorium.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<User>

    suspend fun register(email: String, password: String): NetworkResult<User>

    suspend fun refreshSession(refreshToken: String): String?

    fun getAuthToken(): Flow<String?>
    fun getRefreshToken(): Flow<String?>

    suspend fun logout()
}