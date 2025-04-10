package com.project.score.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
            else -> "0분"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeToKoreanDisplay(time: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localTime = LocalTime.parse(time, formatter)

        val hour = localTime.hour
        val minute = localTime.minute

        val amPm = if (hour < 12) "오전" else "오후"
        val hour12 = if (hour == 0 || hour == 12) 12 else hour % 12

        return "매일 $amPm ${hour12}:${minute.toString().padStart(2, '0')}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateExerciseDuration(startedAt: String, completedAt: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        val start = LocalDateTime.parse(startedAt, formatter)
        val end = LocalDateTime.parse(completedAt, formatter)

        val duration = Duration.between(start, end)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}시간 ${minutes}분"
            hours > 0 -> "${hours}시간"
            else -> "${minutes}분"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(timeString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val time = LocalDateTime.parse(timeString, formatter)
        val now = LocalDateTime.now()

        val duration = Duration.between(time, now)
        val days = duration.toDays()
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        return when {
            days > 0 -> "${days}일 전"
            hours > 0 && minutes > 0 -> "${hours}시간 ${minutes}분 전"
            hours > 0 -> "${hours}시간 전"
            minutes > 0 -> "${minutes}분 전"
            else -> "방금 전"
        }
    }


}