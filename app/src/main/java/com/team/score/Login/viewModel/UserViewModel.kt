package com.team.score.Login.viewModel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.team.score.API.ApiClient
import com.team.score.API.TokenManager
import com.team.score.API.neis.NeisApiClient
import com.team.score.API.neis.response.NeisSchoolResponse
import com.team.score.API.neis.response.SchoolDto
import com.team.score.API.request.signUp.FcmRequest
import com.team.score.API.response.login.LoginResponse
import com.team.score.Login.AgreementFragment
import com.team.score.MainActivity
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel  : ViewModel() {

    var isUniqueNickName: MutableLiveData<Boolean> = MutableLiveData()
    var schoolInfoList = MutableLiveData<MutableList<SchoolDto>>()
    var isSignUp: MutableLiveData<Boolean> = MutableLiveData()

    fun checkOauth(activity: OnboardingActivity, type: String, token: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.checkOauth(type, RequestBody.create("text/plain".toMediaTypeOrNull(), token)).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: LoginResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    if(result?.id == -1) {
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_onboarding, AgreementFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        tokenManager.saveTokens("Bearer ${result?.accessToken.toString()}", "Bearer ${result?.refreshToken.toString()}")
                        tokenManager.saveUserId(result?.id ?: 0)

                        val mainIntent = Intent(activity, MainActivity::class.java)
                        mainIntent.putExtra("isLogin", true)
                        activity.startActivity(mainIntent)
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: LoginResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    fun checkNickName(activity: Activity, nickName: String) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.checkNickName(nickName).enqueue(object :
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

                    if(result == 1) {
                        isUniqueNickName.value = true
                    } else {
                        isUniqueNickName.value = false
                    }

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

    // 학교 정보 API
    fun getSchoolList(activity: Activity, apiKey: String, input: String) {

        var tempSchoolDto = mutableListOf<SchoolDto>()
        val apiClient = NeisApiClient(activity)

        apiClient.apiService.getSchoolList(apiKey, "json", 1, 100, input).enqueue(object :
            Callback<NeisSchoolResponse> {
            override fun onResponse(
                call: Call<NeisSchoolResponse>,
                response: Response<NeisSchoolResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우

                    val schoolList = response.body()?.schoolInfo?.get(1)?.row ?: emptyList()

                    Log.d("##", "school list : ${schoolList}")

                    for (school in schoolList) {
                        var schoolCode = school.SD_SCHUL_CODE
                        var schoolName = school.SCHUL_NM
                        var schoolAddress = school.ORG_RDNMA

                        tempSchoolDto.add(SchoolDto(schoolCode, schoolName, schoolAddress))

                        Log.d("NEIS_API", "코드 : ${school.SD_SCHUL_CODE} 학교명: ${school.SCHUL_NM}, 위치: ${school.ORG_RDNMA}")
                    }

                    schoolInfoList.value = tempSchoolDto

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: NeisSchoolResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<NeisSchoolResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 회원가입
    fun signUp(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        val gson = Gson()

        // JSON 변환 (Gson 사용)
        val userDtoJson = gson.toJson(MyApplication.signUpInfo?.userDto)
        val schoolDtoJson = gson.toJson(MyApplication.signUpInfo?.schoolDto)

        // RequestBody 변환 (application/json 설정)
        val userDtoRequestBody = userDtoJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val schoolDtoRequestBody = schoolDtoJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiClient.apiService.signUp(userDtoRequestBody, schoolDtoRequestBody, MyApplication.signUpImage).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: LoginResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    tokenManager.saveTokens("Bearer ${result?.accessToken.toString()}", "Bearer ${result?.refreshToken.toString()}")
                    tokenManager.saveUserId(result?.id ?: 0)

                    isSignUp.value = true
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: LoginResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    isSignUp.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
                isSignUp.value = false
            }
        })
    }

    // FCM 토큰 조회
    fun getFcmToken(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getFcmToken(tokenManager.getUserId(), tokenManager.getAccessToken().toString()).enqueue(object :
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

                    if(result.toString() != MyApplication.preferences.getFCMToken().toString()) {
                        setFcmToken(activity)
                    }
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

    // FCM 토큰 저장
    fun setFcmToken(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.setFcmToken(tokenManager.getUserId(), tokenManager.getAccessToken().toString(), FcmRequest(MyApplication.preferences.getFCMToken().toString())).enqueue(object :
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
}