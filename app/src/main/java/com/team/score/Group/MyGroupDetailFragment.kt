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
import com.team.score.API.response.group.GroupRankingResponse
import com.team.score.API.response.group.RankerInfo
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.TimeUtil.formatExerciseTime
import com.team.score.Utils.TimeUtil.formatSecondsToMinuteString
import com.team.score.databinding.FragmentMyGroupDetailBinding

class MyGroupDetailFragment : Fragment() {

    lateinit var binding: FragmentMyGroupDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(this)[GroupViewModel::class.java]
    }

    private lateinit var fragmentList: List<Fragment>
    private var groupId: Int = 0
    private var getGroupDetail: GroupDetailResponse? = null
    private var getGroupRanking: GroupRankingResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyGroupDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        groupId = arguments?.getInt("groupId") ?: 0

        // Fragment 리스트 설정
        fragmentList = listOf(
            MyGroupFeedListFragment.newInstance(groupId),
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
                    textViewTodayMemberValue.text = "${getGroupDetail?.numOfExercisedToday}/${getGroupDetail?.numOfTotalMembers} 명"
                }
            }

            groupRanking.observe(viewLifecycleOwner) {
                getGroupRanking = it

                binding.run {
                    layoutMyGroupRanking.run {
                        applyRankingUI(getGroupRanking?.rankersInfo ?: emptyList())
                    }
                }
            }
        }
    }

    fun initView() {

        viewModel.getGroupDetail(mainActivity, arguments?.getInt("groupId") ?: 0)
        viewModel.getGroupRanking(mainActivity, arguments?.getInt("groupId") ?: 0, null)

        binding.run {
            // ViewModel에서 그룹 리스트 중 해당 groupId에 해당하는 그룹 정보 가져오기
            val myGroup = viewModel.myGroupList.value?.find { it.id == groupId }

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
                textViewHead.text = "${myGroup?.name}"
                textViewPublic.visibility = if(myGroup?.private == true) { View.VISIBLE } else { View.GONE }
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
                buttonKebab.setOnClickListener {

                }
            }

            applyRankingUI(getGroupRanking?.rankersInfo ?: emptyList())
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

    fun applyRankingUI(rankersInfo: List<RankerInfo>) {
        binding.layoutMyGroupRanking.run {
            val views = listOf(
                layoutRanking1,
                layoutRanking2,
                layoutRanking3,
                layoutRanking4,
                layoutRanking5
            )

            val graphs = listOf(
                graphRanking1,
                graphRanking2,
                graphRanking3,
                graphRanking4,
                graphRanking5
            )

            val times = listOf(
                textViewRanking1ExerciseTime,
                textViewRanking2ExerciseTime,
                textViewRanking3ExerciseTime,
                textViewRanking4ExerciseTime,
                textViewRanking5ExerciseTime
            )

            val days = listOf(
                textViewRanking1Days,
                textViewRanking2Days,
                textViewRanking3Days,
                textViewRanking4Days,
                textViewRanking5Days
            )

            val profiles = listOf(
                imageViewRanking1Profile,
                imageViewRanking2Profile,
                imageViewRanking3Profile,
                imageViewRanking4Profile,
                imageViewRanking5Profile
            )

            val nicknames = listOf(
                textViewRanking1Nickname,
                textViewRanking2Nickname,
                textViewRanking3Nickname,
                textViewRanking4Nickname,
                textViewRanking5Nickname
            )

            if (rankersInfo.isEmpty()) {
                graphs.forEach { it.visibility = View.GONE }
                return@run
            }

            rankersInfo.forEachIndexed { index, ranker ->
                if (index >= 5) return@forEachIndexed

                val timeText = formatSecondsToMinuteString(ranker.weeklyExerciseTime)
                val hasExercise = ranker.weeklyExerciseTime > 0

                times[index].text = if (hasExercise) timeText else ""  // 기록 없으면 시간 텍스트 없음
                times[index].visibility = if (hasExercise) View.VISIBLE else View.INVISIBLE

                days[index].text = if (index == 0 && hasExercise)
                    "연속 ${ranker.changedAmount}일 째"
                else if (hasExercise)
                    "${ranker.changedAmount}일"
                else
                    ""  // 기록 없으면 일수 텍스트도 생략

                Glide.with(profiles[index].context)
                    .load(ranker.profileImgUrl)
                    .placeholder(R.drawable.img_profile_null)
                    .into(profiles[index])

                // 그래프 뷰 자체도 기록 없으면 숨김
                graphs[index].visibility = if (hasExercise) View.VISIBLE else View.INVISIBLE

                nicknames[index].text = ranker.nickname
            }
        }
    }
}