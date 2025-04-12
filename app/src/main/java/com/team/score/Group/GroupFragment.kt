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
import com.team.score.API.response.group.GroupRankingResponse
import com.team.score.Group.adapter.GroupOthersRankingAdapter
import com.team.score.Group.adapter.MyGroupRankingAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.CalendarUtil.getCurrentWeekInfo
import com.team.score.Utils.CalendarUtil.moveToNextWeek
import com.team.score.Utils.CalendarUtil.moveToPreviousWeek
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentGroupBinding

class GroupFragment : Fragment() {

    lateinit var binding: FragmentGroupBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: GroupViewModel

    lateinit var myGroupAdapter : MyGroupRankingAdapter
    lateinit var otherGroupsAdapter : GroupOthersRankingAdapter

    var getGroupData: GroupRankingResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[GroupViewModel::class.java]

        initAdapter()
        observeViewModel()

        binding.run {
            searchBar.layoutSearch.setOnClickListener {
                // 검색 화면 이동
            }

            buttonLeft.setOnClickListener {
                val weekInfo = moveToPreviousWeek()
                textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
                textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

                val convertedDate = weekInfo.startDate.replace(".", "-")
                viewModel.getGroupRanking(mainActivity, convertedDate)
            }
            buttonRight.setOnClickListener {
                val weekInfo = moveToNextWeek()
                textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
                textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

                val convertedDate = weekInfo.startDate.replace(".", "-")
                viewModel.getGroupRanking(mainActivity, convertedDate)
            }

            buttonCreateGroup.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, CreateGroupFragment())
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

    fun initAdapter() {
        myGroupAdapter = MyGroupRankingAdapter(
            mainActivity,
            getGroupData?.myGroupRanking
        )

        otherGroupsAdapter = GroupOthersRankingAdapter(
            mainActivity,
            getGroupData?.allRankers
        )

        binding.run {
            recyclerViewMyGroupRanking.apply {
                adapter = myGroupAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            recyclerViewGroupRankingOthers.apply {
                adapter = otherGroupsAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            groupRanking.observe(viewLifecycleOwner) { it ->
                getGroupData = it
                getGroupData?.allRankers = getGroupData?.allRankers?.sortedBy { it.rank } ?: emptyList()

                myGroupAdapter.updateList(getGroupData?.myGroupRanking)

                binding.run {
                    if (getGroupData == null) {
                        layoutEmptyGroup.visibility = View.VISIBLE
                        layoutWeekRanking.visibility = View.GONE
                    } else {
                        layoutEmptyGroup.visibility = View.GONE
                        layoutWeekRanking.visibility = View.VISIBLE
                        otherGroupsAdapter.updateList(getGroupData?.allRankers?.drop(3))

                        val top3 = getGroupData?.allRankers?.take(3)

                        top3?.getOrNull(0)?.let { top1 ->
                            Glide.with(requireContext())
                                .load(top1.groupImg)
                                .into(layoutGroupRanking1.imageViewGroupProfile)
                            layoutGroupRanking1.textViewGroupName.text = top1.groupName
                            layoutGroupRanking1.textViewGroupMemberParticipationRate.text = "${top1.participateRatio}%"
                        }

                        top3?.getOrNull(1)?.let { top2 ->
                            Glide.with(requireContext())
                                .load(top2.groupImg)
                                .into(layoutGroupRanking1.imageViewGroupProfile)
                            layoutGroupRanking1.textViewGroupName.text = top2.groupName
                            layoutGroupRanking1.textViewGroupMemberParticipationRate.text = "${top2.participateRatio}%"
                        }

                        top3?.getOrNull(2)?.let { top3 ->
                            Glide.with(requireContext())
                                .load(top3.groupImg)
                                .into(layoutGroupRanking1.imageViewGroupProfile)
                            layoutGroupRanking1.textViewGroupName.text = top3.groupName
                            layoutGroupRanking1.textViewGroupMemberParticipationRate.text = "${top3.participateRatio}%"
                        }
                    }
                }
            }
        }
    }

    fun initView() {
        viewModel.getGroupRanking(mainActivity, null)
        mainActivity.hideBottomNavigation(false)

        binding.run {
            toolbar.textViewHead.text = MyApplication.userInfo?.schoolName.toString()

            val weekInfo = getCurrentWeekInfo()
            textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
            textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

            // 그룹 랭킹 TOP3
            layoutGroupRanking2.imageViewRanking.setImageResource(R.drawable.img_ranking2)
            layoutGroupRanking3.imageViewRanking.setImageResource(R.drawable.img_ranking3)
        }
    }

}