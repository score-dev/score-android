package com.project.score.Login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.project.score.OnBoarding.OnboardingActivity
import com.project.score.R
import com.project.score.SignUp.SignUpNicknameFragment
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentAgreementBinding

class AgreementFragment : Fragment() {

    lateinit var binding: FragmentAgreementBinding
    lateinit var onboardingActivity: OnboardingActivity

    var agreementAll = false
    var agreement1 = false
    var agreement2 = false
    var agreement3 = false

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
        binding.buttonNext.isEnabled = agreement1 && agreement2
    }

    fun checkAgreementAll() {
        binding.run {
            if(agreementAll) {
                imageViewCheckAgreementAll.setImageResource(R.drawable.ic_checked)
                imageViewCheckAgreement1.setImageResource(R.drawable.ic_checked)
                imageViewCheckAgreement2.setImageResource(R.drawable.ic_checked)
                imageViewCheckAgreement3.setImageResource(R.drawable.ic_checked)
                agreement1 = true
                agreement2 = true
                agreement3 = true
            } else {
                imageViewCheckAgreementAll.setImageResource(R.drawable.ic_unchecked)
                imageViewCheckAgreement1.setImageResource(R.drawable.ic_unchecked)
                imageViewCheckAgreement2.setImageResource(R.drawable.ic_unchecked)
                imageViewCheckAgreement3.setImageResource(R.drawable.ic_unchecked)
                agreement1 = false
                agreement2 = false
                agreement3 = false
            }
        }
        binding.buttonNext.isEnabled = agreement1 && agreement2
    }

    fun initView() {
        binding.toolbar.run {
            buttonBack.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }
    }
}