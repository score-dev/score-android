package com.project.score.API.response.home

data class HomeResponse(
    val nickname: String,
    val profileImgUrl: String,
    val level: Int,
    val point: Int,
    val weeklyExerciseTimeByDay: List<Int?>,
    val weeklyTotalExerciseTime: Int,
    val weeklyExerciseCount: Int,
    val consecutiveDate: Int,
    val numOfGroups: Int,
    val groupsInfo: List<HomeGroupInfo>,
    val overReportedUser: Boolean
)

data class HomeGroupInfo(
    val groupId: Int,
    val groupName: String,
    val numOfMembers: Int,
    val todayExercisedMatesImgUrl: List<String>,
    val notExercisedUsers: List<HomeGroupUnexercisedMemebrInfo>
)

data class HomeGroupUnexercisedMemebrInfo(
    val userId: Int,
    val nickname: String,
    val profileImgUrl: String
)
