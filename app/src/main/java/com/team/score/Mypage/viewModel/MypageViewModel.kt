package com.team.score.Mypage.viewModel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.team.score.API.ApiClient
import com.team.score.API.TokenManager
import com.team.score.API.response.login.UserInfoResponse
import com.team.score.API.response.user.BlockedMateListResponse
import com.team.score.API.response.user.NotificationInfoResponse
import com.team.score.MainActivity
import com.team.score.Utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageViewModel: ViewModel() {

    var userInfo: MutableLiveData<UserInfoResponse> = MutableLiveData()
    var notificationInfo: MutableLiveData<NotificationInfoResponse?> = MutableLiveData()
    var blockedMateList = MutableLiveData<MutableList<BlockedMateListResponse>>()

    var isUpdateUserInfo: MutableLiveData<Boolean?> = MutableLiveData()

    var isAddMate: MutableLiveData<Boolean?> = MutableLiveData()

    // 유저 정보
    fun getUserInfo(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getUserInfo(tokenManager.getUserId(), tokenManager.getAccessToken().toString()).enqueue(object :
            Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: UserInfoResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    userInfo.value = result!!
                    tokenManager.saveUserId(result.userId ?: 0)
                    MyApplication.userInfo = result
                    MyApplication.userNickname = result?.nickname.toString()
                    Log.d("##", "viewModel user info : ${MyApplication.userInfo}")
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: UserInfoResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 회원 정보 수정
    fun updateUserInfo(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        val gson = Gson()

        // JSON 변환 (Gson 사용)
        val userDtoJson = gson.toJson(MyApplication.userUpdateInfo?.userUpdateDto)

        // RequestBody 변환 (application/json 설정)
        val userDtoRequestBody = userDtoJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiClient.apiService.updateUserInfo(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), userDtoRequestBody, MyApplication.userUpdateImage).enqueue(object :
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

                    isUpdateUserInfo.value = true
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                    when(response.code()) {
                        400 -> {
                            isUpdateUserInfo.value = false
                        }
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
                isUpdateUserInfo.value = null
            }
        })
    }

    // 알림 수신 여부 설정 현황
    fun getNotificationInfo(activity: Activity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getNotificationInfo(tokenManager.getAccessToken().toString(), tokenManager.getUserId()).enqueue(object :
            Callback<NotificationInfoResponse> {
            override fun onResponse(
                call: Call<NotificationInfoResponse>,
                response: Response<NotificationInfoResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: NotificationInfoResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    notificationInfo.value = result
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: NotificationInfoResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<NotificationInfoResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 알림 수신 여부 설정 수정
    fun updateNotificationInfo(activity: Activity, notificationInfo: NotificationInfoResponse) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.updateNotificationInfo(tokenManager.getAccessToken().toString(), notificationInfo).enqueue(object :
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

    // 메이트 추가
    fun addNewFriends(activity: Activity, mateId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.addNewFriends(tokenManager.getAccessToken().toString(), mateId, tokenManager.getUserId()).enqueue(object :
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

                    isAddMate.value = true

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    isAddMate.value = false
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())

                isAddMate.value = false
            }
        })
    }

    // 차단한 메이트 차단 해제
    fun cancelBlockedMate(activity: Activity, mateId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.cancelBlockedMateList(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), mateId).enqueue(object :
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

    // 회원탈퇴
    fun withdrawal(activity: MainActivity, result: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.withdrawal(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), result).enqueue(object :
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

                    activity.finish()
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