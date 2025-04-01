package com.project.score.Mypage.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.score.API.response.user.BlockedMateListResponse
import com.project.score.MainActivity
import com.project.score.databinding.RowBlockedMateBinding


class BlockedMateAdapter(
    private var activity: MainActivity,
    private var blockedMateList: List<BlockedMateListResponse>?
) :
    RecyclerView.Adapter<BlockedMateAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newBlockedMateList: List<BlockedMateListResponse>?) {
        blockedMateList = newBlockedMateList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowBlockedMateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(activity).load(blockedMateList?.get(position)?.profileImgUrl).into(holder.profile)
        holder.nickName.text = blockedMateList?.get(position)?.nickname
    }

    override fun getItemCount() = (blockedMateList?.size ?: 0) + 1

    inner class ViewHolder(val binding: RowBlockedMateBinding) : RecyclerView.ViewHolder(binding.root) {
        val profile = binding.imageViewProfile
        val nickName = binding.textViewNickname
        val cancelBlock = binding.buttonCancel


        init {
            binding.buttonCancel.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}