package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.group.GroupInfoResponse
import com.team.score.Group.adapter.MyGroupListAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentMyGroupListBinding

class MyGroupListFragment : Fragment() {

    lateinit var binding: FragmentMyGroupListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    lateinit var groupAdapter : MyGroupListAdapter

    var getGroupInfo: List<GroupInfoResponse>? = null

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
        groupAdapter = MyGroupListAdapter(mainActivity, getGroupInfo).apply {
            itemClickListener = object : MyGroupListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val bundle = Bundle().apply {
                        putInt("groupId", getGroupInfo?.get(position)?.id ?: 0)
                        putString("groupName", getGroupInfo?.get(position)?.name ?: "")
                    }

                    // 전달할 Fragment 생성
                    val  nextFragment = MyGroupDetailFragment().apply {
                        arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                    }
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        binding.run {
            recyclerViewMyGroup.apply {
                adapter = groupAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            myGroupList.observe(viewLifecycleOwner) {
                getGroupInfo = it

                groupAdapter.updateList(getGroupInfo)
            }
        }
    }

    fun initView() {
        viewModel.getMyGroupList(mainActivity)
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