package com.team.score.Group

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
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
import com.team.score.BasicDialog
import com.team.score.BasicDialogInterface
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.Mypage.Setting.NotificationSettingFragment
import com.team.score.Mypage.Setting.SettingFragment
import com.team.score.Mypage.UserCalendarFragment
import com.team.score.Mypage.UserFeedFragment
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.R
import com.team.score.ReportDialog
import com.team.score.ReportDialogInterface
import com.team.score.Utils.TimeUtil.formatExerciseTimeToKorean
import com.team.score.databinding.FragmentMateDetailBinding
import kotlinx.coroutines.channels.ticker

class MateDetailFragment : Fragment(), ReportDialogInterface {

    lateinit var binding: FragmentMateDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(this)[GroupViewModel::class.java]
    }

    var getUserInfo: UserInfoResponse? = null

    private lateinit var fragmentList: List<Fragment>

    var type: MateType? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMateDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val userId = arguments?.getInt("userId") ?: 0
        fragmentList = listOf(
            UserFeedFragment.newInstance(userId),
            UserCalendarFragment.newInstance(userId)
        )

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
                    showPopUp()
                }
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun showPopUp() {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_mate_item, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 50f
        }

        // ✅ 블러 뷰 추가
        val blurOverlay = View(mainActivity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#4D000000"))
            isClickable = true // 뒤쪽 클릭 막기
        }
        val rootView = mainActivity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(blurOverlay)

        popupWindow.setOnDismissListener {
            rootView.removeView(blurOverlay)
        }

        popupView.findViewById<LinearLayout>(R.id.layout_mate_block).setOnClickListener {
            // 메이트 차단하기
            type = MateType.BLOCK

            setNotification()

            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.layout_mate_report).setOnClickListener {
            // 메이트 신고하기
            type = MateType.REPORT

            setNotification()

            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(binding.toolbar.buttonKebab, -200, 50)
    }

    fun setNotification() {
        // 다이얼로그
        val title = type?.title ?: ""
        val description = type?.description ?: ""

        val dialog = ReportDialog(this@MateDetailFragment, title, description, "예", "아니요")
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let {
            dialog.show(it.supportFragmentManager, "MateDialog")
        }
    }

    override fun onClickRightButton() {

    }

    override fun onClickLeftButton() {
        when(type) {
            MateType.BLOCK -> {
                // 메이트 차단하기
            }

            MateType.REPORT -> {
                // 메이트 신고하기
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MateReportFragment())
                    .commit()
            }

            else -> { }
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

    enum class MateType(val title: String, val description: String) {
        BLOCK("메이트를 차단하시겠어요?", "차단 시 메이트의 피드를 볼 수 없어요"),
        REPORT("메이트를 신고하시겠어요?", "5명 이상의 신고를 받은 메이트는\n제재가 가해질 수 있어요")
    }
}