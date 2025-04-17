package com.team.score.Group

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.record.GroupFeedListResponse
import com.team.score.API.response.user.FeedListResponse
import com.team.score.API.weather.response.Main
import com.team.score.Group.adapter.MyGroupFeedAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.Mypage.Adapter.FeedAdapter
import com.team.score.R
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentMyGroupFeedListBinding

class MyGroupFeedListFragment : Fragment() {

    lateinit var binding: FragmentMyGroupFeedListBinding
    lateinit var mainActivity: MainActivity

    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyGroupFeedListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
    }

    companion object {
        fun newInstance(groupId: Int): MyGroupFeedListFragment {
            val fragment = MyGroupFeedListFragment()
            val bundle = Bundle()
            bundle.putInt("groupId", groupId)
            fragment.arguments = bundle
            return fragment
        }
    }

}