package com.team.score.Home

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
import com.team.score.databinding.DialogParticipateGroupAcceptBinding
import com.team.score.databinding.DialogParticipateGroupDenyBinding

interface ParticipateGroupAcceptDialogInterface {
    fun onClickRightButton(notificationId: Int, position: Int)
}
class ParticipateGroupAcceptDialog(
    basicDialogInterface: ParticipateGroupAcceptDialogInterface,
    notificationId: Int, userNickName: String, position: Int
) : DialogFragment() {

    private var _binding: DialogParticipateGroupAcceptBinding? = null
    private val binding get() = _binding!!


    private var basicDialogInterface: ParticipateGroupAcceptDialogInterface? = null

    private var notificationId: Int? = null
    private var userNickName: String? = null
    private var position: Int? = null

    init {
        this.notificationId = notificationId
        this.userNickName = userNickName
        this.position = position
        this.basicDialogInterface = basicDialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogParticipateGroupAcceptBinding.inflate(inflater, container, false)

        // 레이아웃 배경 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.run {
            textViewTitle.text = "00님이 00그룹의 메이트로\n함께 하게 되었어요!"

            // 닫기 버튼
            buttonClose.setOnClickListener {
                dismiss()
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