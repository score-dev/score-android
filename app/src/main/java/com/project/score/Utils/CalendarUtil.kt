package com.project.score.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import java.util.Calendar

object CalendarUtil {

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
}

