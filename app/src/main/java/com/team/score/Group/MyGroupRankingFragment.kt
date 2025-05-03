package com.team.score.Group

import android.os.Bundle
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
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    lateinit var otherGroupMembersAdapter : GroupMemberOthersRankingAdapter

    var getGroupRankingData: GroupRankingResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyGroupRankingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
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

    fun initAdapter() {
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
    }

    fun observeViewModel() {
        viewModel.run {
            groupRanking.observe(viewLifecycleOwner) { it ->
                getGroupRankingData = it
                getGroupRankingData?.rankersInfo = getGroupRankingData?.rankersInfo?.sortedBy { it.rankNum } ?: emptyList()

                binding.run {
                    if (getGroupRankingData == null) {
                        layoutEmptyGroup.visibility = View.VISIBLE
                        layoutWeekRanking.visibility = View.GONE
                    } else {
                        layoutEmptyGroup.visibility = View.GONE
                        layoutWeekRanking.visibility = View.VISIBLE
                        otherGroupMembersAdapter.updateList(getGroupRankingData?.rankersInfo?.drop(3))

                        val top3 = getGroupRankingData?.rankersInfo?.take(3)

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
                                .into(layoutGroupRanking1.imageViewMemberProfile)
                            layoutGroupRanking1.textViewNickname.text = top2.nickname
                            layoutGroupRanking1.textViewGroupMemberParticipationRate.text = "+${top2.weeklyLevelIncrement / 500}Lv"
                        }

                        top3?.getOrNull(2)?.let { top3 ->
                            Glide.with(requireContext())
                                .load(top3.profileImgUrl)
                                .into(layoutGroupRanking1.imageViewMemberProfile)
                            layoutGroupRanking1.textViewNickname.text = top3.nickname
                            layoutGroupRanking1.textViewGroupMemberParticipationRate.text = "+${top3.weeklyLevelIncrement / 500}Lv"
                        }

                        var myRanking = getGroupRankingData?.rankersInfo?.find { it.userId == TokenManager(mainActivity).getUserId() }
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