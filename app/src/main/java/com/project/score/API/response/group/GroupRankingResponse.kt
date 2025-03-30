package com.project.score.API.response.group

data class GroupRankingResponse(
    val schoolName: String,
    val schoolId: Int,
    val allRankers: List<GroupRanking>,
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
