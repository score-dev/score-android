package com.team.score.API.request.record

data class FeedUploadRequest(
    var startedAt: String,
    var completedAt: String,
    var agentId: Int,
    var othersId: List<Int>?,
    var distance: Int,
    var reducedKcal: Int,
    var location: String?,
    var weather: String?,
    var temperature: Int?,
    var fineDust: String?,
    var feeling: String?
)