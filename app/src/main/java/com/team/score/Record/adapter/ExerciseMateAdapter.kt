package com.team.score.Record.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.record.FriendResponse
import com.team.score.MainActivity
import com.team.score.databinding.RowExerciseMateProfileBinding

class ExerciseMateAdapter(
    private var activity: MainActivity,
    private var mates: List<FriendResponse>
) :
    RecyclerView.Adapter<ExerciseMateAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newMates: List<FriendResponse>) {
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
            RowExerciseMateProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewMateName.text = mates[position].nickname
            Glide.with(activity).load(mates[position].profileImgUrl).into(imageViewExercisedMemberProfile)
        }
    }

    override fun getItemCount() = mates.size ?: 0


    inner class ViewHolder(val binding: RowExerciseMateProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)

                true
            }
        }
    }
}