package com.project.score.Home

import android.os.Bundle
import com.project.score.Utils.CalendarUtil

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kakao.sdk.user.model.User
import com.project.score.API.response.home.HomeResponse
import com.project.score.Group.CreateGroupFragment
import com.project.score.Home.adapter.GroupRelayAdapter
import com.project.score.Home.adapter.WeeklyCalendarAdapter
import com.project.score.Home.viewModel.HomeViewModel
import com.project.score.Login.viewModel.UserViewModel
import com.project.score.MainActivity
import com.project.score.Mypage.MypageMainFragment
import com.project.score.Mypage.viewModel.MypageViewModel
import com.project.score.R
import com.project.score.Utils.DynamicSpacingItemDecoration
import com.project.score.Utils.EqualSpacingItemDecoration
import com.project.score.Utils.MyApplication
import com.project.score.Utils.TimeUtil.formatExerciseTime
import com.project.score.Utils.VerticalSpacingItemDecoration
import com.project.score.databinding.FragmentHomeBinding
import okhttp3.internal.concurrent.formatDuration

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: HomeViewModel
    private val mypageViewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    lateinit var weeklyGraphAdapter : WeeklyCalendarAdapter
    private lateinit var groupPagerAdapter: GroupRelayAdapter

    var getHomeData: HomeResponse? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[HomeViewModel::class.java]


        initAdapter()
        observeViewModel()

        binding.run {
            layoutWeeklyResult.recyclerViewGraph.apply {
                adapter = weeklyGraphAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                addItemDecoration(VerticalSpacingItemDecoration(9))
            }

            viewPagerGroupList.apply {
                clipToPadding = false
                clipChildren = false
                offscreenPageLimit = 1  // 한 개의 추가 페이지를 미리 로드
                setPadding(24, 0, 24, 0) // 좌우 패딩 추가 (숫자는 조정 가능)
                setPageTransformer { page, position ->
                    val scaleFactor = 0.85f + (1 - Math.abs(position)) * 0.15f
                    page.scaleY = scaleFactor
                    page.scaleX = scaleFactor
                    page.alpha = 0.5f + (1 - Math.abs(position)) * 0.5f
                }
            }

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        val weekDates = CalendarUtil.getCurrentWeekDates() // 이번 주 날짜 가져오기
        val exerciseResults = getHomeData?.weeklyExerciseTimeByDay ?: List(7) { 0 } // 운동 결과 데이터

        weeklyGraphAdapter = WeeklyCalendarAdapter(mainActivity, weekDates, exerciseResults)
        groupPagerAdapter = GroupRelayAdapter(
            mainActivity,
            getHomeData?.groupsInfo,
            viewModel
        )

        binding.run {
            layoutWeeklyResult.recyclerViewGraph.apply {
                adapter = weeklyGraphAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                val spacingRatio = 0.3f // 전체 아이템 넓이 대비 20% 간격
                addItemDecoration(
                    DynamicSpacingItemDecoration(spacingRatio)
                )
            }

            viewPagerGroupList.apply {
                adapter = groupPagerAdapter
                dotsIndicator.attachTo(this)
            }
        }
    }


    fun observeViewModel() {
        viewModel.run {
            homeData.observe(viewLifecycleOwner) {
                getHomeData = it

                binding.run {
                    textViewNickname.text = "${getHomeData?.nickname}님"
                    textViewExerciseDay.text = "${getHomeData?.consecutiveDate}일 연속 운동 중이에요!"

                    // 레벨 layout
                    layoutLevel.run {
                        Glide.with(mainActivity).load(getHomeData?.profileImgUrl).into(imageViewLevelProfile)
                        textViewLevel.text = "Lv.${getHomeData?.level}"
                        textViewLevelPoint.text = "${500 - (getHomeData?.point ?: 0)} 포인트"

                        val pointRatio = (getHomeData?.point ?: 0) / 500f
                        val params = graphLevelMyStatus.layoutParams as ConstraintLayout.LayoutParams
                        params.matchConstraintPercentWidth = pointRatio
                        graphLevelMyStatus.layoutParams = params
                    }


                    // 주간 운동 기록 layout
                    layoutWeeklyResult.run {
                        val weekDates = CalendarUtil.getCurrentWeekDates() // 이번 주 날짜 가져오기
                        val exerciseResults = getHomeData?.weeklyExerciseTimeByDay ?: List(7) { 0 } // 운동 결과 데이터
                        weeklyGraphAdapter.updateList(weekDates, exerciseResults)

                        textViewWeeklyExerciseDistanceValue.text = formatExerciseTime(getHomeData?.weeklyTotalExerciseTime ?: 0)
                        textViewWeeklyExerciseDaysValue.text = "${getHomeData?.weeklyExerciseCount}일"
                    }

                    // 내 그룹 layout
                    textViewGroupCount.text = getHomeData?.numOfGroups.toString()

                    groupPagerAdapter = GroupRelayAdapter(
                        mainActivity,
                        getHomeData?.groupsInfo,
                        viewModel
                    )
                    viewPagerGroupList.adapter = groupPagerAdapter
                    dotsIndicator.attachTo(viewPagerGroupList)
                }
            }
        }
    }

    fun initView() {
        viewModel.getHomeData(mainActivity)
        mypageViewModel.getUserInfo(mainActivity)

        binding.run {
            toolbar.layoutMypage.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MypageMainFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}