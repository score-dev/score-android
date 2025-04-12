package com.team.score.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtil {
    // 목표 시간 설정 포맷
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

    // 목표 시간 표시 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatExerciseTimeToKorean(time: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localTime = LocalTime.parse(time, formatter)

        val hour = localTime.hour
        val minute = localTime.minute

        val amPm = if (hour < 12) "오전" else "오후"
        val hour12 = if (hour == 0 || hour == 12) 12 else hour % 12

        return "매일 $amPm ${hour12}:${minute.toString().padStart(2, '0')}"
    }

    // 운동 시간 계산
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

    // 운동 시간 계산 - 영어
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateExerciseDurationWithEnglish(startedAt: String, completedAt: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX") // XXX: 오프셋(+09:00) 포함

        val start = OffsetDateTime.parse(startedAt, formatter)
        val end = OffsetDateTime.parse(completedAt, formatter)

        val duration = Duration.between(start, end)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}H ${minutes}M"
            hours > 0 -> "${hours}H"
            else -> "${minutes}M"
        }
    }


    // 기간 계산 (00분 전)
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

    // 기록하기 시간 포맷: 00:00 ~ 59:59 → 1:00:00 ~
    fun formatRecordTime(seconds: Int): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hrs > 0) {
            String.format("%d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToIso(millis: Long): String {
        val zonedDateTime = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.of("Asia/Seoul"))
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

}