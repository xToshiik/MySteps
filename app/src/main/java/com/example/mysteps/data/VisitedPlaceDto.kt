package com.example.mysteps.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitedPlaceDto(
    val visit_id: Long? = null,
    val users_id: String,
    val title: String,
    val city_id: Long,
    val description: String?,
    val rating: Int,
    val visit_date: String,
    val image_url: String?,
    val latitude: Double,
    val longitude: Double,
    val created_at: String? = null
)
