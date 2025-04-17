package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.common.net.InternetDomainName
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentMyGroupMateListBinding

class MyGroupMateListFragment : Fragment() {

    lateinit var binding: FragmentMyGroupMateListBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyGroupMateListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
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