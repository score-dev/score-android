package com.project.score.Utils

object TimeUtil {
    fun formatExerciseTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> {
                if (minutes > 0) "${hours}시간 ${minutes}분"
                else "${hours}시간"
            }
            minutes > 0 -> {
                if (remainingSeconds > 0) "${minutes}분 ${remainingSeconds}초"
                else "${minutes}분"
            }
            remainingSeconds > 0 -> "${remainingSeconds}초"
            else -> ""
        }
    }
}