package com.team.score.SignUp.BottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpGoalTimeBottomSheetBinding

interface SignUpGoalTimeBottomSheetListener {
    fun onTimeSelected(time: String)
}
class SignUpGoalTimeBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var listener: SignUpGoalTimeBottomSheetListener
    lateinit var binding: FragmentSignUpGoalTimeBottomSheetBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as SignUpGoalTimeBottomSheetListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpGoalTimeBottomSheetBinding.inflate(inflater, container, false)

        binding.run {
           buttonCheck.setOnClickListener {
               val amPm = spinnerAmPm.displayedValues[spinnerAmPm.value]
               val hour = spinnerHour.value
               val minute = spinnerMinute.value

               val selectedTime = "$amPm $hour:${String.format("%02d", minute)}"

               if(amPm == "오후") {
                   if(minute < 10) {
                       MyApplication.signUpInfo?.userDto?.goal = "${hour+12}:0$minute:00"
                       MyApplication.userUpdateInfo?.userUpdateDto?.goal = "${hour+12}:0$minute:00"
                   } else {
                       MyApplication.signUpInfo?.userDto?.goal = "${hour+12}:$minute:00"
                       MyApplication.userUpdateInfo?.userUpdateDto?.goal = "${hour+12}:$minute:00"
                   }
               } else {
                   if(minute < 10) {
                       MyApplication.signUpInfo?.userDto?.goal = "${hour}:0$minute:00"
                       MyApplication.userUpdateInfo?.userUpdateDto?.goal = "${hour}:0$minute:00"
                   } else {
                       MyApplication.signUpInfo?.userDto?.goal = "${hour}:$minute:00"
                       MyApplication.userUpdateInfo?.userUpdateDto?.goal = "${hour}:$minute:00"
                   }
               }
               onItemClicked(selectedTime)
           }

            // 오전/오후 설정
            spinnerAmPm.minValue = 0
            spinnerAmPm.maxValue = 1
            spinnerAmPm.displayedValues = arrayOf("오전", "오후")

            // 시간 설정 (1 ~ 12)
            spinnerHour.minValue = 1
            spinnerHour.maxValue = 12

            // 분 설정 (0 ~ 59)
            spinnerMinute.minValue = 0
            spinnerMinute.maxValue = 59
            spinnerMinute.setFormatter { String.format("%02d", it) }

        }

        return binding.root
    }

    private fun onItemClicked(time: String) {
        listener.onTimeSelected(time)
        dismiss()
    }
}