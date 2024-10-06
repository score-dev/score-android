package com.project.score.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.R
import com.project.score.databinding.FragmentSignUpProfileBinding

class SignUpProfileFragment : Fragment() {

    lateinit var binding: FragmentSignUpProfileBinding

    var profileImage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpProfileBinding.inflate(layoutInflater)

        initView()
        setProfileImage()

        binding.run {
            // 카메라 버튼 클릭 - 프로필 사진 업로드
            buttonCamera.setOnClickListener {

            }
        }

        return binding.root
    }

    fun setProfileImage() {
        binding.run {
            layoutProfile1.setOnClickListener {
                profileImage = 1
            }
            layoutProfile2.setOnClickListener {
                profileImage = 2
            }
            layoutProfile3.setOnClickListener {
                profileImage = 3
            }
            layoutProfile4.setOnClickListener {
                profileImage = 4
            }
            layoutProfile5.setOnClickListener {
                profileImage = 5
            }
        }
    }

    fun initView() {
        binding.run {
            toolbar.run {
                buttonBack.setOnClickListener {

                }
            }
        }
    }
}