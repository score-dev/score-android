package com.project.score.Mypage.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.project.score.API.ApiClient
import com.project.score.API.TokenManager
import com.project.score.API.neis.response.SchoolDto
import com.project.score.API.response.login.LoginResponse
import com.project.score.API.response.login.UserInfoResponse
import com.project.score.API.response.user.BlockedMateListResponse
import com.project.score.API.response.user.FeedListResponse
import com.project.score.API.response.user.NotificationInfoResponse
import com.project.score.API.response.user.PaginatedResponse
import com.project.score.MainActivity
import com.project.score.Utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class MypageViewModel: ViewModel() {

    var userInfo: MutableLiveData<UserInfoResponse> = MutableLiveData()
    var notificationInfo: MutableLiveData<NotificationInfoResponse?> = MutableLiveData()
    var blockedMateList = MutableLiveData<MutableList<BlockedMateListResponse>>()
    var feedList = MutableLiveData<MutableList<FeedListResponse>>()
    var lastFeed: MutableLiveData<Boolean> = MutableLiveData()
    var firstFeed: MutableLiveData<Boolean> = MutableLiveData()

    // 유저 정보
    fun getUserInfo(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getUserInfo(tokenManager.getUserId(), tokenManager.getAccessToken().toString()).enqueue(object :
            Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: UserInfoResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    userInfo.value = result!!
                    MyApplication.userInfo = result
                    MyApplication.userNickname = result?.nickname.toString()
                    Log.d("##", "viewModel user info : ${MyApplication.userInfo}")
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: UserInfoResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 회원 정보 수정
    fun updateUserInfo(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        val gson = Gson()

        // JSON 변환 (Gson 사용)
        val userDtoJson = gson.toJson(MyApplication.userUpdateInfo?.userUpdateDto)

        // RequestBody 변환 (application/json 설정)
        val userDtoRequestBody = userDtoJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiClient.apiService.updateUserInfo(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), userDtoRequestBody, MyApplication.userUpdateImage).enqueue(object :
            Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: String? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    activity.fragmentManager?.popBackStack()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 피드 리스트 정보
    fun getFeedList(activity: Activity, currentPage: Int) {

        val tempFeedList = mutableListOf<FeedListResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getFeedList(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), tokenManager.getUserId(), currentPage).enqueue(object :
            Callback<PaginatedResponse<FeedListResponse>> {
            override fun onResponse(
                call: Call<PaginatedResponse<FeedListResponse>>,
                response: Response<PaginatedResponse<FeedListResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: PaginatedResponse<FeedListResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    lastFeed.value = (result?.last == true)
                    firstFeed.value = (result?.first == true)

                    val resultFeedList = result?.content ?: emptyList()

                    for (feed in resultFeedList) {
                        val feedItem = FeedListResponse(
                            feedId = feed.feedId,
                            uploaderNickname = feed.uploaderNickname ?: "",
                            uploaderProfileImgUrl = feed.uploaderProfileImgUrl ?: "",
                            feedImg = feed.feedImg ?: "",
                            startedAt = feed.startedAt ?: "",
                            completedAt = feed.completedAt ?: "",
                            location = feed.location ?: "",
                            weather = feed.weather ?: "",
                            temperature = feed.temperature ?: 0,
                            fineDust = feed.fineDust ?: "",
                            feeling = feed.feeling ?: "",
                            taggedNicknames = feed.taggedNicknames ?: emptyList(),
                            taggedProfileImgUrls = feed.taggedProfileImgUrls ?: emptyList()
                        )

                        tempFeedList.add(feedItem)
                    }

                    feedList.value = tempFeedList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: PaginatedResponse<FeedListResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<PaginatedResponse<FeedListResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 알림 수신 여부 설정 현황
    fun getNotificationInfo(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getNotificationInfo(tokenManager.getAccessToken().toString(), tokenManager.getUserId()).enqueue(object :
            Callback<NotificationInfoResponse> {
            override fun onResponse(
                call: Call<NotificationInfoResponse>,
                response: Response<NotificationInfoResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: NotificationInfoResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    notificationInfo.value = result
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: NotificationInfoResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<NotificationInfoResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 차단한 메이트 목록 조회
    fun getBlockedMateList(activity: Activity) {
        var tempBlockedMateList = mutableListOf<BlockedMateListResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getBlockedMateList(tokenManager.getAccessToken().toString(), tokenManager.getUserId()).enqueue(object :
            Callback<List<BlockedMateListResponse>> {
            override fun onResponse(
                call: Call<List<BlockedMateListResponse>>,
                response: Response<List<BlockedMateListResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<BlockedMateListResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val getBlockedMateList = result ?: emptyList()

                    for (mate in getBlockedMateList) {
                        var id = mate.id
                        var nickname = mate.nickname
                        var profile = mate.profileImgUrl

                        tempBlockedMateList.add(BlockedMateListResponse(id, nickname, profile))
                    }

                    blockedMateList.value = tempBlockedMateList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<BlockedMateListResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<List<BlockedMateListResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 차단한 메이트 차단 해제
    fun cancelBlockedMate(activity: Activity, mateId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.cancelBlockedMateList(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), mateId).enqueue(object :
            Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: String? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 회원탈퇴
    fun withdrawal(activity: MainActivity, result: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.withdrawal(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), result).enqueue(object :
            Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: String? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    activity.finish()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}