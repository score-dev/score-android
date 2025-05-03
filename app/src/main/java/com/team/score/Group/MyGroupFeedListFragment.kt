package com.team.score.Group

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.API.response.user.FeedListResponse
import com.team.score.API.weather.response.Main
import com.team.score.Group.adapter.MyGroupFeedAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.Mypage.Adapter.FeedAdapter
import com.team.score.R
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentMyGroupFeedListBinding

class MyGroupFeedListFragment : Fragment() {

    lateinit var binding: FragmentMyGroupFeedListBinding
    lateinit var mainActivity: MainActivity

    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    lateinit var feedAdapter: MyGroupFeedAdapter

    var isLoading = true
    var isLastPage = false
    var isFirstPage = false
    var currentPage = 0
    val pageSize = 20

    var getFeedList: MutableList<FeedListResponse> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyGroupFeedListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        feedAdapter = MyGroupFeedAdapter(mainActivity, requireContext(), viewModel)

        observeViewModel(feedAdapter)

        binding.run {
            recyclerViewGroupFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    // 마지막 항목이 보이고, 로딩 중이 아니며, 마지막 페이지도 아닐 경우
                    if (!isLoading && !isLastPage && lastVisible >= totalItemCount - 1 && !(isFirstPage == true && isLastPage == true)) {
                        Log.d("##", "reload")
                        isLoading = true
                        viewModel.getGroupFeedList(mainActivity, arguments?.getInt("groupId") ?: 0, currentPage)
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

    fun observeViewModel(feedAdapter: MyGroupFeedAdapter) {
        viewModel.run {
            groupFeedList.observe(viewLifecycleOwner) { feedResponse ->
                for(feed in feedResponse) {
                    getFeedList.add(feed)
                }

                getFeedList.forEach { feed ->
                    viewModel.getFeedEmotion(mainActivity, feed.feedId)
                }


                binding.recyclerViewGroupFeed.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    adapter = feedAdapter
                }

                if (currentPage == 0) feedAdapter.clearFeeds()

                feedAdapter.addFeeds(getFeedList)

                // 다음 페이지 준비
                isLoading = false
                currentPage++
            }

            lastFeed.observe(viewLifecycleOwner) {
                // 마지막 페이지 확인
                isLastPage = it
            }

            firstFeed.observe(viewLifecycleOwner) {
                // 첫 페이지 확인
                isFirstPage = it
            }

            feedEmotionsMap.observe(viewLifecycleOwner) { emotionMap ->
                feedAdapter.updateAllEmotions(emotionMap)
            }
        }
    }

    fun initView() {
        // 기존 리스트 초기화
        feedAdapter.clearFeeds()

        currentPage = 0
        isLastPage = false
        isLoading = true

        viewModel.getGroupFeedList(mainActivity, arguments?.getInt("groupId") ?: 0, currentPage)
    }

    companion object {
        fun newInstance(groupId: Int): MyGroupFeedListFragment {
            val fragment = MyGroupFeedListFragment()
            val bundle = Bundle()
            bundle.putInt("groupId", groupId)
            fragment.arguments = bundle
            return fragment
        }
    }

}