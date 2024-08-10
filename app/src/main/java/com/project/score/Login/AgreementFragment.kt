package com.project.score.Login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.R
import com.project.score.databinding.FragmentAgreementBinding

class AgreementFragment : Fragment() {

    lateinit var binding: FragmentAgreementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAgreementBinding.inflate(layoutInflater)

        return binding.root
    }
}