package com.project.score.Utils

import android.os.Build
import androidx.annotation.RequiresApi
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
            else -> ""
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

}