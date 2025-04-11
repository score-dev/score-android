package com.team.score.Mypage.Adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.team.score.R
import com.team.score.databinding.ItemCalendarBinding
import java.time.LocalDate

class CalendarAdapter(
    private val days: List<LocalDate>,
    private val exerciseDays: Set<LocalDate>,  // 운동한 날들의 집합
    private val selectedDate: LocalDate,
    private val today: LocalDate
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    // ViewHolder 내부에 ViewBinding 적용
    inner class DayViewHolder(val binding: ItemCalendarBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        // LayoutInflater를 사용하여 binding 객체 생성
        val binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]

        // 오늘 날짜라면 검은색 배경 설정
        if (day == today && day.month == selectedDate.month) {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_today)
            holder.binding.textViewDay.run {
                setTextColor(Color.WHITE)
            }
        }
        // 운동한 날이라면 주황색 배경 설정
        else if (exerciseDays.contains(day) && day.month == selectedDate.month) {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_exercise)
            holder.binding.textViewDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
        }
        // 지난달/다음달 날이라면 회색 배경 설정
        else if (day.month != selectedDate.month) {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_previous)
            holder.binding.textViewDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_gray))
        }
        // 그 외 날짜는 기본 테두리만 표시
        else {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_default)
            holder.binding.textViewDay.setTextColor(Color.BLACK)
        }

        holder.binding.textViewDay.text = day.dayOfMonth.toString()
    }

    override fun getItemCount(): Int = days.size
}
