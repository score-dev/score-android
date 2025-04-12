package com.team.score.SignUp.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.neis.response.SchoolDto
import com.team.score.databinding.RowSchoolBinding

class SchoolListAdapter(
    private var activity: Activity,
    private var schools: List<SchoolDto>?
) :
    RecyclerView.Adapter<SchoolListAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newSchools: List<SchoolDto>?) {
        schools = newSchools
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowSchoolBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = schools?.get(position)?.SCHUL_NM
        holder.address.text = schools?.get(position)?.ORG_RDNMA
    }

    override fun getItemCount() = schools?.size ?: 0

    inner class ViewHolder(val binding: RowSchoolBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.textViewSchoolName
        val address = binding.textViewSchoolAddress


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