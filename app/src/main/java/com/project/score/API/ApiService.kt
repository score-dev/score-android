package com.project.score.API

import com.project.score.API.request.signUp.FcmRequest
import com.project.score.API.response.login.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // 소셜 로그인 인증 및 기존 회원 여부 확인
    @POST("score/auth/oauth")
    fun checkOauth(
        @Query("provider") provider: String,
        @Body body: RequestBody
    ): Call<LoginResponse>

    // 닉네임 중복 검사
    @GET("score/public/{nickname}/exists")
    fun checkNickName(
        @Path("nickname") nickname: String
    ): Call<Int>

    // FCM 토큰 저장
    @POST("{userId}/token")
    fun setFcmToken(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body body: FcmRequest
    ): Call<String>
}