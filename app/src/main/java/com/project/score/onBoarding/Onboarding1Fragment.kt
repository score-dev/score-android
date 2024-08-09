package com.project.score.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.R
import com.project.score.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : Fragment() {

    lateinit var binding: FragmentOnboarding1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding1Binding.inflate(layoutInflater)

        return binding.root
    }
}