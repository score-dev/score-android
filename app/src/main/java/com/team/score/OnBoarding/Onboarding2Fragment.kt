package com.team.score.OnBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.databinding.FragmentOnboarding2Binding

class Onboarding2Fragment : Fragment() {

    lateinit var binding: FragmentOnboarding2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding2Binding.inflate(layoutInflater)

        return binding.root
    }
}