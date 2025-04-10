package com.project.score.API.response


data class PagingResponse<T>(
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
