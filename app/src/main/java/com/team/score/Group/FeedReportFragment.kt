package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentFeedReportBinding

class FeedReportFragment : Fragment() {

    lateinit var binding: FragmentFeedReportBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(this)[GroupViewModel::class.java]
    }

    var selectedCauseIndex: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFeedReportBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val causeButtons = listOf(
            binding.buttonCause1,
            binding.buttonCause2,
            binding.buttonCause3,
            binding.buttonCause4,
            binding.buttonCause5,
            binding.buttonCause6
        )

        initView()
        textChangeListener(causeButtons)
        buttonChangeListener(causeButtons)

        binding.run {
            scrollView.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()
                false
            }

            buttonClear.setOnClickListener {
                editTextCause.setText("")
            }

            buttonReport.setOnClickListener {
                var reason: String? = null
                var comment = ""

                selectedCauseIndex?.let { index ->
                    reason = when (index) {
                        0 -> "민감한글이나사진"
                        1 -> "가학적이거나유해"
                        2 -> "불법촬영물"
                        3 -> "욕설비하혐오발언"
                        4 -> "광고"
                        5 -> "마음에들지않음"
                        else -> "기타"
                    }
                }

                if (binding.editTextCause.text.isNotEmpty()) {
                    reason = "기타"
                    comment = binding.editTextCause.text.toString()
                }

                // 피드 신고하기
                viewModel.reportFeed(mainActivity, arguments?.getInt("feedId") ?: 0, reason, comment)
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
                // 텍스트가 입력되어 있다면 초기화
                if (binding.editTextCause.text.isNotEmpty()) {
                    binding.editTextCause.setText("")
                }

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
                buttonReport.isEnabled = true
            } else {
                buttonReport.isEnabled = false
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
                textViewHead.text = "피드 신고하기"
            }
        }
    }

}