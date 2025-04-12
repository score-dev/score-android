package com.team.score.API.weather

import com.team.score.API.weather.response.AirPollutionResponse
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

    // 현재 미세먼지 조회
    @GET("air_pollution")
    fun getCurrentAirPollutioin(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<AirPollutionResponse>
}
