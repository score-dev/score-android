package com.team.score.API.response.group

data class GroupRankingResponse(
    val groupId: Int,
    val startDate: String,
    val endDate: String,
    val rankersInfo: List<RankerInfo>
)

data class RankerInfo(
    val userId: Int,
    val nickname: String,
    val profileImgUrl: String,
    val rankNum: Int,
    val changedAmount: Int,
    val weeklyLevelIncrement: Int,
    val weeklyExerciseTime: Int
)

