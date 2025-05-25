package com.team.score.API.response.home

data class NotificationResponse(
    val notificationId: Int,
    val type: String,
    val senderId: Int?,
    val senderProfileImgUrl: String?,
    val receiverId: Int?,
    val relatedGroupId: Int?,
    val relatedFeedId: Int?,
    val createdAt: String?,
    val title: String?,
    val body: String?,
    var read: Boolean?,
    var joinRequestAccepted: Boolean?
)

enum class NotificationType {
    GOAL,              // 목표 운동 시간 알림
    JOIN_REQUEST,      // 그룹 가입 요청 발생 시
    JOIN_COMPLETE,     // 그룹 가입 승인된 경우
    BATON,             // 바통 찌르기 알림
    GROUP_RANKING,     // 그룹 랭킹 1위 알림
    SCHOOL_RANKING,    // 학교 랭킹 1위 알림
    TAGGED,            // 함께 운동한 친구로 태그된 경우
    ETC                // 기타 마케팅/비정기 알림
}
