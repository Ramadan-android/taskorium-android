package com.example.taskorium.data.repository.mappers

import com.example.taskorium.data.remote.dto.SupabaseUserDto
import com.example.taskorium.domain.model.User


fun SupabaseUserDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
    )
}

fun User.toDto(): SupabaseUserDto {
    return SupabaseUserDto(
        id = this.id,
        email = this.email,
    )
}