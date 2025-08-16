package com.team.score.SignUp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import com.team.score.databinding.FragmentSignUpLinkBinding

class SignUpLinkFragment : Fragment() {

    lateinit var binding: FragmentSignUpLinkBinding
    lateinit var onboardingActivity: OnboardingActivity

    var isAlone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpLinkBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        binding.run {
            buttonKakao.setOnClickListener {
                // 카카오 초대
                shareToKakaoWithCustomTemplate(requireContext())
            }

            buttonLink.setOnClickListener {
                // 초대 링크 복사
            }

            buttonAlone.setOnClickListener {
                isAlone = !isAlone

                if(isAlone) {
                    buttonNext.isEnabled = true
                    buttonAlone.setBackgroundResource(R.drawable.background_sub3_radius8)
                    imageViewCheck.visibility = View.VISIBLE
                } else {
                    buttonNext.isEnabled = false
                    buttonAlone.setBackgroundResource(R.drawable.background_grey2_radius8)
                    imageViewCheck.visibility = View.GONE
                }
            }

            buttonNext.setOnClickListener {
                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpGenderFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initView()
    }

    fun initView() {
        binding.run {
            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun shareToKakaoWithCustomTemplate(context: Context) {
        val templateId = 123384L

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareCustom(context, templateId, null) { result, error ->
                if (error != null) {
                    Log.e("KakaoShare", "카카오톡 커스텀 템플릿 공유 실패", error)
                } else if (result != null) {
                    Log.d("KakaoShare", "공유 성공: ${result.intent.data}") // ✅ 딥링크 URI 로그
                    context.startActivity(result.intent)
                }
            }
        } else {
            WebSharerClient.instance.makeCustomUrl(templateId, null)?.let { url ->
                Log.d("KakaoShare", "웹 공유 URI: $url")
                context.startActivity(Intent(Intent.ACTION_VIEW, url))
            } ?: Log.e("KakaoShare", "웹 공유 URL 생성 실패")
        }
    }

}