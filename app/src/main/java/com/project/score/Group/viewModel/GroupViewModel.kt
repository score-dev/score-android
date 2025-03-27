package com.project.score.Group.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.project.score.API.ApiClient
import com.project.score.API.TokenManager
import com.project.score.API.request.group.CreateGroupRequest
import com.project.score.API.response.login.LoginResponse
import com.project.score.Group.CreateGroupCompleteFragment
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.SignUp.SignUpNicknameFragment
import com.project.score.Utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupViewModel: ViewModel() {
    // 회원가입
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
}