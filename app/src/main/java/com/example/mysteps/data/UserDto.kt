package com.example.mysteps.data

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val users_id: String,
    val email: String,
    val password: String,
)
