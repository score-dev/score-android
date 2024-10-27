package com.project.score.Mypage.Adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.score.R
import com.project.score.databinding.ItemCalendarBinding
import java.time.LocalDate

class CalendarAdapter(
    private val days: List<LocalDate>,
    private val exerciseDays: Set<LocalDate>,  // 운동한 날들의 집합
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

        // 날짜가 현재 달이 아니면 회색으로 표시
        if (day.month != today.month) {
            holder.binding.textViewDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_gray))
        } else {
            holder.binding.textViewDay.setTextColor(Color.BLACK)
        }

        holder.binding.textViewDay.text = day.dayOfMonth.toString()


        // 오늘 날짜라면 검은색 배경 설정
        if (day == today) {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_today)
            holder.binding.textViewDay.run {
                setTextColor(Color.WHITE)
            }
        }
        // 운동한 날이라면 주황색 배경 설정
        else if (exerciseDays.contains(day)) {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_exercise)
            holder.binding.textViewDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.main))
        }
        // 지난달/다음달 날이라면 회색 배경 설정
        else if (day.month != today.month) {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_previous)
        }
        // 그 외 날짜는 기본 테두리만 표시
        else {
            holder.binding.layoutDay.setBackgroundResource(R.drawable.background_calendar_default)
        }
    }

    override fun getItemCount(): Int = days.size
}
