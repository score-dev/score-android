package com.team.score.Home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.team.score.Utils.CalendarUtil

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.home.HomeResponse
import com.team.score.Group.MyGroupDetailFragment
import com.team.score.Group.MyGroupListFragment
import com.team.score.Group.adapter.MyGroupListAdapter
import com.team.score.Home.adapter.GroupRelayAdapter
import com.team.score.Home.adapter.WeeklyCalendarAdapter
import com.team.score.Home.viewModel.HomeViewModel
import com.team.score.MainActivity
import com.team.score.Mypage.MypageMainFragment
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.R
import com.team.score.Utils.DynamicSpacingItemDecoration
import com.team.score.Utils.TimeUtil.formatExerciseTime
import com.team.score.Utils.VerticalSpacingItemDecoration
import com.team.score.databinding.FragmentHomeBinding

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

        // 알림 권한 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            // 푸쉬 권한 없음 -> 권한 요청
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                123
            )
        } else {
            // 이미 권한이 있는 경우 바로 화면 전환
        }


        initAdapter()
        observeViewModel()

        binding.run {
            layoutMore.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MyGroupListFragment())
                    .addToBackStack(null)
                    .commit()
            }

            layoutWeeklyResult.recyclerViewGraph.apply {
                adapter = weeklyGraphAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                addItemDecoration(VerticalSpacingItemDecoration(9))
            }

            viewPagerGroupList.apply {
                isNestedScrollingEnabled = false
                clipToPadding = false
                clipChildren = false
                offscreenPageLimit = 1
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
                val spacingRatio = 0.15f
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
                    textViewNickname.text = "${getHomeData?.nickname}님,"
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
                        recyclerViewGraph.visibility = View.VISIBLE
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

        mainActivity.hideBottomNavigation(false)

        binding.run {
            layoutWeeklyResult.recyclerViewGraph.visibility = View.INVISIBLE
            toolbar.layoutMypage.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MypageMainFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    // Fragment에서 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}