package com.team.score.Mypage

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.team.score.MainActivity
import com.team.score.Mypage.Adapter.CalendarAdapter
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.databinding.FragmentUserCalendarBinding
import java.time.LocalDate
import java.time.YearMonth

class UserCalendarFragment : Fragment() {

    lateinit var binding: FragmentUserCalendarBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    // 선택된 날짜
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val today = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserCalendarBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        binding.run {
            // 이전달 버튼 클릭
            buttonPrevious.setOnClickListener {
                selectedDate = selectedDate.minusMonths(1)
                viewModel.getExerciseCalendar(mainActivity, arguments?.getInt("userId") ?: 0, selectedDate.year, selectedDate.monthValue)
            }

            // → 다음 달 버튼 클릭 시
            buttonNext.setOnClickListener {
                selectedDate = selectedDate.plusMonths(1)
                viewModel.getExerciseCalendar(mainActivity, arguments?.getInt("userId") ?: 0, selectedDate.year, selectedDate.monthValue)
            }

        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCalendar(exerciseDays: Set<LocalDate>) {
        binding.run {
            val daysInMonth = getDaysInMonth(selectedDate.year, selectedDate.monthValue)
            initAdapter(daysInMonth, exerciseDays)

            // 상단 월/연도 텍스트 설정
            textViewMonthYear.text = setMonthYearText(selectedDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeViewModel() {
        viewModel.run {
            exerciseList.observe(viewLifecycleOwner) {
                updateCalendar(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initAdapter(daysInMonth: List<LocalDate>, exerciseDays: Set<LocalDate>) {
        binding.run {
            val adapter = CalendarAdapter(daysInMonth, exerciseDays, selectedDate, today)
            recyclerViewCalendar.layoutManager = GridLayoutManager(mainActivity, 7) // 7열로 그리드 배치
            recyclerViewCalendar.adapter = adapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        binding.run {
            root.requestLayout()

            val daysInMonth = getDaysInMonth(today.year, today.monthValue)
            initAdapter(daysInMonth, emptySet())

            viewModel.getExerciseCalendar(mainActivity, arguments?.getInt("userId") ?: 0, today.year, today.monthValue)

            Log.d("스코어", "daysInMonth : ${daysInMonth}")

            textViewMonthYear.text = setMonthYearText(today)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDaysInMonth(year: Int, month: Int): List<LocalDate> {
        val yearMonth = YearMonth.of(year, month)
        val daysInMonth = mutableListOf<LocalDate>()
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        val dayOfWeek = (firstDayOfMonth.dayOfWeek.value + 6) % 7
        // 월요일: 1 → (1+6)%7 = 0 (0칸만큼 이전 날짜), 화요일: 2 → (2+6)%7 = 1, ...

        // 이전 달 날짜 추가
        for (i in 1..dayOfWeek) {
            daysInMonth.add(firstDayOfMonth.minusDays(dayOfWeek.toLong() - i + 1))
        }

        // 이번 달 날짜 추가
        for (day in 1..yearMonth.lengthOfMonth()) {
            daysInMonth.add(yearMonth.atDay(day))
        }

        // 다음 달 날짜 추가 (해당 월 마지막 날이 포함된 주까지만)
        val remainingDays = 7 - (daysInMonth.size % 7)
        if (remainingDays < 7) {
            for (i in 1..remainingDays) {
                daysInMonth.add(lastDayOfMonth.plusDays(i.toLong()))
            }
        }

        return daysInMonth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthYearText(date: LocalDate): String? {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH)
        val formattedMonthYear = date.format(formatter)

        return formattedMonthYear
    }

    companion object {
        fun newInstance(userId: Int): UserCalendarFragment {
            val fragment = UserCalendarFragment()
            val bundle = Bundle()
            bundle.putInt("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }

}