package com.example.mysteps.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object GeocoderHelper {
    suspend fun getCityName(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lon&zoom=10&accept-language=ru")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "MyStepsApp/1.0 (student@mail.ru)")

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonObject = JSONObject(response)
                val address = jsonObject.optJSONObject("address")
                if (address != null) {
                    return@withContext address.optString("city").ifEmpty {
                        address.optString("town").ifEmpty {
                            address.optString("village").ifEmpty { null }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}