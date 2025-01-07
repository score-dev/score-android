package com.project.score.Login.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.score.API.ApiClient
import com.project.score.API.request.signUp.OauthRequest
import com.project.score.API.response.EmptyResponse
import com.project.score.Login.AgreementFragment
import com.project.score.Login.LoginFragment
import com.project.score.OnBoarding.OnboardingActivity
import com.project.score.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel  : ViewModel() {

    fun checkOauth(activity: OnboardingActivity, type: String, token: String) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.checkOauth(type, OauthRequest(token)).enqueue(object :
            Callback<EmptyResponse> {
            override fun onResponse(
                call: Call<EmptyResponse>,
                response: Response<EmptyResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: EmptyResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    if(result?.data.toString() == "-1") {
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_onboarding, AgreementFragment())
                            .addToBackStack(null)
                            .commit()
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: EmptyResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<EmptyResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}