package com.team.score.Group

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.team.score.API.TokenManager
import com.team.score.API.response.login.UserInfoResponse
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.Mypage.Setting.SettingFragment
import com.team.score.Mypage.UserCalendarFragment
import com.team.score.Mypage.UserFeedFragment
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.R
import com.team.score.Utils.TimeUtil.formatExerciseTimeToKorean
import com.team.score.databinding.FragmentMateDetailBinding

class MateDetailFragment : Fragment() {

    lateinit var binding: FragmentMateDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    var getUserInfo: UserInfoResponse? = null

    // Fragment 리스트를 onCreateView에서만 설정
    private val fragmentList = listOf(
        UserFeedFragment.newInstance(TokenManager(mainActivity).getUserId()),
        UserCalendarFragment.newInstance(TokenManager(mainActivity).getUserId())
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMateDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeViewModel() {
        viewModel.run {
            userInfo.observe(viewLifecycleOwner) {
                getUserInfo = it

                binding.run {
                    textViewNickname.text = getUserInfo?.nickname
                    textViewSchoolGrade.text = "${getUserInfo?.schoolName} ${getUserInfo?.grade}학년"
                    Glide.with(mainActivity)
                        .load(getUserInfo?.profileImgUrl)
                        .into(imageViewProfile)

                    // 레벨 layout
                    layoutLevel.run {
                        Glide.with(mainActivity).load(getUserInfo?.profileImgUrl).into(imageViewLevelProfile)
                        textViewLevel.text = "Lv.${getUserInfo?.level}"
                        textViewLevelPoint.text = "${500 - (getUserInfo?.point ?: 0)} 포인트"

                        val pointRatio = (getUserInfo?.point ?: 0) / 500f
                        val params = graphLevelMyStatus.layoutParams as ConstraintLayout.LayoutParams
                        params.matchConstraintPercentWidth = pointRatio
                        graphLevelMyStatus.layoutParams = params
                    }
                }
            }
        }
    }

    fun initView() {
        viewModel.getUserInfo(mainActivity, arguments?.getInt("userId") ?: 0)

        mainActivity.hideBottomNavigation(false)

        binding.run {
            root.requestLayout()

            // 탭에 표시할 이름
            val tabName = arrayOf("피드", "캘린더")

            pagerTab.bringToFront()
            toolbar.run {
                buttonKebab.bringToFront()
                buttonBack.bringToFront()
            }

            pagerTab.isUserInputEnabled = false
            pagerTab.adapter = TabAdapterClass(mainActivity, fragmentList)

            // 탭 구성
            TabLayoutMediator(tab, pagerTab) { tab: TabLayout.Tab, i: Int ->
                tab.text = tabName[i]
            }.attach()

            toolbar.run {
                buttonKebab.setOnClickListener {
                    // 메이트 차단 or 신고

                }
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    // adapter 클래스
    inner class TabAdapterClass(
        fragmentActivity: FragmentActivity,
        private val fragments: List<Fragment> // Fragment 리스트를 전달받음
    ) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }

}