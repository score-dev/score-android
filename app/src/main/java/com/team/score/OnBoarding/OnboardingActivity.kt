package com.team.score.OnBoarding

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.team.score.Utils.MyApplication
import com.team.score.Utils.PreferenceUtil
import com.team.score.databinding.ActivityOnboardingBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class OnboardingActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnboardingBinding
    lateinit var sharedPreferenceManager: PreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        MyApplication.preferences = PreferenceUtil(applicationContext)

        setFCMToken()

        setContentView(binding.root)
    }

    fun hideKeyboard() {
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocusView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            for (signature in info.signingInfo?.apkContentsSigners!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", keyHash)  // 키 해시를 로그로 출력
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash", "Unable to get MessageDigest. signature= $e")
        } catch (e: Exception) {
            Log.e("KeyHash", "Exception: $e")
        }
    }

    fun setFCMToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM Token", "$token")
            MyApplication.preferences.setFCMToken(token)
            Log.d("FCM Token", "FCM 토큰 : ${MyApplication.preferences.getFCMToken()}")

            if (this::sharedPreferenceManager.isInitialized) {
                Log.d("FCM Token", "this::sharedPreferenceManager.isInitialized")
                sharedPreferenceManager.setFCMToken(token)
            }
        }
    }
}