package com.project.score.Mypage.Setting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.databinding.FragmentWithdrawBinding

class WithdrawFragment : Fragment() {

    lateinit var binding: FragmentWithdrawBinding
    lateinit var mainActivity: MainActivity

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
        }

        return binding.root
    }

    fun textChangeListener() {
        binding.run {
            editTextCause.addTextChangedListener {
                checkCause()
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

                    checkCause()
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

                    checkCause()
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

                    checkCause()
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

                    checkCause()
                }
            }
        }
    }

    fun checkCause() {
        binding.run {
            if(isCause1Checked || isCause2Checked || isCause3Checked || isCause4Checked || editTextCause.text.isNotEmpty()) {
                buttonWithdraw.isEnabled = true
            } else {
                buttonWithdraw.isEnabled = false
            }
        }
    }

    fun initView() {
        binding.run {
            toolbar.run {
                buttonBack.setOnClickListener {

                }
                textViewHead.text = "탈퇴하기"
            }
        }
    }
}