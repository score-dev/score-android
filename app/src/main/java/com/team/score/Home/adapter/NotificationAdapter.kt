package com.team.score.Home.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.home.NotificationResponse
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.RecordFragment
import com.team.score.Record.RecordMapFragment
import com.team.score.Utils.CalendarUtil.formatToMonthDay
import com.team.score.databinding.RowNotificationBinding

class NotificationAdapter(
    private var activity: MainActivity,
    private var context: Context,
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int, Boolean) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int, Boolean) -> Unit) {
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
        fun onItemClick(position: Int, accepted: Boolean)
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
                    if(item.read == true) {
                        buttonOne.run {
                            visibility = View.VISIBLE
                            backgroundTintList = ContextCompat.getColorStateList(context, R.color.grey1)
                        }

                        if(item.joinRequestAccepted == true) {
                            buttonOne.text = "수락하셨습니다!"
                        } else {
                            buttonOne.text = "거절하셨습니다."
                        }
                    } else {
                        layoutTwoButton.visibility = View.VISIBLE
                    }
                }
                "JOIN_COMPLETE", "GROUP_RANKING", "SCHOOL_RANKING", "TAGGED" -> {
                    textViewDescription.visibility = View.VISIBLE
                }
                "BATON", "GOAL" -> {
                    buttonOne.visibility = View.VISIBLE

                    if(item.read == true) {
                        buttonOne.backgroundTintList = ContextCompat.getColorStateList(context, R.color.grey1)
                    }
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
                // 알림 삭제
            }

            binding.buttonLeft.setOnClickListener {
                // 그룹 참여 거절
                itemClickListener?.onItemClick(adapterPosition, false)
                onItemClickListener?.invoke(position, false)
            }

            binding.buttonRight.setOnClickListener {
                // 그룹 참여 수락
                itemClickListener?.onItemClick(adapterPosition, true)
                onItemClickListener?.invoke(position, true)
            }

            binding.buttonOne.setOnClickListener {
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}