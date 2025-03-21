package com.project.score.Login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.project.score.BuildConfig
import com.project.score.Login.viewModel.UserViewModel
import com.project.score.OnBoarding.OnboardingActivity
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var onboardingActivity: OnboardingActivity
    lateinit var viewModel: UserViewModel

    // 카카오 로그인
    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.idToken}")
            MyApplication.signUpInfo?.userDto?.provider = "kakao"
            MyApplication.signUpInfo?.userDto?.idToken = token.idToken.toString()
            viewModel.checkOauth(onboardingActivity, "kakao", token.idToken.toString())
        }
    }

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            // ID 토큰
            account.idToken?.let {
                MyApplication.signUpInfo?.userDto?.provider = "google"
                MyApplication.signUpInfo?.userDto?.idToken = it.toString()
                viewModel.checkOauth(onboardingActivity,"google", it.toString())
            }
        } catch (e: ApiException) {
            Log.d("##", "google login fail: ${e}")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity
        viewModel = ViewModelProvider(onboardingActivity)[UserViewModel::class.java]

        binding.run {
            buttonKakao.setOnClickListener {
                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(onboardingActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(onboardingActivity) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡으로 로그인 실패", error)

                            // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }

                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(onboardingActivity, callback = callback)
                        } else if (token != null) {
                            Log.i("##", "카카오톡으로 로그인 성공 ${token.idToken}")
                            MyApplication.signUpInfo?.userDto?.provider = "kakao"
                            MyApplication.signUpInfo?.userDto?.idToken = token.idToken.toString()
                            viewModel.checkOauth(onboardingActivity, "kakao", token.idToken.toString())
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(onboardingActivity, callback = callback)
                }
            }
            buttonGoogle.setOnClickListener {
                requestGoogleLogin()
            }
        }

        return binding.root
    }

    private fun requestGoogleLogin() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/pubsub"))
            .requestServerAuthCode(BuildConfig.google_key) // 콘솔에서 가져온 client id 를 이용해 server authcode를 요청한다.
            .requestIdToken(BuildConfig.google_key) // 콘솔에서 가져온 client id 를 이용해 id token을 요청한다.
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOption)
    }
}