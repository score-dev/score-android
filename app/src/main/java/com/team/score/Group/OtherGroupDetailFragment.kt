package com.team.score.Group

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.team.score.API.response.group.GroupDetailResponse
import com.team.score.API.weather.response.Main
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.Mypage.MypageFeedFragment
import com.team.score.R
import com.team.score.Utils.TimeUtil.formatExerciseTime
import com.team.score.databinding.FragmentOtherGroupDetailBinding

class OtherGroupDetailFragment : Fragment() {

    lateinit var binding: FragmentOtherGroupDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    private lateinit var fragmentList: List<Fragment>
    private var groupId: Int = 0
    private var getGroupDetail: GroupDetailResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOtherGroupDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        groupId = arguments?.getInt("groupId") ?: 0

        // Fragment 리스트 설정
        fragmentList = listOf(
            OtherGroupFeedListFragment.newInstance(groupId),
            MyGroupRankingFragment.newInstance(groupId),
            GroupMateListFragment.newInstance(groupId)
        )

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {
            groupDetail.observe(viewLifecycleOwner) {
                getGroupDetail = it
                Log.d("##", "group detail : ${getGroupDetail}")

                binding.run {
                    toolbar.run {
                        textViewHead.text = getGroupDetail?.groupName
                        textViewPublic.visibility = if(getGroupDetail?.private == true) { View.VISIBLE } else { View.GONE }
                    }

                    textViewParticipationValue.text = "${getGroupDetail?.averageParticipateRatio}%"
                    textViewExerciseTimeValue.text = "${formatExerciseTime(getGroupDetail?.cumulativeTime ?: 0)}"

                    Glide.with(mainActivity).load(getGroupDetail?.groupImg)
                        .into(imageViewGroup)
                    textViewGroupName.text = "${getGroupDetail?.groupName}"
                    textViewGroupMemberValue.text = "${getGroupDetail?.numOfTotalMembers}/${getGroupDetail?.userLimit} 명"
                }
            }
        }
    }

    fun initView() {
        viewModel.getGroupDetail(mainActivity, arguments?.getInt("groupId") ?: 0)

        binding.run {
            root.requestLayout()

            // 탭에 표시할 이름
            val tabName = arrayOf("피드", "랭킹", "메이트")

            pagerTab.bringToFront()

            pagerTab.isUserInputEnabled = false
            pagerTab.adapter = TabAdapterClass(mainActivity, fragmentList)

            // 탭 구성
            TabLayoutMediator(tab, pagerTab) { tab: TabLayout.Tab, i: Int ->
                tab.text = tabName[i]
            }.attach()

            toolbar.run {
                textViewHead.text = "${getGroupDetail?.groupName}"
                textViewPublic.visibility = if(getGroupDetail?.private == true) { View.VISIBLE } else { View.GONE }
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
                buttonKebab.setOnClickListener {

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