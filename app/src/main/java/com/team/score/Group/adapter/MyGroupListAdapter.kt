package com.team.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.group.MyGroupResponse
import com.team.score.MainActivity
import com.team.score.databinding.RowMyGroupBinding

class MyGroupListAdapter(
    private var activity: MainActivity,
    private var groupInfos: List<MyGroupResponse>?
) :
    RecyclerView.Adapter<MyGroupListAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newGroups: List<MyGroupResponse>?) {
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
            RowMyGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groupInfos?.get(position)

        with(holder.binding) {
            textViewGroupName.text = item?.name
            textViewGroupDescription.text = item?.description

            if(item?.private == true) {
                textViewPublic.visibility = View.VISIBLE
            } else {
                textViewPublic.visibility = View.GONE
            }

            // 그룹 프로필 이미지 설정 (최대 3개 표시)
            val profileImages = item?.recentMembersPic ?: emptyList()
            val profileViews = listOf(imageViewGroupMemberProfile1, imageViewGroupMemberProfile2, imageViewGroupMemberProfile3)
            val layouts = listOf(layoutGroupMemberProfile1, layoutGroupMemberProfile2, layoutGroupMemberProfile3)

            profileViews.forEachIndexed { index, imageView ->
                if (index < profileImages.size) {
                    imageView.visibility = View.VISIBLE
                    Glide.with(activity).load(profileImages[index]).into(imageView)
                } else {
                    imageView.visibility = View.GONE
                }
            }

            layouts.forEachIndexed { index, layout ->
                if (index < profileImages.size) {
                    layout.visibility = View.VISIBLE
                } else {
                    layout.visibility = View.GONE
                }
            }

            if((item?.currentMembers ?: 0) > 3) {
                textViewPeople.run {
                    visibility = View.VISIBLE
                    text = "+${(item?.currentMembers ?: 0) - 3}"
                }
            } else {
                textViewPeople.visibility = View.GONE
            }

            textViewGroupMember.text = "${item?.currentMembers}/${item?.userLimit}명"
        }
    }

    override fun getItemCount() = (groupInfos?.size ?: 0)

    inner class ViewHolder(val binding: RowMyGroupBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}