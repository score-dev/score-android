package com.project.score.Home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.Utils.CalendarUtil
import com.project.score.databinding.RowHomeWeeklyResultGraphBinding

class WeeklyCalendarAdapter(
    private var activity: MainActivity,
    private var weekDates: List<Int>,
    private var exerciseResults: List<Int?>
) :
    RecyclerView.Adapter<WeeklyCalendarAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    val day = listOf("월", "화", "수", "목", "금", "토", "일")

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newDates: List<Int>, newExerciseResult: List<Int?>) {
        weekDates = newDates
        exerciseResults = newExerciseResult
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowHomeWeeklyResultGraphBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.day.text = day[position]
        holder.date.text = weekDates[position].toString()

        if(weekDates[position] == CalendarUtil.getTodayDate()) {
            holder.date.run {
                setBackgroundResource(R.drawable.background_black_circle)
                setTextColor(resources.getColor(R.color.white))
            }

            if(exerciseResults[position] != null) { // 오늘 운동 기록이 있는 경우
                holder.graph.run {
                    visibility = View.VISIBLE
                    val layoutParams = holder.graph.layoutParams
                    val calculatedHeight = (exerciseResults[position]?.div(60.0))?.times(130) ?: 0.0

                    // 소수점 첫째 자리까지 반올림 후 정수로 변환
                    val roundedHeight = (Math.round(calculatedHeight * 10) / 10.0).toInt()

                    layoutParams.height = minOf(roundedHeight, 130) // 최대값 130 제한
                    holder.graph.layoutParams = layoutParams
                    setBackgroundResource(R.drawable.background_main_graph)
                }
                holder.totalHour.run {
                    visibility = View.VISIBLE
                    text = "${exerciseResults[position]?.div(60.0)}H"
                }
                if(exerciseResults[position]!! > 60) {
                    holder.overTime.visibility = View.VISIBLE
                } else {
                    holder.overTime.visibility = View.INVISIBLE
                }
            } else { // 오늘 운동 기록이 없는 경우
                holder.graph.visibility = View.INVISIBLE
                holder.totalHour.visibility = View.INVISIBLE
                holder.overTime.visibility = View.INVISIBLE
            }
        } else {
            if(exerciseResults[position] != null) { // 해당 날짜에 운동 기록이 있는 경우
                holder.date.run {
                    setBackgroundResource(R.drawable.background_sub3_circle)
                    setTextColor(resources.getColor(R.color.main))
                }

                holder.totalHour.visibility = View.INVISIBLE

                holder.graph.run {
                    visibility = View.VISIBLE
                    val layoutParams = holder.graph.layoutParams
                    layoutParams.height = minOf((exerciseResults[position]?.div(60))?.times(130) ?: 0, 130)
                    holder.graph.layoutParams = layoutParams
                    setBackgroundResource(R.drawable.background_sub2_graph)
                }

                if(exerciseResults[position]!! > 60) {
                    holder.overTime.visibility = View.VISIBLE
                } else {
                    holder.overTime.visibility = View.INVISIBLE
                }
            } else {
                holder.date.run {
                    setBackgroundResource(R.drawable.background_grey2_circle)
                    setTextColor(resources.getColor(R.color.text_color1))
                }

                holder.graph.visibility = View.INVISIBLE
                holder.totalHour.visibility = View.INVISIBLE
                holder.overTime.visibility = View.INVISIBLE
            }
        }

    }

    override fun getItemCount() = weekDates.size


    inner class ViewHolder(val binding: RowHomeWeeklyResultGraphBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val day = binding.textViewDay
        val date = binding.textViewDate
        var graph = binding.graphWeeklyResult
        val totalHour = binding.textViewTodayExerciseHour
        val overTime = binding.imageViewGraphWeeklyResultOver
    }
}