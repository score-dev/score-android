package com.project.score.Mypage.Setting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.materialswitch.MaterialSwitch
import com.project.score.API.response.user.NotificationInfoResponse
import com.project.score.MainActivity
import com.project.score.Mypage.viewModel.MypageViewModel
import com.project.score.R
import com.project.score.databinding.FragmentNotificationSettingBinding

class NotificationSettingFragment : Fragment() {

    lateinit var binding: FragmentNotificationSettingBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    var getNotificationInfo = NotificationInfoResponse(0, false, false, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNotificationSettingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val switchList = listOf(binding.switchNotice, binding.switchCommunication, binding.switchGoal)

        observeViewModel()

        binding.run {
            // 1. 전체 스위치 클릭 시, 하위 스위치 모두 변경
            binding.switchNotificationAll.setOnCheckedChangeListener { _, isChecked ->
                // 리스너 제거
                switchList.forEach { it.setOnCheckedChangeListener(null) }

                // 상태 변경
                switchList.forEach { it.isChecked = isChecked }

                // NotificationInfo 객체 업데이트
                getNotificationInfo = getNotificationInfo.copy(
                    marketing = switchList[0].isChecked,
                    tag = switchList[1].isChecked,
                    exercisingTime = switchList[2].isChecked
                )

                Log.d("##", "notification : $getNotificationInfo")

                // 리스너 다시 설정
                switchList.forEach { switch ->
                    switch.setOnCheckedChangeListener { _, _ ->
                        updateAllSwitchState(switchList)
                    }
                }
            }

            // 개별 스위치 상태 변경 시 NotificationInfo 객체 업데이트
            switchNotice.setOnCheckedChangeListener { _, isChecked ->
                getNotificationInfo = getNotificationInfo.copy(marketing = isChecked)
                updateAllSwitchState(switchList)
            }
            switchCommunication.setOnCheckedChangeListener { _, isChecked ->
                getNotificationInfo = getNotificationInfo.copy(tag = isChecked)
                updateAllSwitchState(switchList)
            }
            switchGoal.setOnCheckedChangeListener { _, isChecked ->
                getNotificationInfo = getNotificationInfo.copy(exercisingTime = isChecked)
                updateAllSwitchState(switchList)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {
            notificationInfo.observe(viewLifecycleOwner) {
                getNotificationInfo = it!!

                Log.d("##", "notification first : $getNotificationInfo")

                binding.run {
                    switchNotificationAll.isChecked = (getNotificationInfo.marketing == true && getNotificationInfo.tag == true && getNotificationInfo.exercisingTime == true)
                    switchNotice.isChecked = getNotificationInfo.marketing
                    switchCommunication.isChecked = getNotificationInfo.tag
                    switchGoal.isChecked = getNotificationInfo.exercisingTime
                }
            }
        }
    }

    // 2. 개별 스위치 변경 시, 전체 스위치 상태 업데이트
    fun updateAllSwitchState(switchList: List<MaterialSwitch>) {
        val allChecked = switchList.all { it.isChecked }
        binding.switchNotificationAll.setOnCheckedChangeListener(null)
        binding.switchNotificationAll.isChecked = allChecked
        binding.switchNotificationAll.setOnCheckedChangeListener { _, isChecked ->
            switchList.forEach { it.isChecked = isChecked }
        }

        Log.d("##", "notification : $getNotificationInfo")
    }

    fun initView() {

        viewModel.getNotificationInfo(mainActivity)

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