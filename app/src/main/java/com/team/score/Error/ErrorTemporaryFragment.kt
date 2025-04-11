package com.team.score.Error

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.databinding.FragmentErrorTemporaryBinding

class ErrorTemporaryFragment : Fragment() {

    lateinit var binding: FragmentErrorTemporaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentErrorTemporaryBinding.inflate(layoutInflater)

        return binding.root
    }
}