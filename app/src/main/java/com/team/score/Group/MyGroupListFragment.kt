package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.group.MyGroupResponse
import com.team.score.API.weather.response.Main
import com.team.score.Group.adapter.MyGroupListAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.Home.adapter.GroupRelayAdapter
import com.team.score.Home.adapter.WeeklyCalendarAdapter
import com.team.score.MainActivity
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.R
import com.team.score.Utils.CalendarUtil
import com.team.score.Utils.DynamicSpacingItemDecoration
import com.team.score.databinding.FragmentMyGroupListBinding

class MyGroupListFragment : Fragment() {

    lateinit var binding: FragmentMyGroupListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    lateinit var groupAdapter : MyGroupListAdapter

    var getGroupInfo: List<MyGroupResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyGroupListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        groupAdapter = MyGroupListAdapter(mainActivity, getGroupInfo)

        binding.run {
            recyclerViewMyGroup.apply {
                adapter = groupAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            buttonCreateGroup.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, CreateGroupFragment())
                    .addToBackStack(null)
                    .commit()
            }

            toolbar.run {
                textViewHead.text = "내 그룹"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}