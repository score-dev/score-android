package com.team.score.API.response.group

data class SchoolGroupRankingResponse(
    val schoolName: String,
    val schoolId: Int,
    var allRankers: List<GroupRanking>,
    val myGroupRanking: List<GroupRanking>
)

data class GroupRanking(
    val groupId: Int,
    val groupName: String,
    val groupImg: String,
    val maxMemberNum: Int,
    val currentMemberNum: Int,
    val rank: Int,
    val rankChangeAmount: Int,
    val participateRatio: Int,
    val weeklyExerciseTime: Int,
    val private: Boolean
)
