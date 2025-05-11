package com.team.score.Group

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.TokenManager
import com.team.score.API.response.group.GroupRankingResponse
import com.team.score.API.response.group.RankerInfo
import com.team.score.API.response.group.SchoolGroupRankingResponse
import com.team.score.Group.adapter.GroupMemberOthersRankingAdapter
import com.team.score.Group.adapter.GroupOthersRankingAdapter
import com.team.score.Group.adapter.MyGroupRankingAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.CalendarUtil.getCurrentWeekInfo
import com.team.score.Utils.CalendarUtil.moveToNextWeek
import com.team.score.Utils.CalendarUtil.moveToPreviousWeek
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentMyGroupRankingBinding

class MyGroupRankingFragment : Fragment() {

    lateinit var binding: FragmentMyGroupRankingBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(this)[GroupViewModel::class.java]
    }

    lateinit var otherGroupMembersAdapter : GroupMemberOthersRankingAdapter

    var getGroupRankingData: GroupRankingResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyGroupRankingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter(emptyList())
        observeViewModel()

        binding.run {
            buttonLeft.setOnClickListener {
                val weekInfo = moveToPreviousWeek()
                textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
                textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

                val convertedDate = weekInfo.startDate.replace(".", "-")
                viewModel.getGroupRanking(mainActivity, arguments?.getInt("groupId") ?: 0, convertedDate)
            }
            buttonRight.setOnClickListener {
                val weekInfo = moveToNextWeek()
                textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
                textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

                val convertedDate = weekInfo.startDate.replace(".", "-")
                viewModel.getGroupRanking(mainActivity, arguments?.getInt("groupId") ?: 0, convertedDate)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        viewModel.getGroupRanking(mainActivity, arguments?.getInt("groupId") ?: 0, null)
        mainActivity.hideBottomNavigation(false)

        binding.run {
            root.requestLayout()

            val weekInfo = getCurrentWeekInfo()
            textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
            textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

            // 그룹 랭킹 TOP3
            layoutGroupRanking2.imageViewRanking.setImageResource(R.drawable.img_ranking2)
            layoutGroupRanking3.imageViewRanking.setImageResource(R.drawable.img_ranking3)

            Glide.with(mainActivity).load(MyApplication.userInfo?.profileImgUrl)
                .into(layoutGroupMyRanking.imageViewMyProfile)
        }
    }

    fun initAdapter(others: List<RankerInfo>?) {
        otherGroupMembersAdapter = GroupMemberOthersRankingAdapter(
            mainActivity,
            getGroupRankingData?.rankersInfo
        )

        binding.run {
            recyclerViewGroupMemberRankingOthers.apply {
                adapter = otherGroupMembersAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        otherGroupMembersAdapter.updateList(others)
    }

    fun observeViewModel() {
        viewModel.groupRanking.observe(viewLifecycleOwner) { it ->
            getGroupRankingData = it
            if (it != null) {
                getGroupRankingData?.rankersInfo = it.rankersInfo.sortedBy { it.rankNum }
            }

            val top3 = getGroupRankingData?.rankersInfo?.take(3)
            val others = getGroupRankingData?.rankersInfo?.drop(3)
            val myRanking = getGroupRankingData?.rankersInfo?.find { it.userId == TokenManager(mainActivity).getUserId() }

            binding.run {
                // ✅ 1. Top3 초기화 후 다시 세팅
                resetTop3Views()
                bindTop3GroupRankingView(top3)

                // ✅ 2. 내 랭킹 초기화 후 다시 세팅
                bindMyGroupRankingView(null) // 초기화
                bindMyGroupRankingView(myRanking)

                // ✅ 3. RecyclerView 어댑터 새로 설정 (무조건 새로 생성)
                initAdapter(others)

                layoutEmptyGroup.visibility =
                    if (getGroupRankingData == null || getGroupRankingData?.rankersInfo.isNullOrEmpty())
                        View.VISIBLE else View.GONE
            }
        }
    }

    private fun resetTop3Views() {
        binding.layoutGroupRanking1.run {
            imageViewMemberProfile.setImageResource(R.drawable.img_profile_null)
            textViewNickname.text = "-"
            textViewGroupMemberParticipationRate.text = "-Lv"
        }
        binding.layoutGroupRanking2.run {
            imageViewMemberProfile.setImageResource(R.drawable.img_profile_null)
            textViewNickname.text = "-"
            textViewGroupMemberParticipationRate.text = "-Lv"
        }
        binding.layoutGroupRanking3.run {
            imageViewMemberProfile.setImageResource(R.drawable.img_profile_null)
            textViewNickname.text = "-"
            textViewGroupMemberParticipationRate.text = "-Lv"
        }
    }


    fun bindTop3GroupRankingView(top3: List<RankerInfo>?) {
        binding.run {
            top3?.getOrNull(0)?.let { top1 ->
                Glide.with(requireContext())
                    .load(top1.profileImgUrl)
                    .into(layoutGroupRanking1.imageViewMemberProfile)
                layoutGroupRanking1.textViewNickname.text = top1.nickname
                layoutGroupRanking1.textViewGroupMemberParticipationRate.text = "+${top1.weeklyLevelIncrement / 500}Lv"
            }

            top3?.getOrNull(1)?.let { top2 ->
                Glide.with(requireContext())
                    .load(top2.profileImgUrl)
                    .into(layoutGroupRanking2.imageViewMemberProfile)
                layoutGroupRanking2.textViewNickname.text = top2.nickname
                layoutGroupRanking2.textViewGroupMemberParticipationRate.text = "+${top2.weeklyLevelIncrement / 500}Lv"
            }

            top3?.getOrNull(2)?.let { top3 ->
                Glide.with(requireContext())
                    .load(top3.profileImgUrl)
                    .into(layoutGroupRanking3.imageViewMemberProfile)
                layoutGroupRanking3.textViewNickname.text = top3.nickname
                layoutGroupRanking3.textViewGroupMemberParticipationRate.text = "+${top3.weeklyLevelIncrement / 500}Lv"
            }
        }
    }

    fun bindMyGroupRankingView(myRanking: RankerInfo?) {
        binding.run {
            if(myRanking != null) {
                layoutGroupMyRanking.run {
                    textViewGroupTotalExerciseTimeValue.text = "${myRanking.weeklyExerciseTime?.div(60)}시간"
                    textViewGroupLevelValue.text = "${myRanking.weeklyLevelIncrement}"
                    textViewGroupRankingValue.text = "${myRanking.rankNum}위"
                }
            } else {
                layoutGroupMyRanking.run {
                    textViewGroupTotalExerciseTimeValue.text = "-시간"
                    textViewGroupLevelValue.text = "-"
                    textViewGroupRankingValue.text = "-위"
                }
            }
        }
    }


    companion object {
        fun newInstance(groupId: Int): MyGroupRankingFragment {
            val fragment = MyGroupRankingFragment()
            val bundle = Bundle()
            bundle.putInt("groupId", groupId)
            fragment.arguments = bundle
            return fragment
        }
    }
}