package com.example.taskorium.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
//data class CategoryResponse(
////    @SerialName("status") val status: String,
//    @SerialName("categories") val categories: List<CategoryDto>
//)

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)