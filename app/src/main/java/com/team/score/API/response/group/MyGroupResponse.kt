package com.team.score.API.response.group

data class MyGroupResponse(
    val id: Int,
    val name: String,
    val description: String,
    val userLimit: Int,
    val currentMembers: Int,
    val recentMembersPic: List<String>,
    val otherMembers: Int,
    val private: Boolean
)
