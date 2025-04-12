package com.team.score.Mypage.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.MainActivity
import com.team.score.databinding.FragmentQnaBinding

class QnaFragment : Fragment() {

    lateinit var binding: FragmentQnaBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQnaBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {

        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "문의하기"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}