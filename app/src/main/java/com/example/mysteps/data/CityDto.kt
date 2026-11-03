package com.example.mysteps.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    @SerialName("city_id") val cityId: Long? = null,
    @SerialName("name_city") val nameCity: String
)
