import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
val kakaoNativeKey = properties.getProperty("kakao_app_key")

android {
    namespace = "com.team.score"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.team.score"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MAP_API_KEY", "\"${properties["map_client_id"]}\"")
        buildConfigField("String", "MAP_SECRET_KEY", "\"${properties["map_secret_key"]}\"")
        buildConfigField("String","server_url",getApiKey("server_url"))
        buildConfigField("String","google_key",getApiKey("google_key"))
        buildConfigField("String", "KAKAO_APP_KEY", "\"${properties["kakao_key"]}\"")
        buildConfigField("String","school_api_key",getApiKey("school_api_key"))
        buildConfigField("String","weather_api_key",getApiKey("weather_api_key"))

        manifestPlaceholders["kakao_native_key"] = kakaoNativeKey
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    dataBinding {
        enable = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

fun getApiKey(propertyKey:String) : String {
    return  gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // api
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON 변환
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // OkHttp 라이브러리
    implementation("com.squareup.okhttp3:logging-interceptor:3.11.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")
    // circle Image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // dot indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.21.2")
    // 카카오 공유
    implementation("com.kakao.sdk:v2-talk:2.21.2")
    implementation("com.kakao.sdk:v2-friend:2.21.2")
    implementation("com.kakao.sdk:v2-share:2.21.2")


    // 구글 로그인
    implementation("com.google.gms:google-services:4.3.15")
    implementation("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.firebase:firebase-auth")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // 네이버 지도
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.naver.maps:map-sdk:3.21.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.3")


    // dot indicator
    implementation("com.tbuonomo:dotsindicator:5.0")
}