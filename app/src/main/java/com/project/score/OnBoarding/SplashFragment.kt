package com.project.score.OnBoarding

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.R
import com.project.score.databinding.FragmentSplashBinding

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
            val nextFragment = OnboardingFragment()

            val transaction = onboardingActivity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView_onboarding, nextFragment)
//            transaction.addToBackStack("")
            transaction.commit()
        }, 3000)

        return binding.root
    }
}