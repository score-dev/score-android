package com.team.score.API.request.group

data class CreateGroupRequest(
    val adminId: Int,
    val groupName: String,
    val groupDescription: String?,
    val userLimit: Int,
    val groupPassword: String?,
    val `private`: Boolean
)
