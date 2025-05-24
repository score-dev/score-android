package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentMateReportBinding

class MateReportFragment : Fragment() {

    lateinit var binding: FragmentMateReportBinding
    lateinit var mainActivity: MainActivity

    var selectedCauseIndex: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMateReportBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val causeButtons = listOf(
            binding.buttonCause1,
            binding.buttonCause2,
            binding.buttonCause3,
            binding.buttonCause4
        )

        initView()
        textChangeListener(causeButtons)
        buttonChangeListener(causeButtons)

        binding.run {
            buttonClear.setOnClickListener {
                editTextCause.setText("")
            }

            buttonWithdraw.setOnClickListener {
                val reasons = mutableListOf<String>()

                selectedCauseIndex?.let { index ->
                    val causeText = causeButtons[index].text.toString()
                    reasons.add(causeText)
                }

                if (editTextCause.text.isNotEmpty()) {
                    reasons.add(editTextCause.text.toString())
                }

                // 메이트 신고하기
            }
        }

        return binding.root
    }

    fun textChangeListener(causeButtons: List<Button>) {
        binding.editTextCause.addTextChangedListener {
            val isTextNotEmpty = binding.editTextCause.text.isNotEmpty()

            // 텍스트를 입력하면 기존 선택 해제
            if (isTextNotEmpty && selectedCauseIndex != null) {
                selectedCauseIndex = null

                // 버튼 선택 해제 UI
                causeButtons.forEach { btn ->
                    btn.backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.grey2)
                    btn.setTextColor(ContextCompat.getColor(mainActivity, R.color.text_color2))
                }
            }

            // 클리어 버튼 표시 여부
            binding.buttonClear.visibility = if (isTextNotEmpty) View.VISIBLE else View.INVISIBLE

            checkEnabled()
        }
    }


    fun buttonChangeListener(causeButtons: List<Button>) {
        causeButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                // 같은 버튼 누르면 선택 해제
                selectedCauseIndex = if (selectedCauseIndex == index) null else index

                // 전체 버튼 스타일 초기화
                causeButtons.forEachIndexed { i, btn ->
                    if (i == selectedCauseIndex) {
                        btn.backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.main)
                        btn.setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
                    } else {
                        btn.backgroundTintList = ContextCompat.getColorStateList(mainActivity, R.color.grey2)
                        btn.setTextColor(ContextCompat.getColor(mainActivity, R.color.text_color2))
                    }
                }

                checkEnabled()
            }
        }
    }


    fun checkEnabled() {
        binding.run {
            if(selectedCauseIndex != null || editTextCause.text.isNotEmpty()) {
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
                textViewHead.text = "메이트 신고하기"
            }
        }
    }

}