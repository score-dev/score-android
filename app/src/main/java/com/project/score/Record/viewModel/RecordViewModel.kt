package com.project.score.Record.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.score.API.ApiClient
import com.project.score.API.TokenManager
import com.project.score.API.response.login.UserInfoResponse
import com.project.score.API.response.record.FeedDetailResponse
import com.project.score.API.response.user.FeedListResponse
import com.project.score.API.response.user.PaginatedResponse
import com.project.score.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordViewModel: ViewModel() {
    var feedList = MutableLiveData<MutableList<FeedListResponse>>()
    var lastFeed: MutableLiveData<Boolean> = MutableLiveData()
    var firstFeed: MutableLiveData<Boolean> = MutableLiveData()

    var feedDetail: MutableLiveData<FeedDetailResponse?> = MutableLiveData()

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

    // 피드 상세 정보
    fun getFeedDetail(activity: MainActivity, feedId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getFeedDetail(tokenManager.getAccessToken().toString(), feedId).enqueue(object :
            Callback<FeedDetailResponse> {
            override fun onResponse(
                call: Call<FeedDetailResponse>,
                response: Response<FeedDetailResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: FeedDetailResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    feedDetail.value = result
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: FeedDetailResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<FeedDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}