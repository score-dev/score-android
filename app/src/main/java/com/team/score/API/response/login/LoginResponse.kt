package com.team.score.API.response.login

data class LoginResponse(
    val id: Int,
    val accessToken: String,
    val refreshToken: String
)