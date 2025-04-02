package com.project.score.SignUp.BottomSheet

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentSignUpGradeBottomSheetBinding
import com.project.score.databinding.FragmentSignUpSchoolBottomSheetBinding

interface SignUpGradeBottomSheetListener {
    fun onGradeSelected(grade: String)
}
class SignUpGradeBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var listener: SignUpGradeBottomSheetListener
    lateinit var binding: FragmentSignUpGradeBottomSheetBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as SignUpGradeBottomSheetListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpGradeBottomSheetBinding.inflate(inflater, container, false)

        binding.run {
            textViewGrade1.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.grade = 1
                MyApplication.userUpdateInfo?.userUpdateDto?.grade = 1
                onItemClicked(textViewGrade1.text.toString())
            }
            textViewGrade2.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.grade = 2
                MyApplication.userUpdateInfo?.userUpdateDto?.grade = 2
                onItemClicked(textViewGrade2.text.toString())
            }
            textViewGrade3.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.grade = 3
                MyApplication.userUpdateInfo?.userUpdateDto?.grade = 3
                onItemClicked(textViewGrade3.text.toString())
            }
        }

        return binding.root
    }

    private fun onItemClicked(grade: String) {
        listener.onGradeSelected(grade)
        dismiss()
    }
}