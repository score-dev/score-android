package com.project.score.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.R
import com.project.score.databinding.FragmentSignUpSchoolBinding

class SignUpSchoolFragment : Fragment() {

    lateinit var binding: FragmentSignUpSchoolBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpSchoolBinding.inflate(layoutInflater)

        binding.run {

        }

        return binding.root
    }
}