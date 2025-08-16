package com.team.score.API.request.user

data class UserInfoUpdateRequest(
    var userUpdateDto: UserInfo?,
    var file: String?
)

data class UserSchoolInfo(
    var schoolName: String,
    var schoolCode: String
)

data class UserInfo(
    var nickname: String?,
    var school: UserSchoolInfo?,
    var grade: Int?,
    var height: Int,
    var weight: Int,
    var goal: String?
)
