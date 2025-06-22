package com.team.score.OnBoarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.team.score.API.TokenManager
import com.team.score.Login.LoginFragment
import com.team.score.R
import com.team.score.Utils.GlobalApplication
import com.team.score.Utils.GlobalApplication.Companion.firebaseAnalytics
import com.team.score.databinding.FragmentOnboardingBinding
import kotlin.math.min

class OnboardingFragment : Fragment() {

    lateinit var binding: FragmentOnboardingBinding
    lateinit var onboardingActivity: OnboardingActivity

    var totalPager = 4
    var currentItem = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboardingBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        binding.run {
            pager2.run{
                adapter = MainFragmentStateAdapter(onboardingActivity)

                dotsIndicator.setViewPager2(binding.pager2)
                buttonNext.setOnClickListener {
                    firebaseAnalytics.logEvent("click_next_splash", null)

                    currentItem = binding.pager2.currentItem

                    if (currentItem < totalPager - 1) {
                        // 현재 페이지가 마지막 페이지가 아니면 다음 페이지로 이동
                        pager2.setCurrentItem(currentItem + 1, true)
                    } else {
                        // 마지막 페이지면 버튼을 비활성화하거나 다른 작업 수행
                        TokenManager(onboardingActivity).saveEnter(false)

                        onboardingActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_onboarding, LoginFragment())
                            .commit()
                    }
                }
                buttonSkip.setOnClickListener {
                    firebaseAnalytics.logEvent("click_skip_splash", null)

                    TokenManager(onboardingActivity).saveEnter(false)

                    onboardingActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_onboarding, LoginFragment())
                        .commit()
                }

                // 페이지가 변경될 때 동작하는 method
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                    // position : 현재 보여진 페이지의 번호
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                    }
                })
            }
        }
        return binding.root
    }

    // ViewPager2의 어뎁터
    inner class MainFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        // 보여줄 page 수
        override fun getItemCount(): Int {
            return 4
        }

        // position번째 fragment 반환하여 보여준다.
        override fun createFragment(position: Int): Fragment {
            val resultFragment = when(position){
                0 -> Onboarding1Fragment()
                1 -> Onboarding2Fragment()
                2 -> Onboarding3Fragment()
                3 -> Onboarding4Fragment()
                else -> Onboarding1Fragment()
            }
            return resultFragment
        }
    }
}