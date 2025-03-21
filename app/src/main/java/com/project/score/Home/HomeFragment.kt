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
import com.project.score.API.response.home.HomeResponse
import com.project.score.Home.adapter.GroupRelayAdapter
import com.project.score.Home.adapter.WeeklyCalendarAdapter
import com.project.score.Login.viewModel.UserViewModel
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.Utils.DynamicSpacingItemDecoration
import com.project.score.Utils.EqualSpacingItemDecoration
import com.project.score.Utils.MyApplication
import com.project.score.Utils.VerticalSpacingItemDecoration
import com.project.score.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    lateinit var weeklyGraphAdapter : WeeklyCalendarAdapter
    private lateinit var groupPagerAdapter: GroupRelayAdapter

    var getHomeData: HomeResponse? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

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



    fun initView() {

        binding.run {
            Log.d("##", "user info : ${MyApplication.userInfo}")
            textViewNickname.text = "${MyApplication.userInfo?.nickname}님,"
        }
    }
}