package com.project.score.Mypage.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.databinding.FragmentNotificationSettingBinding

class NotificationSettingFragment : Fragment() {

    lateinit var binding: FragmentNotificationSettingBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNotificationSettingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val switchList = listOf(binding.switchNotice, binding.switchCommunication, binding.switchGoal)

        binding.run {
            // 1. 전체 스위치 클릭 시, 하위 스위치 모두 변경
            binding.switchNotificationAll.setOnCheckedChangeListener { _, isChecked ->
                // 리스너 제거
                switchList.forEach { it.setOnCheckedChangeListener(null) }

                // 상태 변경
                switchList.forEach { it.isChecked = isChecked }

                // 리스너 다시 설정
                switchList.forEach { switch ->
                    switch.setOnCheckedChangeListener { _, _ ->
                        updateAllSwitchState(switchList)
                    }
                }
            }

            // 초기에도 리스너 세팅
            switchList.forEach { switch ->
                switch.setOnCheckedChangeListener { _, _ ->
                    updateAllSwitchState(switchList)
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    // 2. 개별 스위치 변경 시, 전체 스위치 상태 업데이트
    fun updateAllSwitchState(switchList: List<MaterialSwitch>) {
        val allChecked = switchList.all { it.isChecked }
        binding.switchNotificationAll.setOnCheckedChangeListener(null)
        binding.switchNotificationAll.isChecked = allChecked
        binding.switchNotificationAll.setOnCheckedChangeListener { _, isChecked ->
            switchList.forEach { it.isChecked = isChecked }
        }
    }

    fun initView() {

        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "알림설정"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}