package com.team.score.SignUp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.team.score.BasicDialog
import com.team.score.BasicDialogInterface
import com.team.score.Login.viewModel.UserViewModel
import com.team.score.MainActivity
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.SignUp.BottomSheet.SignUpGoalTimeBottomSheetFragment
import com.team.score.SignUp.BottomSheet.SignUpGoalTimeBottomSheetListener
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpGoalTimeBinding

class SignUpGoalTimeFragment : Fragment(), BasicDialogInterface, SignUpGoalTimeBottomSheetListener {

    lateinit var binding: FragmentSignUpGoalTimeBinding
    lateinit var onboardingActivity: OnboardingActivity
    lateinit var viewModel: UserViewModel

    var timeBottomSheet = SignUpGoalTimeBottomSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpGoalTimeBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity
        viewModel = ViewModelProvider(onboardingActivity)[UserViewModel::class.java]

        observeViewModel()

        binding.run {
            editTextGoalTime.setOnClickListener {
                timeBottomSheet.show(childFragmentManager, timeBottomSheet.tag)
            }

            buttonNext.setOnClickListener {
                MyApplication.signUpInfo?.userDto?.exercisingTime = true
                viewModel.signUp(onboardingActivity)
            }
        }

        return binding.root
    }

    fun observeViewModel() {
        viewModel.run {
            isSignUp.observe(viewLifecycleOwner) {
                setNotification()
            }
        }
    }

    fun setNotification() {
        // 다이얼로그
        val title = "스코어에서 \n알림을 보내도록 허용하시겠습니까?"
        val description = null

        val dialog = BasicDialog(this@SignUpGoalTimeFragment, title, description, "허용 안함", "허용", false)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let {
            dialog.show(it.supportFragmentManager, "NotificationDialog")
        }
    }

    fun moveToNextFragment() {
        viewModel.setFcmToken(onboardingActivity)

        fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val mainIntent = Intent(activity, MainActivity::class.java)
        mainIntent.putExtra("isLogin", true)
        onboardingActivity.startActivity(mainIntent)
    }

    override fun onClickRightButton() {
        moveToNextFragment()
    }

    override fun onClickLeftButton() {
        // 알림 허용 안함
        moveToNextFragment()
    }

    override fun onTimeSelected(time: String) {
        binding.run {
            editTextGoalTime.run {
                setText(time)
                setTextColor(resources.getColor(R.color.main))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTextAppearance(R.style.body1)
                setBackgroundResource(R.drawable.background_edittext_enabled)
            }
        }
    }
}