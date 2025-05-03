package com.team.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.group.GroupMateResponse
import com.team.score.MainActivity
import com.team.score.databinding.RowGroupMateBinding

class GroupMateAdapter(
    private var activity: MainActivity,
    private var mates: List<GroupMateResponse>?,
    private var isMyGroup: Boolean
) :
    RecyclerView.Adapter<GroupMateAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newMates: List<GroupMateResponse>?) {
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
            RowGroupMateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mates?.get(position)

        with(holder.binding) {
            textViewNumber.text = "${position + 1}"
            textViewNickname.text = item?.nickname
            Glide.with(holder.itemView.context).load(item?.profileImgUrl)
                .into(imageViewProfile)

            if(isMyGroup) {
                buttonNext.visibility = View.VISIBLE
            } else {
                buttonNext.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = (mates?.size ?: 0)

    inner class ViewHolder(val binding: RowGroupMateBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}