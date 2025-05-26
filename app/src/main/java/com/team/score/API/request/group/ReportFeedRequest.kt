package com.team.score.API.request.group

data class ReportFeedRequest(
    val agentId: Int,
    val feedId: Int,
    val reportReason: String,
    val comment: String
)