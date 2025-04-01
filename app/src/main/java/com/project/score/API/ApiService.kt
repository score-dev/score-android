package com.project.score.API

import com.project.score.API.request.signUp.FcmRequest
import com.project.score.API.response.group.GroupRankingResponse
import com.project.score.API.response.home.HomeResponse
import com.project.score.API.response.login.LoginResponse
import com.project.score.API.response.login.UserInfoResponse
import com.project.score.API.response.user.BlockedMateListResponse
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

    // 신규 회원 정보 저장
    @Multipart
    @POST("score/public/onboarding/fin")
    fun signUp(
        @Part("userDto") userDto: RequestBody,
        @Part("schoolDto") schoolDto: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<LoginResponse>

    // FCM 토큰 저장
    @POST("{userId}/token")
    fun setFcmToken(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body body: FcmRequest
    ): Call<String>

    // 유저 정보
    @GET("score/user/info")
    fun getUserInfo(
        @Query("id") id: Int,
        @Header("Authorization") token: String
    ): Call<UserInfoResponse>

    // 홈 정보
    @POST("score/home")
    fun getHomeData(
        @Query("id") id: Int,
        @Header("Authorization") token: String
    ): Call<HomeResponse>

    // 바통 찌르기
    @POST("score/groups/mates/baton")
    fun batonGroupMember(
        @Query("senderId") senderId: Int,
        @Query("receiverId") receiverId: Int,
        @Header("Authorization") token: String
    ): Call<Boolean>

    // 그룹 생성
    @Multipart
    @POST("score/groups/create")
    fun createGroup(
        @Header("Authorization") token: String,
        @Part("groupCreateDto") groupCreateDto: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<Int>

    // 학교 그룹 랭킹
    @GET("score/ranking/school")
    fun getSchoolGroupRanking(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("localDate") localDateId: String?,
    ): Call<GroupRankingResponse>

    // 차단한 메이트 목록 조회
    @GET("score/friends/blocked/list")
    fun getBlockedMateList(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<List<BlockedMateListResponse>>
}