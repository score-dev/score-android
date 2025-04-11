package com.team.score.Record.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.databinding.RowFeedEmotionMateBinding
import com.team.score.databinding.RowFeedFrameBinding

class FeedFrameAdpater(
    private var activity: Activity,
    private var frames: List<Int>
) :
    RecyclerView.Adapter<FeedFrameAdpater.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = -1


    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowFeedFrameBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.frame.setImageResource(frames[position])

        // 선택된 아이템만 checkIcon 표시
        holder.checkIcon.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

        holder.binding.root.setOnClickListener {
            // 선택된 위치 업데이트
            val previousSelected = selectedPosition
            selectedPosition = position

            // 이전 위치와 현재 위치 둘 다 갱신
            notifyItemChanged(previousSelected)
            notifyItemChanged(position)

            // 클릭 리스너 호출
            itemClickListener?.onItemClick(position)
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount() = frames.size ?: 0

    inner class ViewHolder(val binding: RowFeedFrameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val frame = binding.imageViewFrame
        val checkIcon = binding.imageViewCheck
    }
}