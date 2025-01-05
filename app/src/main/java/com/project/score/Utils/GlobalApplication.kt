package com.project.score.Utils

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.project.score.BuildConfig

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }
}