package com.project.score.Mypage

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.score.API.response.login.UserInfoResponse
import com.project.score.Login.viewModel.UserViewModel
import com.project.score.MainActivity
import com.project.score.Mypage.Setting.QnaFragment
import com.project.score.Mypage.Setting.SettingFragment
import com.project.score.Mypage.Setting.WithdrawFragment
import com.project.score.Mypage.viewModel.MypageViewModel
import com.project.score.R
import com.project.score.Utils.TimeUtil.formatExerciseTimeToKorean
import com.project.score.databinding.FragmentMypageMainBinding

class MypageMainFragment : Fragment() {

    private lateinit var binding: FragmentMypageMainBinding
    private lateinit var mainActivity: MainActivity
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    var getUserInfo: UserInfoResponse? = null

    // Fragment 리스트를 onCreateView에서만 설정
    private val fragmentList = listOf(
        MypageFeedFragment(),
        MypageCalendarFragment()
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageMainBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        observeViewModel()

        binding.run {
            buttonMate.setOnClickListener {

            }
            buttonEditProfile.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MypageProfileEditFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

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
                   Glide.with(this@MypageMainFragment)
                       .load(getUserInfo?.profileImgUrl)
                       .into(imageViewProfile)
                   textViewNotificationTime.text = "${formatExerciseTimeToKorean(getUserInfo?.goal ?: "00:00:00")}"

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
        viewModel.getUserInfo(mainActivity)

        mainActivity.hideBottomNavigation(false)

        binding.run {
            // 탭에 표시할 이름
            val tabName = arrayOf("피드", "캘린더")

            pagerTab.bringToFront()
            toolbar.run {
                textViewHead.bringToFront()
                buttonSetting.bringToFront()
                buttonBack.bringToFront()
            }

            pagerTab.isUserInputEnabled = false
            pagerTab.adapter = TabAdapterClass(mainActivity, fragmentList)

            // 탭 구성
            TabLayoutMediator(tab, pagerTab) { tab: TabLayout.Tab, i: Int ->
                tab.text = tabName[i]
            }.attach()

            toolbar.run {
                textViewHead.text = "마이페이지"
                buttonSetting.setOnClickListener {
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, SettingFragment())
                        .addToBackStack(null)
                        .commit()
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
