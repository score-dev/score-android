package com.team.score.Home.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team.score.API.ApiClient
import com.team.score.API.TokenManager
import com.team.score.API.response.PagingResponse
import com.team.score.API.response.home.BatonStatus
import com.team.score.API.response.home.HomeResponse
import com.team.score.API.response.home.NotificationResponse
import com.team.score.API.response.user.FeedListResponse
import com.team.score.Utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {

    var homeData = MutableLiveData<HomeResponse?>()

    var isBaton = MutableLiveData<BatonStatus>()

    var notificationList = MutableLiveData<MutableList<NotificationResponse>>()
    var lastNotification: MutableLiveData<Boolean> = MutableLiveData()
    var firstNotification: MutableLiveData<Boolean> = MutableLiveData()

    // 홈 정보
    fun getHomeData(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getHomeData(tokenManager.getUserId(), tokenManager.getAccessToken().toString()).enqueue(object :
            Callback<HomeResponse> {
            override fun onResponse(
                call: Call<HomeResponse>,
                response: Response<HomeResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: HomeResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    MyApplication.userNickname = result?.nickname.toString()
                    MyApplication.consecutiveDate = result?.consecutiveDate ?: 0
                    homeData.value = result

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: HomeResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 바통 찌르기
    fun batonGroupMember(activity: Activity, receiverId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.batonGroupMember(tokenManager.getUserId(), receiverId, tokenManager.getAccessToken().toString()).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(
                call: Call<Boolean>,
                response: Response<Boolean>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: Boolean? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    isBaton.value = BatonStatus(true, receiverId)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: Boolean? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    isBaton.value = BatonStatus(false, receiverId)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
                isBaton.value = BatonStatus(false, receiverId)
            }
        })
    }

    // 알림 리스트 정보
    fun getNotificationList(activity: Activity, currentPage: Int) {

        val tempNotificationList = mutableListOf<NotificationResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getNotificationList(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), currentPage).enqueue(object :
            Callback<PagingResponse<NotificationResponse>> {
            override fun onResponse(
                call: Call<PagingResponse<NotificationResponse>>,
                response: Response<PagingResponse<NotificationResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: PagingResponse<NotificationResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    lastNotification.value = (result?.last == true)
                    firstNotification.value = (result?.first == true)

                    val resultList = result?.content ?: emptyList()

                    for (notification in resultList) {
                        val notificationItem = NotificationResponse(
                            notificationId = notification.notificationId,
                            type = notification.type,
                            senderId = notification.senderId,
                            senderProfileImgUrl = notification.senderProfileImgUrl,
                            receiverId = notification.receiverId,
                            relatedGroupId = notification.relatedGroupId,
                            relatedFeedId = notification.relatedFeedId,
                            createdAt = notification.createdAt,
                            title = notification.title,
                            body = notification.body,
                            read = notification.read,
                            joinRequestAccepted = notification.joinRequestAccepted
                        )

                        tempNotificationList.add(notificationItem)
                    }

                    notificationList.value = tempNotificationList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: PagingResponse<NotificationResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<PagingResponse<NotificationResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 참여 신청 수락/거절
    fun acceptGroupParticipate(activity: Activity, notificationId: Int, isAccept: Boolean, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.acceptGroupParticipate(tokenManager.getAccessToken().toString(), notificationId, isAccept).enqueue(object :
            Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: String? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    onSuccess()

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

            override fun onFailure(call: Call<String?>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}