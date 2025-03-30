package com.project.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.score.API.response.group.GroupRankingResponse
import com.project.score.API.response.home.HomeResponse
import com.project.score.Group.adapter.GroupOthersRankingAdapter
import com.project.score.Group.adapter.MyGroupRankingAdapter
import com.project.score.Group.viewModel.GroupViewModel
import com.project.score.Home.adapter.GroupRelayAdapter
import com.project.score.Home.adapter.WeeklyCalendarAdapter
import com.project.score.Home.viewModel.HomeViewModel
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.Utils.CalendarUtil
import com.project.score.Utils.CalendarUtil.getCurrentWeekInfo
import com.project.score.Utils.CalendarUtil.moveToNextWeek
import com.project.score.Utils.CalendarUtil.moveToPreviousWeek
import com.project.score.Utils.DynamicSpacingItemDecoration
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentGroupBinding

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

            }
            buttonRight.setOnClickListener {
                val weekInfo = moveToNextWeek()
                textViewWeekMonth.text = "${weekInfo.month}월 ${weekInfo.weekOfMonth}주차"
                textViewWeekDays.text = "${weekInfo.startDate} ~ ${weekInfo.endDate}"

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
    }

    fun initView() {

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