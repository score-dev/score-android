package com.team.score.Group

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.team.score.databinding.DialogBasicBinding
import com.team.score.databinding.DialogEnterGroupBinding

interface EnterGroupDialogInterface {
    fun onClickRightButton()
}
class EnterGroupDialog(
    basicDialogInterface: EnterGroupDialogInterface,
    content: String?, leftButtonText: String, rightButtonText: String
) : DialogFragment() {

    private var _binding: DialogEnterGroupBinding? = null
    private val binding get() = _binding!!

    private var basicDialogInterface: EnterGroupDialogInterface? = null

    private var content: String? = null
    private var leftButtonText: String? = null
    private var rightButtonText: String? = null
    private var id: Int? = null

    init {
        this.content = content
        this.leftButtonText = leftButtonText
        this.rightButtonText = rightButtonText
        this.id = id
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
            editTextPassword.setHint(content)

            // 닫기 버튼
            buttonClose.setOnClickListener {
                dismiss()
            }

            // 왼쪽 버튼
            buttonLeft.run {
                text = leftButtonText
                dismiss()
            }
            // 오른쪽 버튼
            buttonRight.run {
                text = rightButtonText
                setOnClickListener {
                    this@EnterGroupDialog.basicDialogInterface?.onClickRightButton()
                    dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}