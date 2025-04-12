package com.team.score.Home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.CalendarUtil
import com.team.score.databinding.RowHomeWeeklyResultGraphBinding

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
                    val maxHeight = 130f
                    val exerciseSeconds = exerciseResults[position] ?: 0
                    val heightDp = if (exerciseSeconds >= 3600) {
                        maxHeight
                    } else {
                        (exerciseSeconds / 3600f) * maxHeight
                    }

                    val layoutParams = layoutParams
                    layoutParams.height = heightDp.toInt()
                    this.layoutParams = layoutParams
                    setBackgroundResource(R.drawable.background_main_graph)
                }
                holder.totalHour.run {
                    visibility = View.VISIBLE
                    text = String.format("%.1fH", (exerciseResults[position] ?: 0) / 3600f)
                }
                if(exerciseResults[position]!! > 3600) {
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
                    val maxHeight = 130f
                    val exerciseSeconds = exerciseResults[position] ?: 0
                    val heightDp = if (exerciseSeconds >= 3600) {
                        maxHeight
                    } else {
                        (exerciseSeconds / 3600f) * maxHeight
                    }

                    val layoutParams = layoutParams
                    layoutParams.height = heightDp.toInt()
                    this.layoutParams = layoutParams
                    setBackgroundResource(R.drawable.background_sub2_graph)
                }


                if(exerciseResults[position]!! > 3600) {
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