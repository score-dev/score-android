package com.team.score.API.response.group

data class GroupUnexercisedMateResponse(
    val userId: Int,
    val nickname: String,
    val profileImageUrl: String,
    val canTurnOverBaton: Boolean
)
