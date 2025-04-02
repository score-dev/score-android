package com.project.score.Mypage.Setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.databinding.FragmentAgreementSettingBinding

class AgreementSettingFragment : Fragment() {

    lateinit var binding: FragmentAgreementSettingBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAgreementSettingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutAgreement1.setOnClickListener {
                // 서비스 이용약관
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://frequent-pasta-701.notion.site/154a57104d07473b8b1fa3607e834363?pvs=4"))
                startActivity(intent)
            }
            layoutAgreement2.setOnClickListener {
                // 위치기반 서비스 이용약관
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://frequent-pasta-701.notion.site/5fdb224e08c8425ba47fb405d4ce5a5b?pvs=4"))
                startActivity(intent)
            }
            layoutAgreement3.setOnClickListener {
                // 개인정보처리방침
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://frequent-pasta-701.notion.site/ce34930d101343deb722ec25f7419617?pvs=4"))
                startActivity(intent)
            }
            layoutAgreement4.setOnClickListener {
                // 마케팅 정보 수신 동의
                /*
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(""))
                startActivity(intent)
                 */
            }
        }

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
                textViewHead.text = "이용약관"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}