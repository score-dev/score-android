package com.team.score.Home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.R
import com.team.score.databinding.RowRelayMemberProfileBinding

class GroupExercisedMemberProfileAdapter(
    private var activity: Activity,
    private var memberNum: Int,
    private var exercisedMemberProfiles: List<String>?
) :
    RecyclerView.Adapter<GroupExercisedMemberProfileAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newMemberNum: Int, newMemberProfiles: List<String>?) {
        memberNum = newMemberNum
        exercisedMemberProfiles = newMemberProfiles
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(memberId: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowRelayMemberProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.profile.visibility = View.VISIBLE

        if (position < (exercisedMemberProfiles?.size ?: 0)) {
            Glide.with(activity)
                .load(exercisedMemberProfiles?.get(position))
                .into(holder.profile)
            holder.profileBackground.setBackgroundResource(R.drawable.background_main_circle)
        } else {
            holder.profile.setImageResource(R.drawable.img_profile_null)
            holder.profileBackground.setBackgroundResource(0)
        }
    }

    override fun getItemCount() = minOf(5, memberNum)

    inner class ViewHolder(val binding: RowRelayMemberProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val profileBackground = binding.layoutExercisedMemberProfile
        val profile = binding.imageViewExercisedMemberProfile
    }
}