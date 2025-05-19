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
import com.team.score.API.response.group.GroupRanking
import com.team.score.API.response.group.RankerInfo
import com.team.score.API.response.group.SchoolGroupRankingResponse
import com.team.score.Group.adapter.GroupOthersRankingAdapter
import com.team.score.Group.adapter.MyGroupRankingAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.FeedDetailFragment
import com.team.score.Utils.CalendarUtil.getCurrentWeekInfo
import com.team.score.Utils.CalendarUtil.moveToNextWeek
import com.team.score.Utils.CalendarUtil.moveToPreviousWeek
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentGroupBinding

class GroupFragment : Fragment() {

    lateinit var binding: FragmentGroupBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    lateinit var myGroupAdapter : MyGroupRankingAdapter
    lateinit var otherGroupsAdapter : GroupOthersRankingAdapter

    var getGroupData: SchoolGroupRankingResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            searchBar.editTextSearch.setOnClickListener {
                // 검색 화면 이동
                var nextFragment = GroupSearchFragment()

                val bundle = Bundle().apply {
                    putInt("schoolId", MyApplication.userInfo?.schoolId ?: 0)
                }
                // 전달할 Fragment 생성
                nextFragment = GroupSearchFragment().apply {
                    arguments = bundle
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }

            buttonLeft.setOnClickListener {
                val weekInfo = moveToPreviousWeek()
                textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
                textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

                val convertedDate = weekInfo.startDate.replace(".", "-")
                viewModel.getSchoolGroupRanking(mainActivity, convertedDate)
            }
            buttonRight.setOnClickListener {
                val weekInfo = moveToNextWeek()
                textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
                textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

                val convertedDate = weekInfo.startDate.replace(".", "-")
                viewModel.getSchoolGroupRanking(mainActivity, convertedDate)
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
            schoolGroupRanking.observe(viewLifecycleOwner) { it ->
                getGroupData = it
                Log.d("##", "group ranking : ${getGroupData}")
                getGroupData?.allRankers = getGroupData?.allRankers?.sortedBy { it.rank } ?: emptyList()

                initAdapter()

                myGroupAdapter.updateList(getGroupData?.myGroupRanking)

                binding.run {
                    if (getGroupData == null || getGroupData?.allRankers?.isEmpty() == true) {
                        layoutEmptyGroup.visibility = View.VISIBLE
                    } else {
                        layoutEmptyGroup.visibility = View.GONE
                        otherGroupsAdapter.updateList(getGroupData?.allRankers?.drop(3))

                        val top3 = getGroupData?.allRankers?.take(3)
                        bindTop3GroupRankingView(top3)
                    }
                }
            }
        }
    }

    fun bindTop3GroupRankingView(top3: List<GroupRanking>?) {
        binding.run {
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
                    .into(layoutGroupRanking2.imageViewGroupProfile)
                layoutGroupRanking2.textViewGroupName.text = top2.groupName
                layoutGroupRanking2.textViewGroupMemberParticipationRate.text = "${top2.participateRatio}%"
            }

            top3?.getOrNull(2)?.let { top3 ->
                Glide.with(requireContext())
                    .load(top3.groupImg)
                    .into(layoutGroupRanking3.imageViewGroupProfile)
                layoutGroupRanking3.textViewGroupName.text = top3.groupName
                layoutGroupRanking3.textViewGroupMemberParticipationRate.text = "${top3.participateRatio}%"
            }
        }
    }

    fun initView() {
        viewModel.getSchoolGroupRanking(mainActivity, null)
        viewModel.getMyGroupList(mainActivity)

        mainActivity.hideBottomNavigation(false)

        binding.run {
            toolbar.textViewHead.text = MyApplication.userInfo?.schoolName.toString()
            searchBar.editTextSearch.run {
                isFocusable = false
                isClickable = true
            }

            val weekInfo = getCurrentWeekInfo()
            textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
            textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

            // 그룹 랭킹 TOP3
            layoutGroupRanking2.imageViewRanking.setImageResource(R.drawable.img_ranking2)
            layoutGroupRanking3.imageViewRanking.setImageResource(R.drawable.img_ranking3)
        }
    }

}