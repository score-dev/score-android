package com.project.score.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.BasicDialog
import com.project.score.BasicDialogInterface
import com.project.score.R
import com.project.score.databinding.FragmentSignUpGoalTimeBinding

class SignUpGoalTimeFragment : Fragment(), BasicDialogInterface {

    lateinit var binding: FragmentSignUpGoalTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpGoalTimeBinding.inflate(layoutInflater)

        binding.run {
            buttonNext.setOnClickListener {
                setNotification()
            }
        }

        return binding.root
    }

    fun setNotification() {
        // 다이얼로그
        val title = "스코어에서 \n알림을 보내도록 허용하시겠습니까?"
        val description = null

        val dialog = BasicDialog(this@SignUpGoalTimeFragment, title, description, "허용 안함", "허용")
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let {
            dialog.show(it.supportFragmentManager, "NotificationDialog")
        }
    }

    override fun onClickRightButton() {
        // 알림 허용
    }
}