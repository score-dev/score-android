package com.team.score.Mypage.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.API.TokenManager
import com.team.score.BasicDialog
import com.team.score.BasicDialogInterface
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentSettingBinding

class SettingFragment : Fragment(), BasicDialogInterface {

    lateinit var binding: FragmentSettingBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutNotification.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, NotificationSettingFragment())
                    .addToBackStack(null)
                    .commit()
            }
            layoutMate.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MateSettingFragment())
                    .addToBackStack(null)
                    .commit()
            }
            layoutQna.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, QnaFragment())
                    .addToBackStack(null)
                    .commit()
            }
            layoutAgreement.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, AgreementSettingFragment())
                    .addToBackStack(null)
                    .commit()
            }
            layoutLogout.setOnClickListener {
                logout()
            }
            layoutWithdraw.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, WithdrawFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun logout() {
        // 다이얼로그
        val title = "로그아웃하기"
        val description = "정말 로그아웃하시겠어요? 데이터는 그대로\n남아있지만 푸시알림은 받을 수 없어요"

        val dialog = BasicDialog(this, title, description, "남아있기", "로그아웃", true)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let {
            dialog.show(it.supportFragmentManager, "LogoutDialog")
        }
    }

    fun initView() {

        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "환경설정"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    override fun onClickRightButton() {
        // 로그아웃
        TokenManager(mainActivity).deleteAccessToken()
        TokenManager(mainActivity).deleteRefreshToken()

        mainActivity.finish()
    }

    override fun onClickLeftButton() {

    }
}