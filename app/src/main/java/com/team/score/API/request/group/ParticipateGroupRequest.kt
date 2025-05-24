package com.team.score.API.request.group

data class ParticipateGroupRequest(
    val requesterId: Int,
    val groupId: Int,
    val message: String
)