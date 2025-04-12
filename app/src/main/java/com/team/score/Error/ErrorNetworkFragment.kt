package com.team.score.Error

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.databinding.FragmentErrorNetworkBinding

class ErrorNetworkFragment : Fragment() {

    lateinit var binding: FragmentErrorNetworkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentErrorNetworkBinding.inflate(layoutInflater)

        return binding.root
    }
}