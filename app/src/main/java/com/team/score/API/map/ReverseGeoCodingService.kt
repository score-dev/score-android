package com.team.score.API.map

import com.team.score.API.map.response.ReverseGeocodingResponse
import com.team.score.API.neis.response.NeisSchoolResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeoCodingService {
    @GET("map-reversegeocode/v2/gc")
    fun getAddress(
        @Query("coords") coords: String,
        @Query("orders") orders: String = "legalcode",
        @Query("output") output: String = "json"
    ): Call<ReverseGeocodingResponse>
}