package com.team.score.Utils

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.kakao.sdk.common.KakaoSdk
import com.team.score.BuildConfig

class GlobalApplication : Application() {
    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

        // Google Analytics 초기화
        firebaseAnalytics = Firebase.analytics
    }
}