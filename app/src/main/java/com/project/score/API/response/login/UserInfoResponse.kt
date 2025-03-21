package com.project.score.API.response.login

data class UserInfoResponse(
    val userId: Int,
    val nickname: String,
    val gender: String,
    val schoolId: Int,
    val schoolName: String,
    val grade: Int,
    val height: Int,
    val weight: Int,
    val profileImgUrl: String,
    val goal: String,
    val level: Int,
    val point: Int
)
