package com.team.score.API.weather

import com.team.score.API.neis.response.NeisSchoolResponse
import com.team.score.API.weather.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // 현재 날씨 조회
    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String,
        @Query("lang") lang: String = "kr"
    ): Call<WeatherResponse>
}
