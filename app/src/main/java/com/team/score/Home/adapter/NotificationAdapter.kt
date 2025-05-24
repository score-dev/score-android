package com.team.score.Home.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.home.NotificationResponse
import com.team.score.R
import com.team.score.Utils.CalendarUtil.formatToMonthDay
import com.team.score.databinding.RowNotificationBinding

class NotificationAdapter(
    private var context: Context,
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    private var notificationList = mutableListOf<NotificationResponse>()

    fun addNotifications(newFeeds: List<NotificationResponse>) {
        val start = notificationList.size
        notificationList.addAll(newFeeds)
        notifyItemRangeInserted(start, newFeeds.size)
    }

    fun clearNotifications() {
        notificationList = mutableListOf<NotificationResponse>()
        notificationList.clear()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = notificationList[position]
        with(holder.binding) {
            if(!item.senderProfileImgUrl.isNullOrEmpty()) {
                Glide.with(context).load(item.senderProfileImgUrl).into(imageViewProfile)
            } else {
                Glide.with(context).load(R.drawable.img_profile_null).into(imageViewProfile)
            }

            textViewTitle.text = item.title
            textViewDescription.text = item.body
            textViewDate.text = formatToMonthDay(item.createdAt ?: "")

            layoutTwoButton.visibility = View.GONE
            buttonOne.visibility = View.GONE
            textViewDescription.visibility = View.GONE

            // 타입별 버튼 분기
            when (item.type) {
                "JOIN_REQUEST" -> {
                    layoutTwoButton.visibility = View.VISIBLE
                }
                "JOIN_COMPLETE", "GROUP_RANKING", "SCHOOL_RANKING", "TAGGED" -> {
                    textViewDescription.visibility = View.VISIBLE
                }
                "BATON", "GOAL" -> {
                    buttonOne.visibility = View.VISIBLE
                }
                "ETC" -> {
                    textViewDescription.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = notificationList.size

    inner class ViewHolder(val binding: RowNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDelete.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }

            binding.buttonLeft.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }

            binding.buttonRight.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }

            binding.buttonOne.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}