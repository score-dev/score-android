package com.team.score.API.response.record

data class FeedDetailResponse(
    val feedId: Int,
    val uploaderNickname: String,
    val uploaderProfileImgUrl: String,
    val feedImg: String,
    val startedAt: String,
    val completedAt: String,
    val location: String,
    val weather: String,
    val temperature: Int,
    val fineDust: String,
    val feeling: String,
    val taggedNicknames: List<String>?,
    val taggedProfileImgUrls: List<String>?
)

