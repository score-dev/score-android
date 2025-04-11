package com.team.score.API.neis

import com.team.score.API.neis.response.NeisSchoolResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NeisApiService {

    @GET("hub/schoolInfo")
    fun getSchoolList(
        @Query("KEY") apiKey: String,
        @Query("Type") type: String = "json",
        @Query("pIndex") pageIndex: Int = 1,
        @Query("pSize") pageSize: Int = 100,
        @Query("SCHUL_NM") schoolName: String
    ): Call<NeisSchoolResponse>
}
