package com.project.score.API.response.user

data class PaginatedResponse<T>(
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val content: List<T>,
    val number: Int,
    val sort: SortInfo,
    val numberOfElements: Int,
    val pageable: PageableInfo,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)

data class SortInfo(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

data class PageableInfo(
    val offset: Int,
    val sort: SortInfo,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class FeedListResponse(
    val feedId: Int,
    val uploaderNickname: String,
    val uploaderProfileImgUrl: String,
    val feedImg: String,
    val startedAt: String,
    val completedAt: String,
    val location: String,
    val weather: String,
    val temperature: Int,
    val fineDust: String,
    val feeling: String,
    val taggedNicknames: List<String>,
    val taggedProfileImgUrls: List<String>
)
