package com.team.score.Home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.home.HomeGroupUnexercisedMemebrInfo
import com.team.score.R
import com.team.score.databinding.RowTodayUnexercisedMemberBinding

class GroupRelayTodayUnexercisedMemberAdapter(
    private var activity: Activity,
    private var unexercisedMembers: List<HomeGroupUnexercisedMemebrInfo>?
) :
    RecyclerView.Adapter<GroupRelayTodayUnexercisedMemberAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    var selectedPosition = -1

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newMemberInfos: List<HomeGroupUnexercisedMemebrInfo>?, batonMemberPostion: Int) {
        unexercisedMembers = newMemberInfos
        selectedPosition = batonMemberPostion
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(memberId: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowTodayUnexercisedMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(activity).load(unexercisedMembers?.get(position)?.profileImgUrl).into(holder.profile)
        holder.memberName.text = unexercisedMembers?.get(position)?.nickname.toString()

        if(unexercisedMembers?.get(position)?.canTurnOverBaton == true) {
            holder.buttonBaton.run {
                text = "바통 찌르기"
                backgroundTintList = resources.getColorStateList(R.color.sub)
            }
        } else {
            holder.buttonBaton.run {
                text = "찌르기 완료!"
                backgroundTintList = resources.getColorStateList(R.color.main)
            }
        }

        if(position == selectedPosition) {
            holder.buttonBaton.run {
                text = "찌르기 완료!"
                backgroundTintList = resources.getColorStateList(R.color.main)
            }
        } else {
            holder.buttonBaton.run {
                text = "바통 찌르기"
                backgroundTintList = resources.getColorStateList(R.color.sub)
            }
        }
    }

    override fun getItemCount() = unexercisedMembers?.size ?: 0


    inner class ViewHolder(val binding: RowTodayUnexercisedMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val profile = binding.imageViewUnexercisedMemberProfile
        val memberName = binding.textViewNickname
        val buttonBaton = binding.buttonBaton

        init {
            binding.buttonBaton.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}