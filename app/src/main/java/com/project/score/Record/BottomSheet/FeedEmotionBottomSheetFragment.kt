package com.project.score.Record.BottomSheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.score.API.neis.response.SchoolDto
import com.project.score.API.request.signUp.UserSchool
import com.project.score.API.request.user.UserSchoolInfo
import com.project.score.API.response.record.FeedEmotionResponse
import com.project.score.BuildConfig
import com.project.score.Login.viewModel.UserViewModel
import com.project.score.R
import com.project.score.Record.adapter.FeedEmotionMateAdapter
import com.project.score.Record.viewModel.RecordViewModel
import com.project.score.SignUp.adapter.SchoolListAdapter
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentFeedEmotionBottomSheetBinding
import com.project.score.databinding.FragmentSignUpSchoolBottomSheetBinding



class FeedEmotionBottomSheetFragment(var activity: Activity, var emotions: List<FeedEmotionResponse>?) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentFeedEmotionBottomSheetBinding
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    lateinit var feedEmotionMateAdapter: FeedEmotionMateAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedEmotionBottomSheetBinding.inflate(inflater, container, false)

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewEmotionMate.run {
                adapter = feedEmotionMateAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        return binding.root
    }

    fun observeViewModel() {
        viewModel.run {
            feedEmotion.observe(viewLifecycleOwner) {
                emotions = it

                feedEmotionMateAdapter.updateList(emotions)
            }
        }
    }

    fun initAdapter() {
        feedEmotionMateAdapter = FeedEmotionMateAdapter(
            activity,
            emotions
        )
    }
}