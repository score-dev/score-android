package com.team.score.Mypage.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.team.score.Utils.MyApplication
import com.team.score.databinding.DialogBasicBinding
import com.team.score.databinding.DialogProfileEidtBinding

interface ProfileEditDialogInterface {
    fun onClickRightButton()
    fun onClickLeftButton()
}
class ProfileEditDialog(
    profileEditDialogInterface: ProfileEditDialogInterface,
    schoolName: String, address: String
) : DialogFragment() {

    private var _binding: DialogProfileEidtBinding? = null
    private val binding get() = _binding!!

    private var profileEditDialogInterface: ProfileEditDialogInterface? = null

    private var schoolName: String? = null
    private var address: String? = null

    init {
        this.schoolName = schoolName
        this.address = address
        this.profileEditDialogInterface = profileEditDialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProfileEidtBinding.inflate(inflater, container, false)

        // 레이아웃 배경 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.run {

            textViewSchoolName.text = "${schoolName} "
            textViewSchoolAddress.text = "(${address})"

            // 취소 버튼
            buttonLeft.run {
                setOnClickListener {
                    this@ProfileEditDialog.profileEditDialogInterface?.onClickLeftButton()
                    dismiss()
                }
            }
            // 수정 버튼
            buttonRight.run {
                setOnClickListener {
                    this@ProfileEditDialog.profileEditDialogInterface?.onClickRightButton()
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