package com.team.score.Login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.SignUp.SignUpNicknameFragment
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentAgreementBinding

class AgreementFragment : Fragment() {

    lateinit var binding: FragmentAgreementBinding
    lateinit var onboardingActivity: OnboardingActivity

    var agreementAll = false
    var agreement1 = false
    var agreement2 = false
    var agreement3 = false
    var agreement4 = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAgreementBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        binding.run {
            layoutAgreementAll.setOnClickListener {
                agreementAll = !agreementAll
                checkAgreementAll()
            }
            layoutAgreement1.setOnClickListener {
                agreement1 = !agreement1
                changeAgreement(agreement1, imageViewCheckAgreement1)
            }
            layoutAgreement2.setOnClickListener {
                agreement2 = !agreement2
                changeAgreement(agreement2, imageViewCheckAgreement2)
            }
            layoutAgreement3.setOnClickListener {
                agreement3 = !agreement3
                changeAgreement(agreement3, imageViewCheckAgreement3)
            }
            layoutAgreement4.setOnClickListener {
                agreement4 = !agreement4
                changeAgreement(agreement4, imageViewCheckAgreement4)
            }

            imageViewNextAgreement1.setOnClickListener {
                // 서비스 이용약관
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://frequent-pasta-701.notion.site/154a57104d07473b8b1fa3607e834363?pvs=4"))
                startActivity(intent)
            }
            imageViewNextAgreement2.setOnClickListener {
                // 위치기반 서비스 이용약관
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://frequent-pasta-701.notion.site/5fdb224e08c8425ba47fb405d4ce5a5b?pvs=4"))
                startActivity(intent)
            }
            imageViewNextAgreement3.setOnClickListener {
                // 개인정보처리방침
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://frequent-pasta-701.notion.site/ce34930d101343deb722ec25f7419617?pvs=4"))
                startActivity(intent)
            }
            imageViewNextAgreement4.setOnClickListener {
                // 마케팅 정보 수신 동의
            }

            buttonNext.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.marketing = agreement3
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpNicknameFragment())
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

    fun changeAgreement(isAgree: Boolean, view: ImageView) {
        if(isAgree) {
            view.setImageResource(R.drawable.ic_checked)
        } else {
            view.setImageResource(R.drawable.ic_unchecked)
        }
        binding.buttonNext.isEnabled = agreement1 && agreement2 && agreement3
    }

    fun checkAgreementAll() {
        binding.run {
            if(agreementAll) {
                imageViewCheckAgreementAll.setImageResource(R.drawable.ic_checked)
                imageViewCheckAgreement1.setImageResource(R.drawable.ic_checked)
                imageViewCheckAgreement2.setImageResource(R.drawable.ic_checked)
                imageViewCheckAgreement3.setImageResource(R.drawable.ic_checked)
                imageViewCheckAgreement4.setImageResource(R.drawable.ic_checked)
                agreement1 = true
                agreement2 = true
                agreement3 = true
                agreement4 = true
            } else {
                imageViewCheckAgreementAll.setImageResource(R.drawable.ic_unchecked)
                imageViewCheckAgreement1.setImageResource(R.drawable.ic_unchecked)
                imageViewCheckAgreement2.setImageResource(R.drawable.ic_unchecked)
                imageViewCheckAgreement3.setImageResource(R.drawable.ic_unchecked)
                imageViewCheckAgreement4.setImageResource(R.drawable.ic_unchecked)
                agreement1 = false
                agreement2 = false
                agreement3 = false
                agreement4 = false
            }
        }
        binding.buttonNext.isEnabled = agreement1 && agreement2 && agreement4
    }

    fun initView() {
        binding.toolbar.run {
            buttonBack.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }
    }
}