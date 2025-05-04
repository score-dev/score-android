package com.team.score.API.map

import android.content.Context
import com.google.gson.Gson
import com.team.score.API.neis.NeisApiService
import com.team.score.BuildConfig
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ReverseGeoCodingClient(val context: Context) {

    private val BASE_URL = "https://naveropenapi.apigw.ntruss.com/"

    val gson = Gson()

    val logger = HttpLoggingInterceptor().apply {
        level =
            HttpLoggingInterceptor.Level.BASIC
    }
    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.MAP_API_KEY)
            .addHeader("X-NCP-APIGW-API-KEY", BuildConfig.MAP_SECRET_KEY)
            .build()
        chain.proceed(newRequest)
    }

    // OkHttpClient에 TokenInterceptor를 추가
    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(logger)
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .build()

    val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient().build()) //SSL 우회
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService = retrofit.create(ReverseGeoCodingService::class.java)


    /**
     * Retrofit SSL 우회 접속 통신
     */
    fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { hostname, session -> true }
        builder.addInterceptor(authInterceptor)
        builder.addInterceptor(interceptor)
        builder.addInterceptor(logger)
        builder.cookieJar(JavaNetCookieJar(CookieManager()))
        builder.connectTimeout(100, TimeUnit.SECONDS)
        builder.readTimeout(100, TimeUnit.SECONDS)
        builder.writeTimeout(100, TimeUnit.SECONDS)

        return builder
    }
}