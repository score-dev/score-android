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
import com.team.score.MainActivity
import com.team.score.databinding.FragmentGroupMateListBinding

class GroupMateListFragment : Fragment() {

    lateinit var binding: FragmentGroupMateListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    lateinit var groupMateAdapter : GroupMateAdapter
    lateinit var groupUnexercisedMateAdapter : GroupUnexercisedMateAdapter
    var getGroupMateInfo: List<GroupMateResponse>? = null
    var getGroupUnexercisedMateInfo: List<GroupUnexercisedMateResponse>? = null

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
        viewModel.getGroupMateList(mainActivity, arguments?.getInt("groupId") ?: 0)
        viewModel.getGroupUnexercisedMateList(mainActivity, arguments?.getInt("groupId") ?: 0)

        binding.run {
            textViewGroupName.text = "${viewModel.myGroupList.value?.find { it.id == (arguments?.getInt("groupId") ?: 0) }?.name} 메이트"
        }
    }

    fun initAdapter() {
        groupMateAdapter = GroupMateAdapter(mainActivity, getGroupMateInfo).apply {
            itemClickListener = object : GroupMateAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {

                }
            }
        }

        groupUnexercisedMateAdapter = GroupUnexercisedMateAdapter(mainActivity, getGroupUnexercisedMateInfo).apply {
            itemClickListener = object : GroupUnexercisedMateAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {

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

                groupUnexercisedMateAdapter.updateList(getGroupUnexercisedMateInfo)
            }
        }
    }

    companion object {
        fun newInstance(groupId: Int): GroupMateListFragment {
            val fragment = GroupMateListFragment()
            val bundle = Bundle()
            bundle.putInt("groupId", groupId)
            fragment.arguments = bundle
            return fragment
        }
    }

}