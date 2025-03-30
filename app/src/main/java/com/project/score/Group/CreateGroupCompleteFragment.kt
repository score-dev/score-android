package com.project.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.databinding.FragmentCreateGroupCompleteBinding

class CreateGroupCompleteFragment : Fragment() {

    lateinit var binding: FragmentCreateGroupCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateGroupCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            toolbar.buttonClose.setOnClickListener {
                fragmentManager?.popBackStack()
            }

            buttonShare.setOnClickListener {
                // 카카오톡 공유
            }
        }

        return binding.root
    }
}