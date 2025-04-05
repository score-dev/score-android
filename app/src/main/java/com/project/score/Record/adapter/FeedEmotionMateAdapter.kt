package com.project.score.Record.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.score.API.response.record.FeedEmotionResponse
import com.project.score.databinding.RowFeedEmotionMateBinding
import com.project.score.databinding.RowSchoolBinding

class FeedEmotionMateAdapter(
    private var activity: Activity,
    private var emotions: List<FeedEmotionResponse>?
) :
    RecyclerView.Adapter<FeedEmotionMateAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newEmotions: List<FeedEmotionResponse>?) {
        emotions = newEmotions
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowFeedEmotionMateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = emotions?.get(position)?.agentNickname
        Glide.with(activity).load(emotions?.get(position)?.agentProfileImgUrl).into(holder.profile)
        when(emotions?.get(position)?.emotionType) {
            "LIKE" -> {
                holder.emotion.text = "❤"
            }
            "BEST" -> {
                holder.emotion.text = "\uD83D\uDC4D"
            }
            "SUPPORT" -> {
                holder.emotion.text = "\uD83D\uDE4C"
            }
            "CONGRAT" -> {
                holder.emotion.text = "\uD83C\uDF89"
            }
            else -> {
                holder.emotion.text = ""
            }
        }
    }

    override fun getItemCount() = emotions?.size ?: 0

    inner class ViewHolder(val binding: RowFeedEmotionMateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.textViewNickname
        val profile = binding.imageViewProfile
        val emotion = binding.textViewEmotion
    }
}