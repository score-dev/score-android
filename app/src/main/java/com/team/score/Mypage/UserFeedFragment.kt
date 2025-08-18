package com.team.score.Mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.TokenManager
import com.team.score.API.response.user.FeedListResponse
import com.team.score.Group.OtherGroupFeedListFragment
import com.team.score.Home.viewModel.HomeViewModel
import com.team.score.MainActivity
import com.team.score.Mypage.Adapter.FeedAdapter
import com.team.score.R
import com.team.score.Record.FeedDetailFragment
import com.team.score.Record.RecordFragment
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentUserFeedBinding

class UserFeedFragment : Fragment() {

    lateinit var binding: FragmentUserFeedBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(this)[RecordViewModel::class.java]
    }
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
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

        binding = FragmentUserFeedBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        feedAdapter = FeedAdapter(requireContext()).apply {
            itemClickListener = object : FeedAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if (position < 0 || position >= getFeedList.size) return

                    val nextFragment = FeedDetailFragment()
                    val bundle = Bundle().apply {
                        putInt("position", position)
                        putInt("feedId", getFeedList[position].feedId)
                    }

                    nextFragment.arguments = bundle

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        observeViewModel(feedAdapter)

        binding.run {
            buttonRecord.setOnClickListener {
                // 운동 기록 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonBaton.setOnClickListener {
                // 바통 찌르기
                homeViewModel.batonGroupMember(mainActivity, arguments?.getInt("userId") ?: 0) {
                    binding.buttonBaton.run {
                        text = "찌르기 완료!"
                        backgroundTintList =
                            context?.let { ContextCompat.getColorStateList(it, R.color.main) }
                    }
                }
            }

            recyclerViewFeed.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = feedAdapter
            }

            recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    // 마지막 항목이 보이고, 로딩 중이 아니며, 마지막 페이지도 아닐 경우
                    if (!isLoading && !isLastPage && lastVisible >= totalItemCount - 1 && !(isFirstPage == true && isLastPage == true)) {
                        isLoading = true
                        viewModel.getFeedList(mainActivity, arguments?.getInt("userId") ?: 0, currentPage)
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
            feedList.observe(viewLifecycleOwner) { feedResponse ->
                if (currentPage == 0) {
                    feedAdapter.clearFeeds()
                    getFeedList.clear()
                }

                getFeedList.addAll(feedResponse)

                if(isFirstPage) {
                    binding.run {
                        if(feedResponse.isEmpty()) {
                            if(arguments?.getInt("userId") ?: 0 == TokenManager(mainActivity).getUserId()) {
                                layoutEmptyFeed.visibility = View.GONE
                                layoutEmptyMyFeed.visibility = View.VISIBLE
                                textViewMyEmptyTitle.text = "${MyApplication.userNickname}님만의 운동기록을\n채워보세요!"
                                recyclerViewFeed.visibility = View.GONE
                            } else {
                                layoutEmptyFeed.visibility = View.VISIBLE
                                layoutEmptyMyFeed.visibility = View.GONE
                            }
                        } else {
                            layoutEmptyFeed.visibility = View.GONE
                            layoutEmptyMyFeed.visibility = View.GONE
                            recyclerViewFeed.visibility = View.VISIBLE
                        }
                    }
                }

                val imageUrls = feedResponse.map { it.feedImg }
                feedAdapter.addFeeds(imageUrls)

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

        viewModel.getFeedList(mainActivity, arguments?.getInt("userId") ?: 0, currentPage)
    }

    companion object {
        fun newInstance(userId: Int): UserFeedFragment {
            val fragment = UserFeedFragment()
            val bundle = Bundle()
            bundle.putInt("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }
}