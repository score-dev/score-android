package com.team.score.Mypage.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.databinding.RowFeedBinding

class FeedAdapter(
    private var context: Context,
) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    private var feedList = mutableListOf<String>() // 이미지 URL 리스트

    fun addFeeds(newFeeds: List<String>) {
        val start = feedList.size
        feedList.addAll(newFeeds)
        notifyItemRangeInserted(start, newFeeds.size)
    }

    fun clearFeeds() {
        feedList = mutableListOf<String>()
        feedList.clear()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(feedList[position]).into(holder.feedImage)
    }

    override fun getItemCount() = feedList.size

    inner class ViewHolder(val binding: RowFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val feedImage = binding.imageViewFeed


        init {
            binding.imageViewFeed.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}