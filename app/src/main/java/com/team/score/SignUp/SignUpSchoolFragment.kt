package com.team.score.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.SignUp.BottomSheet.SignUpGradeBottomSheetFragment
import com.team.score.SignUp.BottomSheet.SignUpGradeBottomSheetListener
import com.team.score.SignUp.BottomSheet.SignUpSchoolBottomSheetFragment
import com.team.score.SignUp.BottomSheet.SignUpSchoolBottomSheetListener
import com.team.score.Utils.GlobalApplication.Companion.firebaseAnalytics
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpSchoolBinding

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
                firebaseAnalytics.logEvent("onboarding_school_name", null)

                val schoolBottomSheet = SignUpSchoolBottomSheetFragment(onboardingActivity)
                schoolBottomSheet.show(childFragmentManager, schoolBottomSheet.tag)
            }

            buttonClearSchoolname.setOnClickListener {
                editTextSchoolname.setText("")
                checkEnable()
            }

            editTextGrade.setOnClickListener {
                firebaseAnalytics.logEvent("onboarding_school_grade", null)

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