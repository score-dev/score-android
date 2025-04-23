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
import com.team.score.API.response.record.FriendResponse
import com.team.score.Record.adapter.FeedExerciseMateAdapter
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.databinding.FragmentRecordFeedMateBottomSheetBinding

class RecordFeedMateBottomSheetFragment(var activity: Activity, var mates: MutableList<FriendResponse>?) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentRecordFeedMateBottomSheetBinding
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    lateinit var exerciseMateAdapter: FeedExerciseMateAdapter

    var isLoading = true
    var isLastPage = false
    var isFirstPage = false
    var currentPage = 0
    val pageSize = 20

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordFeedMateBottomSheetBinding.inflate(inflater, container, false)

        initAdapter()
        observeViewModel()

        binding.run {
            layoutSearch.editTextSearch.run {
                hint = "메이트 검색"
                setOnEditorActionListener { v, actionId, event ->
                    viewModel.getSearchExerciseFriend(activity, layoutSearch.editTextSearch.text.toString())

                    true
                }
            }

            recyclerViewMate.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    // 마지막 항목이 보이고, 로딩 중이 아니며, 마지막 페이지도 아닐 경우
                    if (!isLoading && !isLastPage && lastVisible >= totalItemCount - 1 && !(isFirstPage == true && isLastPage == true)) {
                        Log.d("##", "reload")
                        isLoading = true
                        viewModel.getFriendList(activity, currentPage)
                    }
                }
            })
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {
            friendList.observe(viewLifecycleOwner) { friendsResponse ->
                for (friends in friendsResponse) {
                    mates?.add(friends)
                }

                binding.recyclerViewMate.apply {
                    adapter = exerciseMateAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }

                if (currentPage == 0) exerciseMateAdapter.clearFriends()

                val friendsList = mutableListOf<FriendResponse>()
                for (i in 0..<(mates?.size ?: 0)) {
                    friendsList.add(
                        FriendResponse(
                            mates?.get(i)?.id!!,
                            mates?.get(i)?.nickname!!,
                            mates?.get(i)?.profileImgUrl!!
                        )
                    )
                }

                exerciseMateAdapter.addFriends(friendsList)

                // 다음 페이지 준비
                isLoading = false
                currentPage++
            }

            lastFriend.observe(viewLifecycleOwner) {
                // 마지막 페이지 확인
                isLastPage = it
            }

            firstFriend.observe(viewLifecycleOwner) {
                // 첫 페이지 확인
                isFirstPage = it
            }

            searchFriendList.observe(viewLifecycleOwner) { friendsResponse ->
                if (friendsResponse != null) {
                    for (friends in friendsResponse) {
                        mates?.add(friends)
                    }
                }

                binding.recyclerViewMate.apply {
                    adapter = exerciseMateAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }

                if (currentPage == 0) exerciseMateAdapter.clearFriends()

                val friendsList = mutableListOf<FriendResponse>()
                for (i in 0..<(mates?.size ?: 0)) {
                    friendsList.add(
                        FriendResponse(
                            mates?.get(i)?.id!!,
                            mates?.get(i)?.nickname!!,
                            mates?.get(i)?.profileImgUrl!!
                        )
                    )
                }

                exerciseMateAdapter.addFriends(friendsList)
            }
        }
    }

    fun initAdapter() {
        exerciseMateAdapter = FeedExerciseMateAdapter(activity).apply {
            itemClickListener = object : FeedExerciseMateAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    dismiss()
                }
            }
        }
    }

    fun initView() {
        exerciseMateAdapter.clearFriends()

        currentPage = 0
        isLastPage = false
        isLoading = true
    }
}