package com.project.score.Mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.score.MainActivity
import com.project.score.databinding.FragmentMypageMainBinding

class MypageMainFragment : Fragment() {

    private lateinit var binding: FragmentMypageMainBinding
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageMainBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        // 탭에 표시할 이름
        val tabName = arrayOf("피드", "캘린더")

        binding.run {

            pagerTab.bringToFront()
            toolbar.run {
                textViewHead.bringToFront()
                buttonSetting.bringToFront()
                buttonBack.bringToFront()
            }


        }

        return binding.root
    }

    // adapter 클래스
    inner class TabAdapterClass(
        fragmentActivity: FragmentActivity,
        private val fragments: List<Fragment> // Fragment 리스트를 전달받음
    ) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
