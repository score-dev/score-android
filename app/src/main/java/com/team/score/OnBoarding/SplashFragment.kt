package com.team.score.OnBoarding

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.API.TokenManager
import com.team.score.Login.LoginFragment
import com.team.score.R
import com.team.score.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        Handler().postDelayed({
            val nextFragment = if(TokenManager(onboardingActivity).getEnter()) {
                OnboardingFragment()
            } else {
                LoginFragment()
            }

            val transaction = onboardingActivity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView_onboarding, nextFragment)
            transaction.commit()
        }, 3000)

        return binding.root
    }
}