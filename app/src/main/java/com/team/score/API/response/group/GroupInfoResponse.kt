package com.team.score.API.response.group

data class GroupInfoResponse(
    val id: Int,
    val name: String,
    val description: String,
    val groupImg: String,
    val userLimit: Int,
    val currentMembers: Int,
    val recentMembersPic: List<String>,
    val otherMembers: Int,
    val private: Boolean
)
