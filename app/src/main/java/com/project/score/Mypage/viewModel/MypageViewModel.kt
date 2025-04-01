package com.project.score.Mypage.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.score.API.ApiClient
import com.project.score.API.TokenManager
import com.project.score.API.neis.response.SchoolDto
import com.project.score.API.response.login.UserInfoResponse
import com.project.score.API.response.user.BlockedMateListResponse
import com.project.score.Utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageViewModel: ViewModel() {

    var blockedMateList = MutableLiveData<MutableList<BlockedMateListResponse>>()

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

}