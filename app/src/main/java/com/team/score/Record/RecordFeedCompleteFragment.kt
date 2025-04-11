package com.team.score.Record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentRecordFeedCompleteBinding


class RecordFeedCompleteFragment : Fragment() {

    lateinit var binding: FragmentRecordFeedCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordFeedCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNext.setOnClickListener {
                // 메인 화면으로 이동
                mainActivity.binding.bottomNavigationView.selectedItemId = R.id.menu_home
            }
        }

        return binding.root
    }

}