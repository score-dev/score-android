package com.team.score.Record.BottomSheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.Record.adapter.FeedEmotionMateAdapter
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.databinding.FragmentFeedEmotionBottomSheetBinding


class FeedEmotionBottomSheetFragment(var activity: Activity, var emotions: List<FeedEmotionResponse>?) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentFeedEmotionBottomSheetBinding
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    lateinit var feedEmotionMateAdapter: FeedEmotionMateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedEmotionBottomSheetBinding.inflate(inflater, container, false)

        initAdapter()

        return binding.root
    }

    fun initAdapter() {
        feedEmotionMateAdapter = FeedEmotionMateAdapter(
            activity,
            emotions
        )

        binding.run {
            recyclerViewEmotionMate.run {
                adapter = feedEmotionMateAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }
}