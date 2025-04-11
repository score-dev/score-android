package com.team.score.Utils

import android.app.Application
import com.naver.maps.geometry.LatLng
import com.team.score.API.request.signUp.SignUpRequest
import com.team.score.API.request.signUp.UserSchool
import com.team.score.API.request.signUp.Users
import com.team.score.API.request.user.UserInfo
import com.team.score.API.request.user.UserInfoUpdateRequest
import com.team.score.API.response.login.UserInfoResponse
import okhttp3.MultipartBody

class MyApplication : Application() {
    companion object {
        lateinit var preferences: PreferenceUtil

        var signUpImage: MultipartBody.Part? = null
        var signUpInfo: SignUpRequest? = SignUpRequest(
            userDto = Users(
                nickname = "",
                grade = 0,
                height = 0,
                weight = 0,
                gender = "",
                goal = "00:00:00",
                marketing = false,
                exercisingTime = false,
                provider = "",
                idToken = ""
            ),
            schoolDto = UserSchool(
                schoolName = "",
                schoolAddress = "",
                schoolCode = ""
            ),
            file = ""
        )

        var userInfo: UserInfoResponse? = null
        var userNickname = ""
        var consecutiveDate = 0
        var userUpdateImage: MultipartBody.Part? = null
        var userUpdateInfo: UserInfoUpdateRequest? = UserInfoUpdateRequest(
            userUpdateDto = UserInfo(
                nickname = null,
                school = null,
                grade = null,
                height = 0,
                weight = 0,
                goal = null,
            ),
            file = null
        )

        // group
        var groupImage: MultipartBody.Part? = null

        // record
        var recordTimer = 0
        var locationList = mutableListOf<LatLng>()
        var totalDistance = 0 // 미터 단위로 저장
    }
}