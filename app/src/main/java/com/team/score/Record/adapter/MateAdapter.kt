package com.team.score.Record.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.record.FriendResponse
import com.team.score.databinding.RowMateBinding

class MateAdapter(
    private var context: Context,
) :
    RecyclerView.Adapter<MateAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    private var friendList = mutableListOf<FriendResponse>()

    fun addFriends(newFriends: List<FriendResponse>) {
        val start = friendList.size
        friendList.addAll(newFriends)
        notifyItemRangeInserted(start, newFriends.size)
    }

    fun clearFriends() {
        friendList = mutableListOf<FriendResponse>()
        friendList.clear()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowMateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(friendList[position].profileImgUrl).into(holder.profileImage)
        holder.nickname.text = friendList[position].nickname
    }

    override fun getItemCount() = friendList.size

    inner class ViewHolder(val binding: RowMateBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage = binding.imageViewProfile
        val nickname = binding.textViewNickname


        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}