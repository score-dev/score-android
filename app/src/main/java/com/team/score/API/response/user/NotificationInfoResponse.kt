package com.team.score.API.response.user

data class NotificationInfoResponse(
    val userId: Int,
    var marketing: Boolean,
    var exercisingTime: Boolean,
    var tag: Boolean
)