package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.group.GroupMateResponse
import com.team.score.API.response.group.GroupUnexercisedMateResponse
import com.team.score.Group.adapter.GroupMateAdapter
import com.team.score.Group.adapter.GroupUnexercisedMateAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.Home.viewModel.HomeViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentGroupMateListBinding

class GroupMateListFragment : Fragment() {

    lateinit var binding: FragmentGroupMateListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(this)[GroupViewModel::class.java]
    }
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    lateinit var groupMateAdapter : GroupMateAdapter
    lateinit var groupUnexercisedMateAdapter : GroupUnexercisedMateAdapter
    var getGroupMateInfo: List<GroupMateResponse>? = null
    var getGroupUnexercisedMateInfo: List<GroupUnexercisedMateResponse>? = null

    var isMyGroup = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupMateListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            root.requestLayout()

            isMyGroup = MyApplication.myGroupList.contains(arguments?.getInt("groupId") ?: 0)
            if(isMyGroup) {
                viewModel.getGroupMateList(mainActivity, arguments?.getInt("groupId") ?: 0)
                viewModel.getGroupUnexercisedMateList(mainActivity, arguments?.getInt("groupId") ?: 0)

                textViewGroupName.text = "${arguments?.getString("groupName") ?: ""} 메이트"
            } else {
                viewModel.getGroupMateList(mainActivity, arguments?.getInt("groupId") ?: 0)

                textViewGroupName.visibility = View.GONE
                textViewUnexercisedMate.visibility = View.GONE
                recyclerViewUnexercisedMember.visibility = View.GONE
            }
        }
    }

    fun initAdapter() {
        groupMateAdapter = GroupMateAdapter(mainActivity, getGroupMateInfo, MyApplication.myGroupList.contains(arguments?.getInt("groupId") ?: 0)).apply {
            itemClickListener = object : GroupMateAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if(MyApplication.myGroupList.contains(arguments?.getInt("groupId") ?: 0)) {
                        var nextFragment = MateDetailFragment()

                        val bundle = Bundle().apply {
                            putInt("userId", getGroupMateInfo?.get(position)?.id ?: 0)
                        }
                        // 전달할 Fragment 생성
                        nextFragment = MateDetailFragment().apply {
                            arguments = bundle
                        }

                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }

        groupUnexercisedMateAdapter = GroupUnexercisedMateAdapter(mainActivity, getGroupUnexercisedMateInfo).apply {
            itemClickListener = object : GroupUnexercisedMateAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val userId = getGroupUnexercisedMateInfo?.get(position)?.userId ?: 0

                    homeViewModel.batonGroupMember(mainActivity, userId)
                }
            }
        }

        binding.run {
            recyclerViewMate.apply {
                adapter = groupMateAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            recyclerViewUnexercisedMember.apply {
                adapter = groupUnexercisedMateAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            groupMateList.observe(viewLifecycleOwner) {
                getGroupMateInfo = it

                groupMateAdapter.updateList(getGroupMateInfo)
            }

            groupUnexercisedMateList.observe(viewLifecycleOwner) {
                getGroupUnexercisedMateInfo = it

                groupUnexercisedMateAdapter.updateList(getGroupUnexercisedMateInfo, -1)
            }
        }

        homeViewModel.run {
            isBaton.observe(viewLifecycleOwner) { baton ->
                if(baton?.isBaton == true) {
                    val position = getGroupUnexercisedMateInfo?.find { it.userId == baton.receiverId }?.userId ?: 0
                    groupUnexercisedMateAdapter.updateList(getGroupUnexercisedMateInfo, position)
                }
            }
        }
    }

    companion object {
        fun newInstance(groupId: Int, groupName: String): GroupMateListFragment {
            val fragment = GroupMateListFragment()
            val bundle = Bundle()
            bundle.putInt("groupId", groupId)
            bundle.putString("groupName", groupName)
            fragment.arguments = bundle
            return fragment
        }
    }

}