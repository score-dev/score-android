package com.project.score.Home.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.score.API.response.home.HomeGroupInfo
import com.project.score.Home.viewModel.HomeViewModel
import com.project.score.Utils.DynamicSpacingItemDecoration
import com.project.score.Utils.MyApplication
import com.project.score.databinding.RowHomeGroupBinding

class GroupRelayAdapter(
    private var activity: Activity,
    private var groupInfos: List<HomeGroupInfo>?,
    private val viewModel: HomeViewModel
) :
    RecyclerView.Adapter<GroupRelayAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newGroups: List<HomeGroupInfo>?) {
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
            RowHomeGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == (groupInfos?.size)) {
            holder.layoutEmptyView.visibility = View.VISIBLE
            holder.layoutGroupInfo.visibility = View.GONE
        } else {
            holder.layoutEmptyView.visibility = View.GONE
            holder.layoutGroupInfo.visibility = View.VISIBLE

            holder.groupName.text = groupInfos?.get(position)?.groupName.toString()

            // 그룹 프로필 이미지 설정 (최대 3개 표시)
            val profileImages = groupInfos?.get(position)?.todayExercisedMatesImgUrl ?: emptyList()
            val profileViews = listOf(holder.groupProfile1, holder.groupProfile2, holder.groupProfile3)

            profileViews.forEachIndexed { index, imageView ->
                if (index < profileImages.size) {
                    imageView.visibility = View.VISIBLE
                    Glide.with(activity).load(profileImages[index]).into(imageView)
                } else {
                    imageView.visibility = View.INVISIBLE
                }
            }

            if((groupInfos?.get(position)?.numOfMembers ?: 0) > 3) {
                holder.overGroupMemberNum.run {
                    visibility = View.VISIBLE
                    text = "+${(groupInfos?.get(position)?.numOfMembers ?: 0) - 3}"
                }
            } else {
                holder.overGroupMemberNum.visibility = View.INVISIBLE
            }

            holder.totalGroupMemberNum.text = "${groupInfos?.get(position)?.numOfMembers}명 중 "
            holder.exercisedGroupMemberNum.text = "${groupInfos?.get(position)?.todayExercisedMatesImgUrl?.size}명"
            holder.userName.text = "${MyApplication.userNickname}님"

            // **RecyclerView 설정**
            holder.relayExercisedMemberProfile.run {
                adapter = GroupExercisedMemberProfileAdapter(activity, groupInfos?.get(position)?.numOfMembers ?: 0, groupInfos?.get(position)?.todayExercisedMatesImgUrl)
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                val spacingRatio = 20f
                addItemDecoration(
                    DynamicSpacingItemDecoration(spacingRatio)
                )
            }

            holder.recyclerViewUnexercisedMember.run {
                adapter = GroupRelayTodayUnexercisedMemberAdapter(activity, groupInfos?.get(position)?.notExercisedUsers ?: emptyList()).apply {
                    itemClickListener = object : GroupRelayTodayUnexercisedMemberAdapter.OnItemClickListener {
                        override fun onItemClick(adapterPosition: Int) {
                            updateList(groupInfos?.get(position)?.notExercisedUsers, adapterPosition)
                        }
                    }
                }
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    override fun getItemCount() = (groupInfos?.size ?: 0) + 1

    inner class ViewHolder(val binding: RowHomeGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        // layout
        val layoutEmptyView = binding.layoutCreateGroup
        val layoutGroupInfo = binding.layoutGroupInfo

        // 그룹 정보
        val groupName = binding.textViewGroupName
        val groupProfile1 = binding.imageViewGroupMemberProfile1
        val groupProfile2 = binding.imageViewGroupMemberProfile2
        val groupProfile3 = binding.imageViewGroupMemberProfile3
        val overGroupMemberNum = binding.textViewGroupMemberNum

        // 릴레이 현황
        val relayExercisedMemberProfile = binding.layoutRelayProfile.recyclerViewExercisedMemberProfile
        val totalGroupMemberNum = binding.textViewGroupRelayGroupMemberCountDescription
        val exercisedGroupMemberNum = binding.textViewGroupRelayCompleteMemberCountDescription
        val userName = binding.textViewGroupRelayNicknameDescription

        // 참여 전 그룹 멤버
        val recyclerViewUnexercisedMember = binding.recyclerviewTodayRelayUnexercisedMember

    }
}