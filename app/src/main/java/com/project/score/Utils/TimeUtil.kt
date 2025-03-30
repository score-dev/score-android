package com.project.score.Utils

object TimeUtil {
    fun formatExerciseTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60

        return when {
            hours == 0 -> "${minutes}분"
            minutes == 0 -> "${hours}시간"
            else -> "${hours}시간 ${minutes}분"
        }
    }
}