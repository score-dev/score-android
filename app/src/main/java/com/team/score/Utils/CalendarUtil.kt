package com.team.score.Utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object CalendarUtil {

    private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    // 현재 주를 나타내는 calendar 객체
    private val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
    }

    fun getCurrentWeekDates(): List<Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android O 이상 (LocalDate 사용)
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) // 월요일 시작으로 변경
            (0..6).map { startOfWeek.plusDays(it.toLong()).dayOfMonth }
        } else {
            // Android O 미만 (Calendar 사용)
            getCurrentWeekDatesLegacy()
        }
    }

    private fun getCurrentWeekDatesLegacy(): List<Int> {
        val calendar = Calendar.getInstance()

        // 현재 요일 가져오기 (일요일=1, 월요일=2 ...)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 이번 주 월요일로 이동
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) -6 else -(dayOfWeek - Calendar.MONDAY)
        calendar.add(Calendar.DAY_OF_MONTH, daysFromMonday)

        // 이번 주 날짜 리스트 생성
        return (0..6).map {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            calendar.add(Calendar.DAY_OF_MONTH, 1) // 하루 증가
            day
        }
    }

    fun getTodayDate() : Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().dayOfMonth
        } else {
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        }
    }

    fun getTodayFormatted(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy.MM.dd E요일", Locale.KOREAN)
        return dateFormat.format(calendar.time)
    }

    fun getTodayDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)
        return dateFormat.format(calendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAmPmAndTime(time: String): Pair<String, String> {
        return try {
            // 1. 오프셋이 있는 경우
            val offsetDateTime = OffsetDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val amPm = if (offsetDateTime.hour < 12) "AM" else "PM"
            val timeText = offsetDateTime.format(DateTimeFormatter.ofPattern("hh:mm"))
            Pair(amPm, timeText)
        } catch (e: Exception) {
            // 2. 오프셋이 없는 경우
            val localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val amPm = if (localDateTime.hour < 12) "AM" else "PM"
            val timeText = localDateTime.format(DateTimeFormatter.ofPattern("hh:mm"))
            Pair(amPm, timeText)
        }
    }



    fun getCurrentWeekInfo(): WeekInfo {
        val current = calendar.clone() as Calendar

        // 주 시작 (일요일)
        current.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = current.time

        // 주 끝 (토요일)
        current.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val endDate = current.time

        // 주차 정보 계산을 위해 원래 calendar 기준으로 가져옴
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return WeekInfo(
            year = year,
            month = month,
            weekOfMonth = weekOfMonth,
            startDate = dateFormat.format(startDate),
            endDate = dateFormat.format(endDate)
        )
    }

    fun moveToPreviousWeek(): WeekInfo {
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        return getCurrentWeekInfo()
    }

    fun moveToNextWeek(): WeekInfo {
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        return getCurrentWeekInfo()
    }
}

data class WeekInfo(
    val year: Int,
    val month: Int,
    val weekOfMonth: Int,
    val startDate: String,
    val endDate: String
)

