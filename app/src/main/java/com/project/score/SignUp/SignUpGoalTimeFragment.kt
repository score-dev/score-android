package com.project.score.SignUp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity.CENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.user.model.User
import com.project.score.BasicDialog
import com.project.score.BasicDialogInterface
import com.project.score.Home.HomeFragment
import com.project.score.Login.viewModel.UserViewModel
import com.project.score.MainActivity
import com.project.score.Mypage.viewModel.MypageViewModel
import com.project.score.OnBoarding.OnboardingActivity
import com.project.score.R
import com.project.score.SignUp.BottomSheet.SignUpGoalTimeBottomSheetFragment
import com.project.score.SignUp.BottomSheet.SignUpGoalTimeBottomSheetListener
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentSignUpGoalTimeBinding

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

    override fun onClickRightButton() {
        // 알림 권한 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(
                onboardingActivity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            // 푸쉬 권한 없음 -> 권한 요청
            ActivityCompat.requestPermissions(
                onboardingActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                123
            )
        } else {
            // 이미 권한이 있는 경우 바로 화면 전환
            moveToNextFragment()
        }
    }

    // Fragment에서 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었을 때 화면 전환
                // FCM 토큰 전송
                moveToNextFragment()
            } else {
                onClickLeftButton()
            }
        }
    }

    fun moveToNextFragment() {
        viewModel.setFcmToken(onboardingActivity)

        fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val mainIntent = Intent(activity, MainActivity::class.java)
        mainIntent.putExtra("isLogin", true)
        onboardingActivity.startActivity(mainIntent)
    }

    override fun onClickLeftButton() {
        // 알림 허용 안함
        val mainIntent = Intent(activity, MainActivity::class.java)
        mainIntent.putExtra("isLogin", true)
        onboardingActivity.startActivity(mainIntent)
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