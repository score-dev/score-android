package com.team.score.API.response.group

import com.team.score.API.response.PagingResponse

data class GroupDetailResponse(
    val groupName: String,
    val userLimit: Int,
    val cumulativeTime: Int,
    val averageParticipateRatio: Int,
    val groupImg: String,
    val numOfTotalMembers: Int,
    val numOfExercisedToday: Int,
    val feeds: PagingResponse<GroupFeedContent>,
    val `private`: Boolean
)

data class GroupFeedContent(
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
    val taggedNicknames: List<String>,
    val taggedProfileImgUrls: List<String>
)