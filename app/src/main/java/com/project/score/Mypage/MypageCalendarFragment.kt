package com.project.score.Mypage

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.project.score.MainActivity
import com.project.score.Mypage.Adapter.CalendarAdapter
import com.project.score.databinding.FragmentMypageCalendarBinding
import java.time.LocalDate
import java.time.YearMonth

class MypageCalendarFragment : Fragment() {

    lateinit var binding: FragmentMypageCalendarBinding
    lateinit var mainActivity: MainActivity

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageCalendarBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {

            val today = LocalDate.now()
            val exerciseDays = setOf( // 운동한 날짜를 정의합니다.
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 4),
                LocalDate.of(2024, 10, 12)
            )
            val daysInMonth = getDaysInMonth(today.year, today.monthValue)

            Log.d("스코어", "daysInMonth : ${daysInMonth}")

            val adapter = CalendarAdapter(daysInMonth, exerciseDays, today)
            recyclerViewCalendar.layoutManager = GridLayoutManager(mainActivity, 7) // 7열로 그리드 배치
            recyclerViewCalendar.adapter = adapter
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDaysInMonth(year: Int, month: Int): List<LocalDate> {
        val yearMonth = YearMonth.of(year, month)
        val daysInMonth = mutableListOf<LocalDate>()
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 일요일을 0으로 맞춤

        // 이전 달 날짜 추가 (해당 월 시작 요일 전의 빈 칸 채우기)
        for (i in 1..dayOfWeek) {
            daysInMonth.add(firstDayOfMonth.minusDays(i.toLong()))
        }

        // 이번 달 날짜 추가
        for (day in 1..yearMonth.lengthOfMonth()) {
            daysInMonth.add(yearMonth.atDay(day))
        }

        // 다음 달 날짜 추가 (해당 월 마지막 날이 포함된 주까지만)
        val remainingDays = 7 - (daysInMonth.size % 7)
        if (remainingDays < 7) { // 정확히 나누어 떨어질 경우 추가하지 않음
            for (i in 1..remainingDays) {
                daysInMonth.add(lastDayOfMonth.plusDays(i.toLong()))
            }
        }

        return daysInMonth
    }

}