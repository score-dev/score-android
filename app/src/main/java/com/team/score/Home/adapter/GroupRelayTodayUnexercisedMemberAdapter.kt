package com.team.score.Home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
    var selectedPositions = mutableSetOf<Int>()

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newMemberInfos: List<HomeGroupUnexercisedMemebrInfo>?, newlySelectedPosition: Int?) {
        unexercisedMembers = newMemberInfos
        newlySelectedPosition?.let { selectedPositions.add(it) }
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

        holder.buttonBaton.run {
            if (selectedPositions.contains(position) || unexercisedMembers?.get(position)?.canTurnOverBaton == false) {
                text = "찌르기 완료!"
                backgroundTintList = context?.let { ContextCompat.getColorStateList(it, R.color.main) }
            } else {
                text = "바통 찌르기"
                backgroundTintList = context?.let { ContextCompat.getColorStateList(it, R.color.sub) }
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
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    selectedPositions.add(pos)
                    notifyItemChanged(pos)

                    itemClickListener?.onItemClick(pos)
                    onItemClickListener?.invoke(pos)
                }
            }
        }
    }
}