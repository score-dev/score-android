package com.team.score.API.response.record

data class FeedEmotionResponse(
    val agentId: Int,
    val agentProfileImgUrl: String,
    val agentNickname: String,
    val emotionType: String, // "LIKE", "BEST", "SUPPORT", "CONGRAT"
    val reactedAt: String
)
