package com.example.taskorium.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseAuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: SupabaseUserDto
)

@Serializable
data class SupabaseUserDto(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String
)
