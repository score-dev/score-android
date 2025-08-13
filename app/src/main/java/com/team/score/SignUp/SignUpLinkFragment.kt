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
import com.team.score.databinding.FragmentSignUpLinkBinding

class SignUpLinkFragment : Fragment() {

    lateinit var binding: FragmentSignUpLinkBinding
    lateinit var onboardingActivity: OnboardingActivity

    var isAlone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpLinkBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        binding.run {
            buttonKakao.setOnClickListener {
                // 카카오 초대
            }

            buttonLink.setOnClickListener {
                // 초대 링크 복사
            }

            buttonAlone.setOnClickListener {
                isAlone = !isAlone

                if(isAlone) {
                    buttonNext.isEnabled = true
                    buttonAlone.setBackgroundResource(R.drawable.background_sub3_radius8)
                    imageViewCheck.visibility = View.VISIBLE
                } else {
                    buttonNext.isEnabled = false
                    buttonAlone.setBackgroundResource(R.drawable.background_grey2_radius8)
                    imageViewCheck.visibility = View.GONE
                }
            }

            buttonNext.setOnClickListener {
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpGenderFragment())
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