package com.team.score.API.request.user

data class ReportMateRequest(
    val agentId: Int,
    val objectId: Int,
    val reportReason: String,
    val comment: String
)