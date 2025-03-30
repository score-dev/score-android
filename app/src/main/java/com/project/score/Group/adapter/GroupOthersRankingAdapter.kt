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
import com.project.score.Utils.TimeUtil
import com.project.score.databinding.RowGroupRankingOthersBinding
import com.project.score.databinding.RowHomeGroupBinding
import com.project.score.databinding.RowMyGroupRankingBinding

class GroupOthersRankingAdapter(
    private var activity: MainActivity,
    private var groupInfos: List<GroupRanking>?
) :
    RecyclerView.Adapter<GroupOthersRankingAdapter.ViewHolder>() {

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
            RowGroupRankingOthersBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(activity).load(groupInfos?.get(position)?.groupImg).into(holder.profile)
        holder.groupName.text = groupInfos?.get(position)?.groupName

        if(groupInfos?.get(position)?.private == true) {
            holder.textViewIsPublic.visibility = View.VISIBLE
        } else {
            holder.textViewIsPublic.visibility = View.GONE
        }
        holder.groupMemberNum.text = "${groupInfos?.get(position)?.currentMemberNum}/${groupInfos?.get(position)?.maxMemberNum}명"

        holder.totalExerciseTime.text = TimeUtil.formatExerciseTime(groupInfos?.get(position)?.weeklyExerciseTime ?: 0)
        holder.participateRate.text = "${groupInfos?.get(position)?.participateRatio}%"
        holder.ranking.text = "${groupInfos?.get(position)?.rank}"


        if((groupInfos?.get(position)?.rankChangeAmount ?: 0) < 0) {
            holder.rankingChangeArrow.setImageResource(R.drawable.ic_arrow_down_blue)
            holder.rankingChange.text = "${-(groupInfos?.get(position)?.rankChangeAmount ?: 0)}"
        } else if((groupInfos?.get(position)?.rankChangeAmount ?: 0) == 0) {
            holder.rankingChangeArrow.visibility = View.INVISIBLE
            holder.rankingChange.visibility = View.INVISIBLE
        } else {
            holder.rankingChangeArrow.setImageResource(R.drawable.ic_arrow_up_red)
            holder.rankingChange.text = "${(groupInfos?.get(position)?.rankChangeAmount ?: 0)}"
        }
    }

    override fun getItemCount() = (groupInfos?.size ?: 0)

    inner class ViewHolder(val binding: RowGroupRankingOthersBinding) : RecyclerView.ViewHolder(binding.root) {
        val profile = binding.imageViewGroupProfile
        val groupName = binding.textViewGroupName
        val textViewIsPublic = binding.textViewPublic
        val groupMemberNum = binding.textViewGroupMember
        val totalExerciseTime = binding.textViewGroupWeeklyTotalExerciseTimeValue
        val participateRate = binding.textViewGroupWeeklyParticipateRateValue
        val ranking = binding.textViewRanking
        val rankingChange = binding.textViewRankingChange
        val rankingChangeArrow = binding.imageViewRankingChange
    }
}