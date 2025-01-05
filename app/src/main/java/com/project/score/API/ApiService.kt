package com.project.score.API

import com.project.score.API.response.EmptyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // 소셜 로그인 인증 및 기존 회원 여부 확인
    @GET("score/auth/oauth")
    fun checkOauth(
        @Query("provider") provider: String,
        @Query("idToken") idToken: String
    ): Call<EmptyResponse>
}