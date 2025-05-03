package com.team.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.group.GroupUnexercisedMateResponse
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.RowGroupUnexercisedMemberBinding

class GroupUnexercisedMateAdapter(
    private var activity: MainActivity,
    private var mates: List<GroupUnexercisedMateResponse>?
) :
    RecyclerView.Adapter<GroupUnexercisedMateAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    var selectedPosition = -1

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newMateInfos: List<GroupUnexercisedMateResponse>?, batonMemberPostion: Int) {
        mates = newMateInfos
        selectedPosition = batonMemberPostion
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

            if(item?.canTurnOverBaton == true) {
                buttonBaton.run {
                    text = "바통 찌르기"
                    setBackgroundResource(R.drawable.background_sub_radius10)
                }
            } else {
                buttonBaton.run {
                    text = "찌르기 완료!"
                    setBackgroundResource(R.drawable.background_main_radius10)
                }
            }

            if(position == selectedPosition) {
                buttonBaton.run {
                    text = "찌르기 완료!"
                    setBackgroundResource(R.drawable.background_main_radius10)
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