package com.team.score.Record.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.team.score.API.ApiClient
import com.team.score.API.TokenManager
import com.team.score.API.TokenUtil.refreshToken
import com.team.score.API.map.ReverseGeoCodingClient
import com.team.score.API.map.response.ReverseGeocodingResponse
import com.team.score.API.response.PagingResponse
import com.team.score.API.response.group.GroupInfoResponse
import com.team.score.API.response.record.FeedDetailResponse
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.API.response.record.FriendResponse
import com.team.score.API.response.user.ExerciseCalendarResponse
import com.team.score.API.response.user.FeedListResponse
import com.team.score.API.weather.WeatherApiClient
import com.team.score.API.weather.response.AirPollutionResponse
import com.team.score.API.weather.response.WeatherResponse
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.RecordFeedCompleteFragment
import com.team.score.Utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


class RecordViewModel: ViewModel() {
    var feedList = MutableLiveData<MutableList<FeedListResponse>>()
    var lastFeed: MutableLiveData<Boolean> = MutableLiveData()
    var firstFeed: MutableLiveData<Boolean> = MutableLiveData()

    var exerciseList = MutableLiveData<MutableSet<LocalDate>>()

    var feedDetail: MutableLiveData<FeedDetailResponse?> = MutableLiveData()
    var feedEmotion: MutableLiveData<MutableList<FeedEmotionResponse>?> = MutableLiveData()

    var searchFriendList = MutableLiveData<MutableList<FriendResponse>?>()
    var friendList = MutableLiveData<MutableList<FriendResponse>>()
    var lastFriend: MutableLiveData<Boolean> = MutableLiveData()
    var firstFriend: MutableLiveData<Boolean> = MutableLiveData()

    // 피드 리스트 정보
    fun getFeedList(activity: Activity, userId: Int, currentPage: Int) {

        val tempFeedList = mutableListOf<FeedListResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getFeedList(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), userId, currentPage).enqueue(object :
            Callback<PagingResponse<FeedListResponse>> {
            override fun onResponse(
                call: Call<PagingResponse<FeedListResponse>>,
                response: Response<PagingResponse<FeedListResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: PagingResponse<FeedListResponse>? = response.body()
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
                    var result: PagingResponse<FeedListResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { getFeedList(activity, userId, currentPage) },
                                onFailure = { activity.finish() }
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PagingResponse<FeedListResponse>>, t: Throwable) {
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

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { getFeedDetail(activity, feedId) },
                                onFailure = { activity.finish() }
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<FeedDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 피드 감정 표현 정보
    fun getFeedEmotion(activity: MainActivity, feedId: Int) {

        val tempFeedEmotion = mutableListOf<FeedEmotionResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getFeedEmotion(tokenManager.getAccessToken().toString(), feedId).enqueue(object :
            Callback<List<FeedEmotionResponse>> {
            override fun onResponse(
                call: Call<List<FeedEmotionResponse>>,
                response: Response<List<FeedEmotionResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<FeedEmotionResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultFeedEmotionList = result ?: emptyList()

                    for (feed in resultFeedEmotionList) {
                        val feedEmotionList = FeedEmotionResponse(
                            feed.agentId,
                            feed.agentProfileImgUrl,
                            feed.agentNickname,
                            feed.emotionType,
                            feed.reactedAt
                        )

                        tempFeedEmotion.add(feedEmotionList)
                    }

                    feedEmotion.value = tempFeedEmotion
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<FeedEmotionResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { getFeedEmotion(activity, feedId) },
                                onFailure = { activity.finish() }
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<FeedEmotionResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 피드 감정표현 추가
    fun setFeedEmotion(activity: MainActivity, feedId: Int, type: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.setFeedEmotion(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), feedId, type).enqueue(object :
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

                    getFeedEmotion(activity, feedId)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { setFeedEmotion(activity, feedId, type) },
                                onFailure = { activity.finish() }
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 피드 캘린더 정보 조회
    fun getExerciseCalendar(activity: Activity, userId: Int, year: Int, month: Int) {

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getMateExerciseCalendar(tokenManager.getAccessToken().toString(), userId, year, month).enqueue(object :
            Callback<List<ExerciseCalendarResponse>> {
            override fun onResponse(
                call: Call<List<ExerciseCalendarResponse>>,
                response: Response<List<ExerciseCalendarResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<ExerciseCalendarResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val tempExerciseSet = mutableSetOf<LocalDate>()

                    val resultExerciseList = result ?: emptyList()

                    resultExerciseList.forEach {
                        val localDate = LocalDate.parse(it.startedAt.substring(0, 10)) // "yyyy-MM-dd" 형태 추출
                        tempExerciseSet.add(localDate)
                    }

                    exerciseList.value = tempExerciseSet
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<ExerciseCalendarResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { getExerciseCalendar(activity, userId, year, month) },
                                onFailure = { activity.finish() }
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ExerciseCalendarResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 함께 운동한 친구 검색
    fun getSearchExerciseFriend(activity: Activity, nickname: String) {

        val tempFriends = mutableListOf<FriendResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getSearchExerciseFriend(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), nickname).enqueue(object :
            Callback<List<FriendResponse>> {
            override fun onResponse(
                call: Call<List<FriendResponse>>,
                response: Response<List<FriendResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<FriendResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultFriendList = result ?: emptyList()

                    for (friends in resultFriendList) {
                        val friendList = FriendResponse(
                            friends.id,
                            friends.nickname,
                            friends.profileImgUrl
                        )

                        tempFriends.add(friendList)
                    }

                    searchFriendList.value = tempFriends
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<FriendResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { getSearchExerciseFriend(activity, nickname) },
                                onFailure = { activity.finish() }
                            )
                        }
                        500 -> {
                            searchFriendList.value = null
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<FriendResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 친구 리스트 조회
    fun getFriendList(activity: Activity, currentPage: Int) {

        val tempFriendList = mutableListOf<FriendResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getFriendList(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), currentPage).enqueue(object :
            Callback<PagingResponse<FriendResponse>> {
            override fun onResponse(
                call: Call<PagingResponse<FriendResponse>>,
                response: Response<PagingResponse<FriendResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: PagingResponse<FriendResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    lastFriend.value = (result?.last == true)
                    firstFriend.value = (result?.first == true)

                    val resultFriendList = result?.content ?: emptyList()

                    for (friend in resultFriendList) {
                        val friendItem = FriendResponse(
                            friend.id,
                            friend.nickname,
                            friend.profileImgUrl
                        )

                        tempFriendList.add(friendItem)
                    }

                    friendList.value = tempFriendList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: PagingResponse<FriendResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { getFriendList(activity, currentPage) },
                                onFailure = { activity.finish() }
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PagingResponse<FriendResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 날씨 정보 API - 온도
    fun getWeather(activity: Activity, apiKey: String, lat: Double, lon: Double) {
        val apiClient = WeatherApiClient(activity)

        apiClient.apiService.getCurrentWeather(lat, lon, "metric", apiKey, "kr").enqueue(object :
            Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우

                    MyApplication.recordFeedInfo.weather = response.body()?.weather?.get(0)?.description.toString()
                    MyApplication.recordFeedInfo.temperature =
                        response.body()?.main?.temp?.toInt() ?: 0
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: WeatherResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 날씨 정보 API - 미세먼지
    fun getAirPollution(activity: Activity, apiKey: String, lat: Double, lon: Double) {
        val apiClient = WeatherApiClient(activity)

        apiClient.apiService.getCurrentAirPollutioin(lat, lon, apiKey).enqueue(object :
            Callback<AirPollutionResponse> {
            override fun onResponse(
                call: Call<AirPollutionResponse>,
                response: Response<AirPollutionResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우

                    val fineDust = when(response.body()?.list?.get(0)?.main?.aqi) {
                        1 -> "매우 좋음"
                        2 -> "좋음"
                        3 -> "보통"
                        4 -> "나쁨"
                        5 -> "매우 나쁨"
                        else -> "매우 좋음"
                    }
                    MyApplication.recordFeedInfo.fineDust = fineDust

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: AirPollutionResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<AirPollutionResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // reverse geocoding
    fun getAddressByReverseGeocoding(activity: Activity, lat: Double, lon: Double) {
        val apiClient = ReverseGeoCodingClient(activity)

        apiClient.apiService.getAddress("$lon,$lat").enqueue(object :
            Callback<ReverseGeocodingResponse> {
            override fun onResponse(
                call: Call<ReverseGeocodingResponse>,
                response: Response<ReverseGeocodingResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우

                    var area1 = response.body()?.results?.get(0)?.region?.area1?.name
                    var area2 = response.body()?.results?.get(0)?.region?.area2?.name
                    var area3 = response.body()?.results?.get(0)?.region?.area3?.name

                    MyApplication.recordFeedInfo.location = "${area1} ${area2} ${area3}"
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: ReverseGeocodingResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<ReverseGeocodingResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }


    // 피드 저장
    fun uploadFeed(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        val gson = Gson()

        // JSON 변환 (Gson 사용)
        val walkingDtoJson = gson.toJson(MyApplication.recordFeedInfo)

        // RequestBody 변환 (application/json 설정)
        val walkingDtoRequestBody = walkingDtoJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiClient.apiService.uploadFeed(tokenManager.getAccessToken().toString(), walkingDtoRequestBody, MyApplication.recordFeedImage!!).enqueue(object :
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

                    activity.stopAndResetTracking()

                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, RecordFeedCompleteFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        401 -> {
                            refreshToken(
                                activity,
                                retryRequest = { uploadFeed(activity) },
                                onFailure = { activity.finish() }
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}