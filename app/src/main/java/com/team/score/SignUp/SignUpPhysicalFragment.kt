package com.team.score.SignUp

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpPhysicalBinding

class SignUpPhysicalFragment : Fragment() {

    lateinit var binding: FragmentSignUpPhysicalBinding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpPhysicalBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        initView()
        observeKeyboardState()

        binding.run {

            editTextPhysicalHeight.addTextChangedListener {
                checkEnable()
            }

            editTextPhysicalWeight.addTextChangedListener {
                checkEnable()
            }

            buttonNext.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.height = editTextPhysicalHeight.text.toString().toInt()
                MyApplication.signUpInfo?.userDto?.weight = editTextPhysicalWeight.text.toString().toInt()

                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpGoalTimeFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonSkip.setOnClickListener {
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpGoalTimeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    private fun observeKeyboardState() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            var originHeight = -1
            if ( binding.root.height > originHeight) {
                originHeight =  binding.root.height
            }

            val visibleFrameSize = Rect()
            binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)

            val visibleFrameHeight = visibleFrameSize.bottom - visibleFrameSize.top
            val keyboardHeight = originHeight - visibleFrameHeight

            if (keyboardHeight > visibleFrameHeight * 0.15) {
                // 키보드가 올라옴
                binding.buttonNext.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.buttonNext.translationY = + keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            }
        }
    }

    fun checkEnable() {
        binding.run {
            if(editTextPhysicalHeight.text.isNotEmpty() && editTextPhysicalWeight.text.isNotEmpty()) {
                buttonNext.visibility = View.VISIBLE
            } else {
                buttonNext.visibility = View.INVISIBLE
            }
        }
    }

    fun initView() {
        binding.run {
            root.setOnTouchListener { v, event ->
                onboardingActivity.hideKeyboard()
                false
            }

            buttonNext.visibility = View.INVISIBLE

            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}