package com.team.score.Group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.user.FeedListResponse
import com.team.score.Group.MyGroupRankingFragment
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.Mypage.Adapter.FeedAdapter
import com.team.score.R
import com.team.score.Record.FeedDetailFragment
import com.team.score.Record.RecordFragment
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentMypageFeedBinding
import com.team.score.databinding.FragmentOtherGroupFeedListBinding

class OtherGroupFeedListFragment : Fragment() {

    lateinit var binding: FragmentOtherGroupFeedListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(this)[GroupViewModel::class.java]
    }

    lateinit var feedAdapter: FeedAdapter

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

        binding = FragmentOtherGroupFeedListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        feedAdapter = FeedAdapter(requireContext())

        observeViewModel(feedAdapter)

        binding.run {
            buttonRecord.setOnClickListener {
                // 운동 기록 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFragment())
                    .addToBackStack(null)
                    .commit()
            }

            recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
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

    fun observeViewModel(feedAdapter: FeedAdapter) {
        viewModel.run {
            groupFeedList.observe(viewLifecycleOwner) { feedResponse ->
                for(feed in feedResponse) {
                    getFeedList.add(feed)
                }

                binding.recyclerViewFeed.apply {
                    layoutManager = GridLayoutManager(context, 3) // 3열 구성
                    adapter = feedAdapter
                }

                binding.run {
                    if(feedResponse.size == 0) {
                        layoutEmptyFeed.visibility = View.VISIBLE
                        textViewEmptyTitle.text = "${MyApplication.userNickname}님만의 운동기록을\n채워보세요!"
                        recyclerViewFeed.visibility = View.GONE
                    } else {
                        layoutEmptyFeed.visibility = View.GONE
                        recyclerViewFeed.visibility = View.VISIBLE
                    }
                }

                if (currentPage == 0) feedAdapter.clearFeeds()

                val imageUrls = mutableListOf<String>()
                for(i in 0..<feedResponse.size) {
                    imageUrls.add(feedResponse[i].feedImg)
                }

                feedAdapter.addFeeds(imageUrls)

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
        }
    }

    fun initView() {
        // 기존 리스트 초기화
        feedAdapter.clearFeeds()

        currentPage = 0
        isLastPage = false
        isLoading = true

        binding.root.requestLayout()

        viewModel.getGroupFeedList(mainActivity, arguments?.getInt("groupId") ?: 0, currentPage)
    }

    companion object {
        fun newInstance(groupId: Int): OtherGroupFeedListFragment {
            val fragment = OtherGroupFeedListFragment()
            val bundle = Bundle()
            bundle.putInt("groupId", groupId)
            fragment.arguments = bundle
            return fragment
        }
    }
}