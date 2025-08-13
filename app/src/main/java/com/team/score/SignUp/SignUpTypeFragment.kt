package com.team.score.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpTypeBinding

class SignUpTypeFragment : Fragment() {

    lateinit var binding: FragmentSignUpTypeBinding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpTypeBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        binding.run {
            layoutStudent.setOnClickListener {
                MyApplication.signUpIsStudent = true
                buttonNext.isEnabled = true
                layoutStudent.setBackgroundResource(R.drawable.background_sub3_radius15_stroke)
                layoutNotStudent.setBackgroundResource(R.drawable.background_grey2_radius15)
            }

            layoutNotStudent.setOnClickListener {
                MyApplication.signUpIsStudent = false
                buttonNext.isEnabled = true
                layoutStudent.setBackgroundResource(R.drawable.background_grey2_radius15)
                layoutNotStudent.setBackgroundResource(R.drawable.background_green_radius15_stroke)
            }

            buttonNext.setOnClickListener {
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

    fun initView() {
        binding.run {
            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}