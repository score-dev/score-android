package com.team.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.group.GroupInfoResponse
import com.team.score.MainActivity
import com.team.score.databinding.RowGroupSearchBinding

class SearchGroupAdapter(
    private var activity: MainActivity,
    private var groupInfos: List<GroupInfoResponse>?
) :
    RecyclerView.Adapter<SearchGroupAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newGroups: List<GroupInfoResponse>?) {
        groupInfos = newGroups
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowGroupSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groupInfos?.get(position)

        with(holder.binding) {
            textViewGroupName.text = item?.name
            textViewGroupDescription.text = item?.description
            Glide.with(activity).load(item?.groupImg).into(imageViewGroup)

            if(item?.private == true) {
                textViewPublic.visibility = View.VISIBLE
            } else {
                textViewPublic.visibility = View.GONE
            }

            textViewGroupMemberNum.text = "${item?.currentMembers}/${item?.userLimit}명"
        }
    }

    override fun getItemCount() = (groupInfos?.size ?: 0)

    inner class ViewHolder(val binding: RowGroupSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}