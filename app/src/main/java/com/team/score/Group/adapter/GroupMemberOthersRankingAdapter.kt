package com.team.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.group.GroupRanking
import com.team.score.API.response.group.RankerInfo
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.TimeUtil
import com.team.score.databinding.RowGroupMemberRankingOthersBinding
import com.team.score.databinding.RowGroupRankingOthersBinding

class GroupMemberOthersRankingAdapter(
    private var activity: MainActivity,
    private var rankingInfos: List<RankerInfo>?
) :
    RecyclerView.Adapter<GroupMemberOthersRankingAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newRankings: List<RankerInfo>?) {
        rankingInfos = newRankings
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowGroupMemberRankingOthersBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = rankingInfos?.get(position)

        with(holder.binding) {
            textViewNickname.text = item?.nickname
            textViewRanking.text = item?.rankNum.toString()
            if((item?.weeklyLevelIncrement ?: 0) >= 500) {
                textViewLevelChange.text = "+ ${(item?.weeklyLevelIncrement ?: 0) / 500}레벨"
            } else {
                textViewLevelChange.text = "+ ${(item?.weeklyLevelIncrement ?: 0)}포인트"
            }


            if((item?.changedAmount ?: 0) < 0) {
                imageViewRankingChange.setImageResource(R.drawable.ic_arrow_down_blue)
                textViewRankingChange.text = "${(item?.changedAmount ?: 0)}"
            } else if((item?.changedAmount ?: 0) == 0) {
                imageViewRankingChange.visibility = View.INVISIBLE
                textViewRankingChange.visibility = View.INVISIBLE
            } else {
                imageViewRankingChange.setImageResource(R.drawable.ic_arrow_up_red)
                textViewRankingChange.text = "${(item?.changedAmount ?: 0)}"
            }
        }
    }

    override fun getItemCount() = (rankingInfos?.size ?: 0)

    inner class ViewHolder(val binding: RowGroupMemberRankingOthersBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}