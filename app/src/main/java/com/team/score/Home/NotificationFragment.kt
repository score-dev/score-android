package com.team.score.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    lateinit var binding: FragmentNotificationBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNotificationBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewHead.text = "알림"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }


}