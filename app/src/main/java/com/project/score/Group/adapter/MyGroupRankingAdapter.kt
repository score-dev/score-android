package com.project.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.score.API.response.group.GroupRanking
import com.project.score.API.response.home.HomeGroupInfo
import com.project.score.Group.CreateGroupFragment
import com.project.score.Home.adapter.GroupExercisedMemberProfileAdapter
import com.project.score.Home.adapter.GroupRelayTodayUnexercisedMemberAdapter
import com.project.score.Home.viewModel.HomeViewModel
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.Utils.DynamicSpacingItemDecoration
import com.project.score.Utils.MyApplication
import com.project.score.databinding.RowHomeGroupBinding
import com.project.score.databinding.RowMyGroupRankingBinding

class MyGroupRankingAdapter(
    private var activity: MainActivity,
    private var groupInfos: List<GroupRanking>?
) :
    RecyclerView.Adapter<MyGroupRankingAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newGroups: List<GroupRanking>?) {
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
            RowMyGroupRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(groupInfos?.get(position) == null) {
            holder.profile.setImageResource(R.drawable.img_profile_null)
            holder.groupName.text = "내그룹"
            holder.totalExerciseTime.text = "-시간"
            holder.participateRate.text = "-%"
            holder.ranking.text = "-위"
        } else {
            Glide.with(activity).load(groupInfos?.get(position)?.groupImg).into(holder.profile)
            holder.groupName.text = groupInfos?.get(position)?.groupName
            holder.totalExerciseTime.text = "${groupInfos?.get(position)?.weeklyExerciseTime?.div(60)}"
            holder.participateRate.text = "${groupInfos?.get(position)?.participateRatio}%"
            holder.ranking.text = "${groupInfos?.get(position)?.rank}위"
        }
    }

    override fun getItemCount() = (groupInfos?.size ?: 0) + 1

    inner class ViewHolder(val binding: RowMyGroupRankingBinding) : RecyclerView.ViewHolder(binding.root) {
        val profile = binding.imageViewGroupProfile
        val groupName = binding.textViewGroupName
        val totalExerciseTime = binding.textViewGroupTotalExerciseTimeValue
        val participateRate = binding.textViewGroupParticipationRateValue
        val ranking = binding.textViewGroupRankingValue
    }
}