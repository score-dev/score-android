package com.project.score.SignUp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.score.API.request.signUp.SignUpRequest
import com.project.score.API.request.signUp.UserSchool
import com.project.score.MainActivity
import com.project.score.OnBoarding.OnboardingActivity
import com.project.score.R
import com.project.score.SignUp.BottomSheet.SignUpGradeBottomSheetFragment
import com.project.score.SignUp.BottomSheet.SignUpGradeBottomSheetListener
import com.project.score.SignUp.BottomSheet.SignUpSchoolBottomSheetFragment
import com.project.score.SignUp.BottomSheet.SignUpSchoolBottomSheetListener
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentSignUpSchoolBinding

class SignUpSchoolFragment : Fragment(), SignUpGradeBottomSheetListener, SignUpSchoolBottomSheetListener {

    lateinit var binding: FragmentSignUpSchoolBinding
    lateinit var onboardingActivity: OnboardingActivity

    val gradeBottomSheet = SignUpGradeBottomSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpSchoolBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        initView()

        binding.run {

            editTextSchoolname.setOnClickListener {
                val schoolBottomSheet = SignUpSchoolBottomSheetFragment(onboardingActivity)
                schoolBottomSheet.show(childFragmentManager, schoolBottomSheet.tag)
            }

            buttonClearSchoolname.setOnClickListener {
                editTextSchoolname.setText("")
                checkEnable()
            }

            editTextGrade.setOnClickListener {
                gradeBottomSheet.show(childFragmentManager, gradeBottomSheet.tag)
            }

            buttonClearGrade.setOnClickListener {
                editTextGrade.setText("")
                checkEnable()
            }

            buttonNext.setOnClickListener {
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpGenderFragment())
                    .addToBackStack(null)
                    .commit()
            }

        }

        return binding.root
    }

    fun initView() {
        binding.run {
            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun checkEnable() {
        binding.run {
            if(editTextGrade.text.isNotEmpty()) {
                buttonNext.isEnabled = true
            } else {
                buttonNext.isEnabled = false
            }
        }
    }

    // 학교 선택
    override fun onSchoolSelected(position: Int) {
        binding.run {
            editTextSchoolname.setText(MyApplication.signUpInfo?.schoolDto?.schoolName.toString())
            onboardingActivity.hideKeyboard()

            buttonClearSchoolname.visibility = View.VISIBLE
            checkEnable()
        }
    }

    // 학년 선택
    override fun onGradeSelected(grade: String) {
        binding.run {
            editTextGrade.setText(grade)
            buttonClearGrade.visibility = View.VISIBLE
            checkEnable()
        }
    }
}