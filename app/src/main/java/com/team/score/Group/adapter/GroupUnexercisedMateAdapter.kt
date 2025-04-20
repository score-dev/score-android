package com.team.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.group.GroupMateResponse
import com.team.score.API.response.group.GroupUnexercisedMateResponse
import com.team.score.API.response.group.MyGroupResponse
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.RowGroupMateBinding
import com.team.score.databinding.RowGroupUnexercisedMemberBinding
import com.team.score.databinding.RowMyGroupBinding

class GroupUnexercisedMateAdapter(
    private var activity: MainActivity,
    private var mates: List<GroupUnexercisedMateResponse>?
) :
    RecyclerView.Adapter<GroupUnexercisedMateAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newMates: List<GroupUnexercisedMateResponse>?) {
        mates = newMates
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowGroupUnexercisedMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mates?.get(position)

        with(holder.binding) {
            textViewNickname.text = item?.nickname
            Glide.with(holder.itemView.context).load(item?.profileImageUrl)
                .into(imageViewUnexercisedMemberProfile)
            buttonBaton.run {
                if(item?.canTurnOverBaton == true) {
                    isEnabled = true
                    setBackgroundResource(R.drawable.background_sub_radius10)
                    text = "바통 찌르기"
                } else {
                    isEnabled = false
                    setBackgroundResource(R.drawable.background_main_radius10)
                    text = "찌르기 완료!"
                }
            }
        }
    }

    override fun getItemCount() = (mates?.size ?: 0)

    inner class ViewHolder(val binding: RowGroupUnexercisedMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonBaton.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}