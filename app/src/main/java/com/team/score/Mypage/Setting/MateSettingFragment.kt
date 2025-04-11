package com.team.score.Mypage.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.user.BlockedMateListResponse
import com.team.score.MainActivity
import com.team.score.Mypage.Adapter.BlockedMateAdapter
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.databinding.FragmentMateSettingBinding

class MateSettingFragment : Fragment() {

    lateinit var binding: FragmentMateSettingBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    lateinit var blockedMateAdapter : BlockedMateAdapter

    var getBlockedMateList: MutableList<BlockedMateListResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMateSettingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {
            blockedMateList.observe(viewLifecycleOwner) {
                getBlockedMateList = it

                binding.run {
                    if(getBlockedMateList?.size == 0) {
                        layoutEmptyMate.visibility = View.VISIBLE
                        recyclerViewMate.visibility = View.GONE
                    } else {
                        layoutEmptyMate.visibility = View.GONE
                        recyclerViewMate.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun initAdapter() {
        blockedMateAdapter = BlockedMateAdapter(
            mainActivity,
            getBlockedMateList
        )

        binding.run {
            recyclerViewMate.apply {
                adapter = blockedMateAdapter.apply {
                    itemClickListener = object : BlockedMateAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                           viewModel.cancelBlockedMate(mainActivity, (getBlockedMateList?.get(position)?.id ?: 0))
                        }
                    }
                }
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun initView() {
        viewModel.getBlockedMateList(mainActivity)

        mainActivity.hideBottomNavigation(true)

        binding.run {
            layoutEmptyMate.visibility = View.VISIBLE
            recyclerViewMate.visibility = View.GONE

            toolbar.run {
                textViewHead.text = "차단한 메이트 관리하기"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}