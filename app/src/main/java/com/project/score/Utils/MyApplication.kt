package com.project.score.Utils

import android.app.Application
import com.project.score.API.request.signUp.SignUpRequest
import com.project.score.API.request.signUp.UserSchool
import com.project.score.API.request.signUp.Users
import com.project.score.API.response.login.UserInfoResponse
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
    }
}