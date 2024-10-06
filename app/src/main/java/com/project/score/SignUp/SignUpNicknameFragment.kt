package com.project.score.SignUp

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.project.score.databinding.FragmentSignUpNicknameBinding

class SignUpNicknameFragment : Fragment() {

    lateinit var binding: FragmentSignUpNicknameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpNicknameBinding.inflate(layoutInflater)

        observeKeyboardState()
        textChangeListener()

        binding.run {
            buttonClearNickname.setOnClickListener {
                editTextNickname.setText("")
            }
        }

        return binding.root
    }

    fun textChangeListener() {
        binding.run {
            editTextNickname.addTextChangedListener {
                if(editTextNickname.text.isNotEmpty()){
                    buttonNext.isEnabled = true
                    buttonClearNickname.visibility = View.VISIBLE
                }
                else{
                    buttonNext.isEnabled = false
                    buttonClearNickname.visibility = View.INVISIBLE
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
}