package com.team.score.SignUp

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.team.score.Login.viewModel.UserViewModel
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpNicknameBinding

class SignUpNicknameFragment : Fragment() {

    lateinit var binding: FragmentSignUpNicknameBinding
    lateinit var onboardingActivity: OnboardingActivity
    lateinit var viewModel: UserViewModel

    val regex = Regex("^[가-힣a-zA-Z0-9]{1,20}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpNicknameBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity
        viewModel = ViewModelProvider(onboardingActivity)[UserViewModel::class.java]

        observeKeyboardState()
        textChangeListener()
        observeViewModel()

        binding.run {
            editTextNickname.setOnEditorActionListener { v, actionId, event ->
                if (regex.matches(editTextNickname.text.toString())) {
                    viewModel.checkNickName(onboardingActivity, editTextNickname.text.toString())
                }
                true
            }

            buttonClearNickname.setOnClickListener {
                editTextNickname.setText("")
            }

            buttonNext.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.nickname = editTextNickname.text.toString()
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpProfileFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun textChangeListener() {
        binding.run {
            editTextNickname.addTextChangedListener {
                buttonNext.isEnabled = false
                textViewNicknameAlert.visibility = View.GONE
                textViewNicknameExpression.visibility = View.VISIBLE

                if(editTextNickname.text.isNotEmpty()){
                    buttonClearNickname.visibility = View.VISIBLE
                }
                else{
                    buttonClearNickname.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.run {
            isUniqueNickName.observe(viewLifecycleOwner) {
                binding.run {
                    textViewNicknameAlert.visibility = View.VISIBLE
                    textViewNicknameExpression.visibility = View.GONE
                    if(it) {
                        textViewNicknameAlert.text = "사용 가능한 닉네임이에요"
                        buttonNext.isEnabled = true
                    } else {
                        textViewNicknameAlert.text = "이미 존재하는 닉네임이에요! 다른 닉네임을 입력해주세요"
                        buttonNext.isEnabled = false
                    }
                }
            }
        }
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
                binding.run {
                    // 버튼을 키보드 위로 이동
                    buttonNext.translationY =-keyboardHeight.toFloat()
                    textViewNicknameTitle.visibility = View.VISIBLE
                }
            } else {
                // 키보드가 내려감
                binding.run {
                    buttonNext.translationY = 20f
                    textViewNicknameTitle.visibility = View.GONE
                }
            }
        }
    }

    fun initView() {
        binding.run {
            root.setOnTouchListener { v, event ->
                onboardingActivity.hideKeyboard()
                false
            }

            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}