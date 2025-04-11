package com.team.score.Group.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.team.score.API.ApiClient
import com.team.score.API.TokenManager
import com.team.score.API.request.group.CreateGroupRequest
import com.team.score.API.response.group.GroupRankingResponse
import com.team.score.Group.CreateGroupCompleteFragment
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupViewModel: ViewModel() {

    var groupRanking = MutableLiveData<GroupRankingResponse?>()

    // 그룹 생성
    fun createGroup(activity: MainActivity,
                    groupName: String,
                    groupDescription: String?,
                    groupMemberNum: Int,
                    groupPassword: String?,
                    isPrivate: Boolean
    ) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        var groupInfo = CreateGroupRequest(tokenManager.getUserId(), groupName, groupDescription, groupMemberNum, groupPassword, isPrivate)

        val gson = Gson()

        // JSON 변환 (Gson 사용)
        val groupDtoJson = gson.toJson(groupInfo)

        // RequestBody 변환 (application/json 설정)
        val groupDtoRequestBody = groupDtoJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiClient.apiService.createGroup(tokenManager.getAccessToken().toString(), groupDtoRequestBody, MyApplication.groupImage!!).enqueue(object :
            Callback<Int> {
            override fun onResponse(
                call: Call<Int>,
                response: Response<Int>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: Int? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, CreateGroupCompleteFragment())
                        .commit()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: Int? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 학교 그룹 랭킹 조회
    fun getGroupRanking(activity: MainActivity, date: String?) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getSchoolGroupRanking(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), date).enqueue(object :
            Callback<GroupRankingResponse> {
            override fun onResponse(
                call: Call<GroupRankingResponse>,
                response: Response<GroupRankingResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: GroupRankingResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    groupRanking.value = result

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: GroupRankingResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<GroupRankingResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}