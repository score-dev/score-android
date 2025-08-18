package com.team.score.Mypage.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.team.score.API.TokenManager
import com.team.score.MainActivity
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.R
import com.team.score.databinding.FragmentWithdrawBinding

class WithdrawFragment : Fragment() {

    lateinit var binding: FragmentWithdrawBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    var isCause1Checked = false
    var isCause2Checked = false
    var isCause3Checked = false
    var isCause4Checked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWithdrawBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initView()
        textChangeListener()
        buttonChangeListener()

        binding.run {
            buttonClear.setOnClickListener {
                editTextCause.setText("")
            }

            buttonWithdraw.setOnClickListener {
                val reasons = mutableListOf<String>()

                if (isCause1Checked) reasons.add(buttonCause1.text.toString())
                if (isCause2Checked) reasons.add(buttonCause2.text.toString())
                if (isCause3Checked) reasons.add(buttonCause3.text.toString())
                if (isCause4Checked) reasons.add(buttonCause4.text.toString())
                if (editTextCause.text.isNotEmpty()) reasons.add(editTextCause.text.toString())

                val result = reasons.joinToString(separator = ", ")

                viewModel.withdrawal(mainActivity, result) {

                    TokenManager(mainActivity).deleteAccessToken()
                    TokenManager(mainActivity).deleteRefreshToken()

                    mainActivity.finish()
                }
            }
        }

        return binding.root
    }

    fun textChangeListener() {
        binding.run {
            editTextCause.addTextChangedListener {
                checkEnabled()
                if(editTextCause.text.isNotEmpty()){
                    buttonClear.visibility = View.VISIBLE
                }
                else{
                    buttonClear.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun buttonChangeListener() {
        binding.run {
            buttonCause1.run {
                setOnClickListener {
                    isCause1Checked = !isCause1Checked

                    if(isCause1Checked) {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.main)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
                    } else {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.grey2)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.text_color2))
                    }

                    checkEnabled()
                }
            }
            buttonCause2.run {
                setOnClickListener {
                    isCause2Checked = !isCause2Checked

                    if(isCause2Checked) {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.main)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
                    } else {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.grey2)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.text_color2))
                    }

                    checkEnabled()
                }
            }
            buttonCause3.run {
                setOnClickListener {
                    isCause3Checked = !isCause3Checked

                    if(isCause3Checked) {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.main)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
                    } else {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.grey2)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.text_color2))
                    }

                    checkEnabled()
                }
            }
            buttonCause4.run {
                setOnClickListener {
                    isCause4Checked = !isCause4Checked

                    if(isCause4Checked) {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.main)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
                    } else {
                        backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.grey2)
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.text_color2))
                    }

                    checkEnabled()
                }
            }
        }
    }

    fun checkEnabled() {
        binding.run {
            if(isCause1Checked || isCause2Checked || isCause3Checked || isCause4Checked || editTextCause.text.isNotEmpty()) {
                buttonWithdraw.isEnabled = true
            } else {
                buttonWithdraw.isEnabled = false
            }
        }
    }

    fun initView() {

        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
                textViewHead.text = "탈퇴하기"
            }
        }
    }
}