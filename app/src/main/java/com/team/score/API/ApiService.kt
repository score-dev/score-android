package com.team.score.API

import com.team.score.API.request.signUp.FcmRequest
import com.team.score.API.response.PagingResponse
import com.team.score.API.response.group.GroupDetailResponse
import com.team.score.API.response.group.GroupMateResponse
import com.team.score.API.response.group.GroupRankingResponse
import com.team.score.API.response.group.GroupUnexercisedMateResponse
import com.team.score.API.response.group.GroupInfoResponse
import com.team.score.API.response.group.SchoolGroupRankingResponse
import com.team.score.API.response.group.SearchGroupResponse
import com.team.score.API.response.home.HomeResponse
import com.team.score.API.response.login.LoginResponse
import com.team.score.API.response.login.UserInfoResponse
import com.team.score.API.response.record.FriendResponse
import com.team.score.API.response.record.FeedDetailResponse
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.API.response.user.BlockedMateListResponse
import com.team.score.API.response.user.ExerciseCalendarResponse
import com.team.score.API.response.user.FeedListResponse
import com.team.score.API.response.user.NotificationInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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
        @Part file: MultipartBody.Part?
    ): Call<LoginResponse>

    // FCM 토큰 저장
    @POST("{userId}/token")
    fun setFcmToken(
        @Path("userId") id: Int,
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

    // 내 그룹 리스트 조회
    @GET("score/groups/all")
    fun getMyGroupList(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<List<GroupInfoResponse>>

    // 그룹 상세 조회
    @GET("score/groups/info")
    fun getGroupDetail(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("groupId") groupId: Int
    ): Call<GroupDetailResponse>

    // 그룹 생성
    @Multipart
    @POST("score/groups/create")
    fun createGroup(
        @Header("Authorization") token: String,
        @Part("groupCreateDto") groupCreateDto: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<Int>

    // 그룹 가입 신청
    @GET("score/groups/join/request")
    fun participateGroup(
        @Header("Authorization") token: String,
        @Query("groupId") groupId: Int,
        @Query("userId") userId: Int
    ): Call<String>

    // 그룹 가입 신청
    @GET("score/groups/join/verify")
    fun verifyGroupPassword(
        @Header("Authorization") token: String,
        @Query("input") input: String,
        @Query("groupId") groupId: Int
    ): Call<Boolean>

    // 학교 그룹 랭킹
    @GET("score/ranking/school")
    fun getSchoolGroupRanking(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("localDate") localDateId: String?,
    ): Call<SchoolGroupRankingResponse>

    // 학교 그룹 검색
    @GET("score/school/search")
    fun searchSchoolGroup(
        @Header("Authorization") token: String,
        @Query("schoolId") schoolId: Int,
        @Query("keyword") keyword: String?,
    ): Call<SearchGroupResponse>

    // 학교 추천 그룹
    @GET("score/school/recommend/recent")
    fun getRecommendGroup(
        @Header("Authorization") token: String,
        @Query("schoolCode") schoolCode: String
    ): Call<List<GroupInfoResponse>>

    // 그룹 내 랭킹 조회
    @GET("score/ranking/group")
    fun getGroupRanking(
        @Header("Authorization") token: String,
        @Query("groupId") groupId: Int,
        @Query("localDate") localDate: String?,
    ): Call<GroupRankingResponse>

    // 그룹 피드 목록 조회
    @GET("score/groups/exercise/list")
    fun getGroupFeedList(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("groupId") groupId: Int,
        @Query("page") page: Int
    ): Call<PagingResponse<FeedListResponse>>

    @GET("score/exercise/calendar")
    fun getMateExerciseCalendar(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Call<List<ExerciseCalendarResponse>>

    // 그룹 내 메이트 조회
    @GET("score/groups/mates/list")
    fun getGroupMate(
        @Header("Authorization") token: String,
        @Query("groupId") groupId: Int
    ): Call<List<GroupMateResponse>>

    // 그룹 내 운동 쉰 메이트 조회
    @GET("score/groups/mates/nonExercised")
    fun getGroupUnexercisedMate(
        @Header("Authorization") token: String,
        @Query("groupId") groupId: Int,
        @Query("userId") userId: Int
    ): Call<List<GroupUnexercisedMateResponse>>

    // 피드 리스트
    @GET("score/exercise/list")
    fun getFeedList(
        @Header("Authorization") token: String,
        @Query("id1") id1: Int,
        @Query("id2") id2: Int,
        @Query("page") page: Int
    ): Call<PagingResponse<FeedListResponse>>

    // 피드 상세정보 조회
    @GET("score/exercise/read")
    fun getFeedDetail(
        @Header("Authorization") token: String,
        @Query("feedId") feedId: Int,
    ): Call<FeedDetailResponse>

    // 피드 감정 표현 조회
    @GET("score/exercise/emotion/list/all")
    fun getFeedEmotion(
        @Header("Authorization") token: String,
        @Query("feedId") feedId: Int,
    ): Call<List<FeedEmotionResponse>>

    // 피드 감정 표현 추가
    @POST("score/exercise/emotion")
    fun setFeedEmotion(
        @Header("Authorization") token: String,
        @Query("agentId") agentId: Int,
        @Query("feedId") feedId: Int,
        @Query("type") type: String,
    ): Call<String>

    // 회원 정보 수정
    @Multipart
    @PATCH("score/user/update/{userId}")
    fun updateUserInfo(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Part("userUpdateDto") userUpdateDto: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<String>

    // 알림 수신 여부 설정 현황
    @GET("score/user/info/notification")
    fun getNotificationInfo(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<NotificationInfoResponse>

    // 알림 수신 여부 설정 수정
    @PUT("score/user/setting/notification")
    fun updateNotificationInfo(
        @Header("Authorization") token: String,
        @Body request: NotificationInfoResponse
    ): Call<String>

    // 함께 운동한 친구 검색
    @GET("score/exercise/friends")
    fun getSearchExerciseFriend(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("nickname") nickname: String
    ): Call<List<FriendResponse>>

    // 친구 목록 조회
    @GET("score/friends/list")
    fun getFriendList(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("page") page: Int
    ): Call<PagingResponse<FriendResponse>>

    // 친구 추가
    @POST("score/friends/new/{id1}/{id2}")
    fun addNewFriends(
        @Header("Authorization") token: String,
        @Path("id1") id1: Int,
        @Path("id2") id2: Int
    ): Call<String>

    // 피드 정보 저장
    @Multipart
    @POST("score/exercise/walking/save")
    fun uploadFeed(
        @Header("Authorization") token: String,
        @Part("walkingDto") walkingDto: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<String>

    // 차단한 메이트 목록 조회
    @GET("score/friends/blocked/list")
    fun getBlockedMateList(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<List<BlockedMateListResponse>>

    // 차단한 메이트 차단 해제
    @PUT("score/friends/blocked/list")
    fun cancelBlockedMateList(
        @Header("Authorization") token: String,
        @Query("id1") id1: Int, //친구 차단 해제를 요청한 유저의 고유 번호
        @Query("id2") id2: Int //차단에서 해제될 유저의 고유 번호
    ): Call<String>

    // 회원탈퇴
    @DELETE("score/user/withdrawal")
    fun withdrawal(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("reason") reason: String
    ): Call<String>
}