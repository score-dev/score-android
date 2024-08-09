package com.project.score.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    lateinit var binding: FragmentOnboardingBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboardingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            pager2.run{
                adapter = MainFragmentStateAdapter(mainActivity)

                binding.run {
                    dotsIndicator.setViewPager2(binding.pager2)
                    buttonNext.setOnClickListener {

                    }
                }

                // LAYOUT_DIRECTION_RTL : 스크롤 방향 - 오른쪽
                // LAYOUT_DIRECTION_LTR : 스크롤 방향 - 왼쪽 (기본)
//                layoutDirection = ViewPager2.LAYOUT_DIRECTION_RTL

                // ORIENTATION_VERTICAL : 스크롤 방향 - 상하
//                orientation = ViewPager2.ORIENTATION_VERTICAL

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
                else -> Onboarding1Fragment()
            }
            return resultFragment
        }
    }
}