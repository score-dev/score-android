package com.project.score.OnBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.databinding.FragmentOnboarding4Binding

class Onboarding4Fragment : Fragment() {

    lateinit var binding: FragmentOnboarding4Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding4Binding.inflate(layoutInflater)

        return binding.root
    }
}