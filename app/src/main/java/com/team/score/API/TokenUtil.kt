package com.team.score.API

import android.app.Activity
import android.content.Context
import android.util.Log
import com.team.score.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TokenUtil {

    fun refreshToken(activity: Activity, retryRequest: () -> Unit, onFailure: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.refreshToken(tokenManager.getUserId(), tokenManager.getRefreshToken().toString())
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        tokenManager.saveAccessToken("Bearer ${result}")

                        retryRequest()

                    } else {
                        Log.e("TokenUtil", "재발급 실패: ${response.errorBody()?.string()}")

                        TokenManager(activity).deleteAccessToken()
                        TokenManager(activity).deleteRefreshToken()
                        onFailure()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("TokenUtil", "onFailure: ${t.message}")

                    TokenManager(activity).deleteAccessToken()
                    TokenManager(activity).deleteRefreshToken()
                    onFailure()
                }
            })
    }
}
