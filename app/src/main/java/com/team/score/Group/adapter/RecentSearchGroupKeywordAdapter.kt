package com.team.score.Group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.group.GroupInfoResponse
import com.team.score.MainActivity
import com.team.score.Utils.MyApplication
import com.team.score.databinding.RowGroupSearchBinding
import com.team.score.databinding.RowSearchWordBinding

class RecentSearchGroupKeywordAdapter(
    private var activity: MainActivity,
    private var keywords: List<String>?
) :
    RecyclerView.Adapter<RecentSearchGroupKeywordAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newKeywords: List<String>?) {
        keywords = newKeywords
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowSearchWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding) {
           textViewSearch.text = keywords?.get(position) ?: ""
        }
    }

    override fun getItemCount() = (keywords?.size ?: 0)

    inner class ViewHolder(val binding: RowSearchWordBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonDelete.setOnClickListener {
                MyApplication.preferences.removeRecentSearch(activity, MyApplication.preferences.getRecentSearchesLimited(activity).get(position))
                updateList(MyApplication.preferences.getRecentSearchesLimited(activity))
            }

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}