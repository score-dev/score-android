package com.team.score.Group

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.team.score.API.weather.response.Main
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.DialogBasicBinding
import com.team.score.databinding.DialogEnterGroupBinding

interface EnterGroupDialogInterface {
    fun onClickRightButton()
}
class EnterGroupDialog(
    basicDialogInterface: EnterGroupDialogInterface,
    content: String?, leftButtonText: String, rightButtonText: String, isPrivate: Boolean, groupId: Int
) : DialogFragment() {

    private var _binding: DialogEnterGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }
    private val mainActivity by lazy { requireActivity() as MainActivity }


    private var basicDialogInterface: EnterGroupDialogInterface? = null

    private var content: String? = null
    private var leftButtonText: String? = null
    private var rightButtonText: String? = null
    private var groupId: Int? = null
    private var isPrivate: Boolean? = null

    init {
        this.content = content
        this.leftButtonText = leftButtonText
        this.rightButtonText = rightButtonText
        this.groupId = groupId
        this.isPrivate = isPrivate
        this.basicDialogInterface = basicDialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEnterGroupBinding.inflate(inflater, container, false)

        // 레이아웃 배경 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.run {
            editTextPassword.run {
                isFocusable = isPrivate == true
                setHint(content)
                addTextChangedListener {
                    backgroundTintList = resources.getColorStateList(R.color.grey3)
                }
            }

            viewModel.isParticipated.observe(viewLifecycleOwner) {
                if(it == true) {
                    dismiss()
                }
            }

            viewModel.isValidPassword.observe(viewLifecycleOwner) {
                if(it == false) {
                    editTextPassword.run {
                        setHint("비밀 번호를 다시 입력해주세요*")
                        setHintTextColor(resources.getColor(R.color.red))
                        backgroundTintList = resources.getColorStateList(R.color.red)
                    }
                }
            }

            // 닫기 버튼
            buttonClose.setOnClickListener {
                dismiss()
            }

            // 왼쪽 버튼
            buttonLeft.run {
                text = leftButtonText
                setOnClickListener {
                    dismiss()
                }
            }

            // 오른쪽 버튼
            buttonRight.run {
                text = rightButtonText
                setOnClickListener {
                    if(isPrivate == true) {
                        viewModel.verifyGroupPassword(mainActivity, groupId ?: 0, editTextPassword.text.toString())
                    } else {
                        viewModel.participateGroup(mainActivity, groupId ?: 0)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.let { window ->
            val params = window.attributes
            val marginInPx = (17 * resources.displayMetrics.density).toInt()

            // 화면 전체 너비 - margin * 2
            val screenWidth = resources.displayMetrics.widthPixels
            params.width = screenWidth - marginInPx * 2
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            window.attributes = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}