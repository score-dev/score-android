package com.team.score.Home.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team.score.API.ApiClient
import com.team.score.API.TokenManager
import com.team.score.API.response.home.BatonStatus
import com.team.score.API.response.home.HomeResponse
import com.team.score.Utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {

    var homeData = MutableLiveData<HomeResponse?>()

    var isBaton = MutableLiveData<BatonStatus>()

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
}