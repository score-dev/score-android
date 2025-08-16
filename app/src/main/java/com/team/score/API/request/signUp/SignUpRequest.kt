package com.team.score.API.request.signUp

data class SignUpRequest(
    var userDto: Users,
    var schoolDto: UserSchool,
    var file: String
)

data class UserSchool(
    var schoolName: String,
    var schoolCode: String
)

data class Users(
    var nickname: String,
    var profileImgId: Int,
    var grade: Int,
    var height: Int?,
    var weight: Int?,
    var gender: String?,
    var goal: String?,
    var marketing: Boolean,
    var exercisingTime: Boolean,
    var provider: String,
    var idToken: String
)
