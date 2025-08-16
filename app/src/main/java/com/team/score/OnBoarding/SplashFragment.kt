package com.team.score.OnBoarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.API.TokenManager
import com.team.score.API.TokenUtil
import com.team.score.Login.LoginFragment
import com.team.score.MainActivity
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
            if(TokenManager(onboardingActivity).getEnter()) {
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, OnboardingFragment())
                    .commit()
            } else {
                if(TokenManager(onboardingActivity).getRefreshToken().isNullOrEmpty()) {
                    onboardingActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_onboarding, LoginFragment())
                        .commit()
                } else {
                    TokenUtil.refreshToken(
                        onboardingActivity,
                        retryRequest = {
                            val mainIntent = Intent(activity, MainActivity::class.java)
                            mainIntent.putExtra("isLogin", true)
                            onboardingActivity.startActivity(mainIntent) },
                        onFailure = {
                            onboardingActivity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView_onboarding, LoginFragment())
                                .commit()
                        }
                    )
                }
            }
        }, 3000)

        return binding.root
    }
}