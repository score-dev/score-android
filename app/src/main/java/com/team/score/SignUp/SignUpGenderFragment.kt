package com.team.score.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpGenderBinding

class SignUpGenderFragment : Fragment() {

    lateinit var binding: FragmentSignUpGenderBinding
    lateinit var onboardingActivity: OnboardingActivity

    var gender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpGenderBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        initView()

        binding.run {
            buttonGenderFemale.setOnClickListener {
                buttonNext.visibility = View.VISIBLE
                when(gender) {
                    "MALE" -> {
                        buttonGenderMale.run {
                            backgroundTintList = resources.getColorStateList(R.color.grey2)
                            setTextColor(resources.getColor(R.color.text_color3))
                        }
                    }
                    "OTHER" -> {
                        buttonGenderOthers.run {
                            backgroundTintList = resources.getColorStateList(R.color.grey2)
                            setTextColor(resources.getColor(R.color.text_color3))
                        }
                    }
                }
                buttonGenderFemale.run {
                    backgroundTintList = resources.getColorStateList(R.color.main)
                    setTextColor(resources.getColor(R.color.white))
                }
                gender = "FEMALE"
            }

            buttonGenderMale.setOnClickListener {
                buttonNext.visibility = View.VISIBLE
                when(gender) {
                    "FEMALE" -> {
                        buttonGenderFemale.run {
                            backgroundTintList = resources.getColorStateList(R.color.grey2)
                            setTextColor(resources.getColor(R.color.text_color3))
                        }
                    }
                    "OTHER" -> {
                        buttonGenderOthers.run {
                            backgroundTintList = resources.getColorStateList(R.color.grey2)
                            setTextColor(resources.getColor(R.color.text_color3))
                        }
                    }
                }
                buttonGenderMale.run {
                    backgroundTintList = resources.getColorStateList(R.color.main)
                    setTextColor(resources.getColor(R.color.white))
                }
                gender = "MALE"
            }

            buttonGenderOthers.setOnClickListener {
                buttonNext.visibility = View.VISIBLE
                when(gender) {
                    "FEMALE" -> {
                        buttonGenderFemale.run {
                            backgroundTintList = resources.getColorStateList(R.color.grey2)
                            setTextColor(resources.getColor(R.color.text_color3))
                        }
                    }
                    "MALE" -> {
                        buttonGenderMale.run {
                            backgroundTintList = resources.getColorStateList(R.color.grey2)
                            setTextColor(resources.getColor(R.color.text_color3))
                        }
                    }
                }
                buttonGenderOthers.run {
                    backgroundTintList = resources.getColorStateList(R.color.main)
                    setTextColor(resources.getColor(R.color.white))
                }
                gender = "OTHER"
            }

            buttonSkip.setOnClickListener {
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpPhysicalFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonNext.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.gender = gender.toString()
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpPhysicalFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    fun initView() {
        binding.run {
            buttonNext.visibility = View.INVISIBLE

            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}