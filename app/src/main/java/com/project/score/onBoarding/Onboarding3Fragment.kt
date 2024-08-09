package com.project.score.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.R
import com.project.score.databinding.FragmentOnboarding3Binding

class Onboarding3Fragment : Fragment() {

    lateinit var binding: FragmentOnboarding3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding3Binding.inflate(layoutInflater)

        return binding.root
    }
}