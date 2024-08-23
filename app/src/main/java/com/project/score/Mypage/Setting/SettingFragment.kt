package com.project.score.Mypage.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.BasicDialog
import com.project.score.BasicDialogInterface
import com.project.score.databinding.FragmentSettingBinding

class SettingFragment : Fragment(), BasicDialogInterface {

    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingBinding.inflate(layoutInflater)

        binding.run {
            layoutNotification.setOnClickListener {

            }
            layoutMate.setOnClickListener {

            }
            layoutQna.setOnClickListener {

            }
            layoutAgreement.setOnClickListener {

            }
            layoutLogout.setOnClickListener {

            }
            layoutLogout.setOnClickListener {
                logout()
            }
            layoutWithdraw.setOnClickListener {

            }
        }

        return binding.root
    }

    fun logout() {
        // 다이얼로그
        val title = "로그아웃하기"
        val description = "정말 로그아웃하시겠어요? 데이터는 그대로\n남아있지만 푸시알림은 받을 수 없어요"

        val dialog = BasicDialog(this, title, description, "남아있기", "로그아웃")
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let {
            dialog.show(it.supportFragmentManager, "LogoutDialog")
        }
    }

    override fun onClickRightButton() {
        // 로그아웃
    }
}